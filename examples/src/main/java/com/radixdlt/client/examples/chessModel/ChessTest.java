package com.radixdlt.client.examples.chessModel;

import com.google.common.collect.ImmutableList;
import com.radixdlt.client.application.RadixApplicationAPI;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.application.translate.FeeMapper;
import com.radixdlt.client.application.translate.PowFeeMapper;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.atommodel.chess.ChessBoardParticle;
import com.radixdlt.client.atommodel.chess.ChessMoveParticle;
import com.radixdlt.client.core.Bootstrap;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.UnsignedAtom;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.client.core.ledger.AtomObservation;
import com.radixdlt.client.core.network.HttpClients;
import com.radixdlt.client.core.network.jsonrpc.RadixJsonRpcClient;
import com.radixdlt.client.core.network.websocket.WebSocketClient;
import com.radixdlt.client.core.network.websocket.WebSocketStatus;
import com.radixdlt.client.core.pow.ProofOfWorkBuilder;
import com.radixdlt.client.core.util.TestUtils;
import io.reactivex.Observable;
import okhttp3.Request;
import org.radix.common.ID.EUID;
import org.radix.crypto.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChessTest {
	private RadixUniverse universe = RadixUniverse.create(Bootstrap.LOCALHOST_SINGLENODE);
	private RadixIdentity identity;
	private FeeMapper feeMapper = new PowFeeMapper(Atom::getHash, new ProofOfWorkBuilder());
	private RadixJsonRpcClient jsonRpcClient;

	public static void main(String[] args) {
		ChessTest test = new ChessTest();
		test.setUp();

		RadixApplicationAPI gameApi = RadixApplicationAPI.create(Bootstrap.LOCALHOST_SINGLENODE, RadixIdentities.createNew());
		RadixApplicationAPI whiteApi = RadixApplicationAPI.create(Bootstrap.LOCALHOST_SINGLENODE, RadixIdentities.createNew());
		RadixApplicationAPI blackApi = RadixApplicationAPI.create(Bootstrap.LOCALHOST_SINGLENODE, RadixIdentities.createNew());

		RadixAddress gameAddress = gameApi.getMyAddress();
		RadixAddress whiteAddress = whiteApi.getMyAddress();
		RadixAddress blackADdress = blackApi.getMyAddress();
		EUID gameUID = new EUID(Hash.random().toByteArray());
		test.submitAtom(
			SpunParticle.up(new ChessBoardParticle(
				"test",
				gameAddress,
				whiteAddress,
				blackADdress,
				gameUID,
				ChessBoardParticle.State.INITIAL
			)),
			SpunParticle.up(new ChessMoveParticle(
				gameAddress,
				gameUID,
				""
			)))
			.subscribe(update -> System.out.println(update.getTimestamp() + " - " + update.getState()));

		test.universe.getLedger().getAtomPuller().pull(gameAddress)
			.filter(observation -> !observation.isHead())
			.scan(new ArrayList<Atom>(), (prev, observation) -> {
				if (observation.getType() == AtomObservation.Type.DELETE) {
					prev.remove(observation.getAtom());
				} else if (observation.getType() == AtomObservation.Type.STORE) {
					prev.add(observation.getAtom());
				}

				return prev;
			})
			.map(atoms -> atoms.stream()
				.flatMap(Atom::spunParticles)
				.filter(particle -> particle.getParticle() instanceof ChessBoardParticle)
				.map(particle -> ((SpunParticle<ChessBoardParticle>) particle))
				.collect(Collectors.groupingBy(SpunParticle<ChessBoardParticle>::getParticle)).entrySet().stream()
				.filter(boards -> boards.getValue().size() == 1)
				.map(Map.Entry::getKey)
				.findFirst()
			)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.subscribe(board -> System.out.println("Latest Chess Board: " + board.getBoardState() + " as of " + board.getHid()));
	}

	public void setUp() {
		this.identity = RadixIdentities.createNew();
		Request localhost = new Request.Builder().url("ws://localhost:8080/rpc").build();
		WebSocketClient webSocketClient = new WebSocketClient(listener -> HttpClients.getSslAllTrustingClient().newWebSocket(localhost, listener));
		webSocketClient.connect();
		webSocketClient.getState()
			.filter(WebSocketStatus.CONNECTED::equals)
			.blockingFirst();
		this.jsonRpcClient = new RadixJsonRpcClient(webSocketClient);
	}

	private Observable<RadixJsonRpcClient.NodeAtomSubmissionUpdate> submitAtom(SpunParticle<?>... spunParticles) {
		List<ParticleGroup> particleGroups = new ArrayList<>();
		particleGroups.add(ParticleGroup.of(ImmutableList.copyOf(spunParticles)));

		Map<String, String> atomMetaData = new HashMap<>();
		atomMetaData.put("timestamp", System.currentTimeMillis() + "");
		atomMetaData.putAll(feeMapper.map(new Atom(particleGroups, atomMetaData), universe, this.identity.getPublicKey()).getFirst());

		UnsignedAtom unsignedAtom = new UnsignedAtom(new Atom(particleGroups, atomMetaData));
		// Sign and submit
		Atom signedAtom = this.identity.sign(unsignedAtom).blockingGet();
		TestUtils.dumpJsonForHash(signedAtom);
		return jsonRpcClient.submitAtom(signedAtom);
	}
}
