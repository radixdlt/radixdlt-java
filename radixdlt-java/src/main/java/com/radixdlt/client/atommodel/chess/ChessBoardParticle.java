package com.radixdlt.client.atommodel.chess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.address.RadixUniverseConfig;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.client.core.crypto.ECKeyPair;
import com.radixdlt.client.core.crypto.ECPublicKey;
import org.radix.common.ID.EUID;
import org.radix.serialization2.DsonOutput;
import org.radix.serialization2.SerializerId2;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@SerializerId2("CHESSBOARDPARTICLE")
public final class ChessBoardParticle extends Particle {
    @JsonProperty("board")
    @DsonOutput(DsonOutput.Output.ALL)
    private String boardState;

    @JsonProperty("address")
    @DsonOutput(DsonOutput.Output.ALL)
    private RadixAddress gameAddress;

    @JsonProperty("whiteAddress")
    @DsonOutput(DsonOutput.Output.ALL)
    private RadixAddress whiteAddress;

    @JsonProperty("blackAddress")
    @DsonOutput(DsonOutput.Output.ALL)
    private RadixAddress blackAddress;

    @JsonProperty("gameUID")
    @DsonOutput(DsonOutput.Output.ALL)
    private EUID gameUID;

    @JsonProperty("lastMoveWhite")
    @DsonOutput(DsonOutput.Output.ALL)
    private boolean lastMoveWhite;

    @JsonProperty("nonce")
    @DsonOutput(DsonOutput.Output.ALL)
    private long nonce;

    private State gameState;

    @JsonProperty("gameState")
    @DsonOutput(value = {DsonOutput.Output.ALL})
    private String getJsonState() {
        return this.gameState.getName();
    }

    @JsonProperty("gameState")
    private void setJsonPermissions(String state) {
        this.gameState = State.from(state);
    }

    private ChessBoardParticle(RadixAddress gameAddress, RadixAddress whiteAddress, RadixAddress blackAddress, EUID gameUID) {
        super();
        this.gameAddress = Objects.requireNonNull(gameAddress, "gameAddress is required");
        this.whiteAddress = Objects.requireNonNull(whiteAddress, "whiteAddress is required");
        this.blackAddress = Objects.requireNonNull(blackAddress, "blackAddress is required");
        this.nonce = System.nanoTime();
        this.gameUID = gameUID;
        this.gameState = State.ACTIVE;
    }


//    public ChessBoardParticle makeMove(name.ulbricht.chess.game.Board nextBoardState) {
//
//    }

//    public void foo() {
//        GameContext gameContext = new GameContext(GameMode.HUMAN_VS_HUMAN, VariationType.NORMAL);
//        Board board = new Board(gameContext, true);
//        Square fromSquare = Square.valueOf("apa");
//        Square toSquare = Square.valueOf("apa");
//        Move move = new Move(fromSquare, toSquare);
//        board.doMove(move, true);
//    }

    public enum State {
        ACTIVE("active"),
        WHITE_WON("whiteWon"),
        BLACK_WON("blackWon"),
        DRAW("draw");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static State from(String name) {
            Map<String, State> statesByName = Arrays.stream(values())
                    .collect(Collectors.toMap(state -> state.name, state -> state));

            if (!statesByName.containsKey(name)) {
                throw new IllegalStateException("Unknown game state '" + name + "', ");
            }

            return statesByName.get(name);
        }
    }


}