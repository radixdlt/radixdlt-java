package com.radixdlt.client.examples.chessModel;

import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.atommodel.chess.ChessBoardParticle;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.address.RadixUniverseConfig;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.crypto.ECKeyPair;
import com.radixdlt.client.core.crypto.ECPublicKey;
import com.radixdlt.client.core.network.RadixNode;
import com.radixdlt.client.core.network.actions.SubmitAtomAction;
import com.radixdlt.client.core.network.actions.SubmitAtomRequestAction;
import org.radix.common.ID.EUID;
import org.radix.crypto.Hash;

import java.util.Objects;

public class ChessGame {

    private String boardState;

    private RadixAddress gameAddress;

    private RadixAddress whiteAddress;

    private RadixAddress blackAddress;

    private EUID gameUID;

    private boolean lastMoveWhite = true;

    private ChessBoardParticle.State gameState;

    private RadixUniverse universe;

    private ChessGame(RadixUniverse universe, RadixAddress gameAddress, RadixAddress whiteAddress, RadixAddress blackAddress) {
        this.universe = universe;
        this.gameAddress = Objects.requireNonNull(gameAddress, "gameAddress is required");
        this.whiteAddress = Objects.requireNonNull(whiteAddress, "whiteAddress is required");
        this.blackAddress = Objects.requireNonNull(blackAddress, "blackAddress is required");
        this.gameUID = new EUID(Hash.random().toByteArray());
        this.gameState = ChessBoardParticle.State.ACTIVE;
    }

//    public SubmitAtomRequestAction makeMove() {
////        return new SubmitAtomRequestAction()
//    }

    public static ChessGame gameBetween(RadixUniverse universe, byte[] myPrivateKey, RadixAddress myAddress, RadixAddress otherPlayer) {
        ECKeyPair myself = new ECKeyPair(myPrivateKey);
        ECPublicKey otherPlayerPublicKey = otherPlayer.getPublicKey();
        ECPublicKey sharedKey = myself.diffieHellman(otherPlayerPublicKey);
        RadixAddress gameAddress = new RadixAddress(universe.getConfig(), sharedKey);
        return new ChessGame(universe, gameAddress, myAddress, otherPlayer);
    }
}
