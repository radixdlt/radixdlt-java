package com.radixdlt.client.examples.chess;

import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.atommodel.chess.ChessBoardParticle;
import com.radixdlt.client.atommodel.chess.ChessMoveParticle;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.atoms.particles.Spin;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.client.core.crypto.ECPublicKey;
import com.radixdlt.client.core.crypto.RadixECKeyPairs;
import com.radixdlt.client.core.ledger.AtomObservation;
import io.reactivex.Observable;
import org.radix.common.ID.EUID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ChessGame {
	private RadixAddress gameAddress;
	private RadixAddress myAddress;
	private RadixAddress otherAddress;
	private EUID gameUID;
	private ChessBoardParticle currentBoard;
	private RadixUniverse universe;
	private Consumer<List<SpunParticle<?>>> submitter;
	private final boolean creator;

	private ChessGame(RadixUniverse universe, RadixAddress gameAddress, RadixAddress myAddress, RadixAddress otherPlayer, EUID gameUID, Consumer<List<SpunParticle<?>>> submitter, boolean creator) {
		this.universe = universe;
		this.gameAddress = Objects.requireNonNull(gameAddress, "gameAddress is required");
		this.myAddress = Objects.requireNonNull(myAddress, "myAddress is required");
		this.otherAddress = Objects.requireNonNull(otherPlayer, "otherPlayer is required");
		this.gameUID = Objects.requireNonNull(gameUID);
		this.submitter = submitter;
		this.creator = creator;
	}

	public void makeMove(String move, String newBoard) {
		if (this.currentBoard == null) {
			throw new IllegalStateException("Cannot make move before board is set");
		}

		this.submitter.accept(Arrays.asList(
			SpunParticle.down(currentBoard),
			SpunParticle.up(onNext(new ChessBoardParticle(
				newBoard,
				gameAddress,
				getWhiteAddress(),
				getBlackAddress(),
				gameUID,
				ChessBoardParticle.State.ACTIVE, // TODO This is not correct!
				!currentBoard.isLastMoveWhite()))),
			SpunParticle.up(new ChessMoveParticle(
				gameAddress,
				gameUID,
				move
			))
		));
	}

	public void initialiseBoard(String board) {
		ChessBoardParticle initialBoard = new ChessBoardParticle(
			board,
			gameAddress,
			getWhiteAddress(),
			getBlackAddress(),
			gameUID,
			ChessBoardParticle.State.INITIAL,
			false);
		this.submitter.accept(Collections.singletonList(
			SpunParticle.up(onNext(initialBoard))
		));
	}

	private RadixAddress getWhiteAddress() {
		return creator ? myAddress : otherAddress;
	}

	private RadixAddress getBlackAddress() {
		return creator ? otherAddress : myAddress;
	}

	private ChessBoardParticle onNext(ChessBoardParticle nextState) {
		this.currentBoard = Objects.requireNonNull(nextState, "nextState is required");

		return nextState;
	}

	public boolean onBoardChanged(ChessBoardParticle board) {
		boolean changed = !Objects.equals(board, this.currentBoard);
		if (changed) {
			onNext(board);
		}
		System.out.println("Current: " + this.currentBoard.getBoardState());
		System.out.println("New: " + board.getBoardState());
		System.out.println("Is Different? " + changed);
		return changed;
	}

	public Observable<ChessBoardParticle> chessboardParticles() {
		return universe.getLedger().getAtomPuller().pull(gameAddress)
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
				.filter(particle -> particle.getParticle().getGameUID().equals(this.gameUID))
				.collect(Collectors.groupingBy(cb -> cb.getParticle().getBoardState())).entrySet().stream()
				.filter(boards -> boards.getValue().stream().noneMatch(sp -> sp.getSpin() == Spin.DOWN))
				.map(boardsWithUp -> boardsWithUp.getValue().stream().filter(sp -> sp.getSpin() == Spin.UP).findFirst().get().getParticle())
				.findFirst()
			)
			.filter(Optional::isPresent)
			.map(Optional::get);
	}

	public static ChessGame join(String gameName, RadixIdentity myIdentity, RadixAddress otherPlayer, Consumer<List<SpunParticle<?>>> submitter, RadixUniverse universe) {
		return create(gameName, myIdentity, otherPlayer, submitter, universe, false);
	}

	public static ChessGame create(String gameName, RadixIdentity myIdentity, RadixAddress otherPlayer, Consumer<List<SpunParticle<?>>> submitter, RadixUniverse universe) {
		return create(gameName, myIdentity, otherPlayer, submitter, universe, true);
	}

	private static ChessGame create(String gameName, RadixIdentity myIdentity, RadixAddress otherPlayer, Consumer<List<SpunParticle<?>>> submitter, RadixUniverse universe, boolean creator) {
		RadixAddress myAddress = universe.getAddressFrom(myIdentity.getPublicKey());
		String myAddressBase58 = myAddress.toString();
		String otherAddressBase58 = otherPlayer.toString();
		String gameSeed = creator ? (myAddressBase58 + otherAddressBase58) : (otherAddressBase58 + myAddressBase58);
		ECPublicKey gameKey = RadixECKeyPairs.newInstance()
			.generateKeyPairFromSeed(gameSeed.getBytes())
			.getPublicKey();
		Random rng = new Random();
		rng.setSeed(gameName.hashCode());
		byte[] gameUIDBytes = new byte[EUID.BYTES];
		rng.nextBytes(gameUIDBytes);
		EUID gameUID = new EUID(gameUIDBytes);
		RadixAddress gameAddress = universe.getAddressFrom(gameKey);

		return new ChessGame(universe, gameAddress, myAddress, otherPlayer, gameUID, submitter, creator);
	}
}
