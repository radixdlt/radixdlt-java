package com.radixdlt.client.examples.chessModel;

import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.atommodel.chess.ChessBoardParticle;
import com.radixdlt.client.atommodel.chess.ChessMoveParticle;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.client.core.crypto.ECKeyPair;
import com.radixdlt.client.core.crypto.ECPublicKey;
import com.radixdlt.client.core.ledger.AtomObservation;
import io.reactivex.Observable;
import org.radix.common.ID.EUID;
import org.radix.common.tuples.Pair;
import org.radix.crypto.Hash;

import java.util.Objects;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChessGame {

    private String boardState;

    private RadixAddress gameAddress;

    private RadixAddress whiteAddress;

    private RadixAddress blackAddress;

    private EUID gameUID;

    private boolean lastMoveWhite = true;

    private ChessBoardParticle.State gameState;

    private RadixUniverse universe;
    private ECKeyPair myKeys;

    private ChessGame(RadixUniverse universe, RadixAddress gameAddress, ECKeyPair myKeys, RadixAddress otherPlayer) {
        this.universe = universe;
        this.gameAddress = Objects.requireNonNull(gameAddress, "gameAddress is required");
        RadixAddress myAddress = new RadixAddress(universe.getConfig(), myKeys.getPublicKey());
        this.whiteAddress = Objects.requireNonNull(myAddress, "myAddress was null");
        this.blackAddress = Objects.requireNonNull(otherPlayer, "otherPlayer is required");
        this.gameUID = new EUID(Hash.random().toByteArray());
        this.gameState = ChessBoardParticle.State.ACTIVE;
        this.myKeys = myKeys;
    }

    public Observable<List<SpunParticle>> particlesForMove(String move) {
        // ChessBoardParticle.newGame(gameAddress, whiteAddress, blackAddress, gameUID)
        return chessboardParticles().map(prev -> {
            return Pair.of(prev, ChessBoardParticle.fromPrevious(prev, move));
        }).map(prevAndNewBoard -> Arrays.asList(
                SpunParticle.down(prevAndNewBoard.getFirst()),
                SpunParticle.up(prevAndNewBoard.getSecond()),
                SpunParticle.up(ChessMoveParticle.move(prevAndNewBoard.getSecond(), move))
        ));
    }

    private Observable<ChessBoardParticle> chessboardParticles() {
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
                        .collect(Collectors.groupingBy(SpunParticle<ChessBoardParticle>::getParticle)).entrySet().stream()
                        .filter(boards -> boards.getValue().size() == 1)
                        .map(Map.Entry::getKey)
                        .findFirst()
                )
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public static ChessGame gameBetween(RadixUniverse universe, ECKeyPair myKeys, RadixAddress otherPlayer) {
        ECKeyPair myself = new ECKeyPair(myKeys.getPrivateKey());
        ECPublicKey otherPlayerPublicKey = otherPlayer.getPublicKey();
        ECPublicKey sharedKey = myself.diffieHellman(otherPlayerPublicKey);
        RadixAddress gameAddress = new RadixAddress(universe.getConfig(), sharedKey);

        return new ChessGame(universe, gameAddress, myKeys, otherPlayer);
    }
}
