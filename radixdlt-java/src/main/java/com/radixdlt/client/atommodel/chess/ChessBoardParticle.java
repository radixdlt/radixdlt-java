package com.radixdlt.client.atommodel.chess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.atoms.particles.Particle;
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

    public RadixAddress getGameAddress() {
        return gameAddress;
    }

    @JsonProperty("gameAddress")
    @DsonOutput(DsonOutput.Output.ALL)
    private RadixAddress gameAddress;

    @JsonProperty("whiteAddress")
    @DsonOutput(DsonOutput.Output.ALL)
    private RadixAddress whiteAddress;

    @JsonProperty("blackAddress")
    @DsonOutput(DsonOutput.Output.ALL)
    private RadixAddress blackAddress;

    public EUID getGameUID() {
        return gameUID;
    }

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

    private ChessBoardParticle() {
    }

    public ChessBoardParticle(String boardState, RadixAddress gameAddress, RadixAddress whiteAddress, RadixAddress blackAddress, EUID gameUID, State gameState) {
        super();

        this.boardState = Objects.requireNonNull(boardState);
        this.gameAddress = Objects.requireNonNull(gameAddress, "gameAddress is required");
        this.whiteAddress = Objects.requireNonNull(whiteAddress, "whiteAddress is required");
        this.blackAddress = Objects.requireNonNull(blackAddress, "blackAddress is required");
        this.nonce = System.nanoTime();
        this.gameUID = Objects.requireNonNull(gameUID, "gameUID is required");
        this.gameState = State.ACTIVE;
    }

    public static ChessBoardParticle newGame(RadixAddress gameAddress, RadixAddress whiteAddress, RadixAddress blackAddress, EUID gameUID) {
        return new ChessBoardParticle(gameAddress, whiteAddress, blackAddress, gameUID, "");
    }

    public static ChessBoardParticle fromPrevious(ChessBoardParticle prev, String move) {
        return new ChessBoardParticle(prev.boardState, prev.gameAddress, prev.whiteAddress, prev.blackAddress, prev.gameUID, move);
    }


    public String getBoardState() {
        return boardState;
    }

    public enum State {
        INITIAL("initial"),
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