package com.radixdlt.client.examples.chess;

import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.Bootstrap;
import com.radixdlt.client.core.RadixUniverse;
import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataExporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataImporter;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataTransferFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.TransferFormat;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.NetworkPlayer;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;

import java.util.ArrayList;

public class ChessApp extends JChessApp {
    private JChessView javaChessView;
    private Game game;
    private DataExporter fenExporter;
    private DataImporter fenImporter;
    private ChessGame radixChess;
    private MiniAtomSubmitter submitter;

    private static String radixAddressOfOtherPlayer;

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Not enough arguments, need to specify address");
        }

        launch(ChessApp.class, args);
        ChessApp.radixAddressOfOtherPlayer = args[0];
    }

    @Override
    protected void startup() {
        RadixUniverse universe = RadixUniverse.create(Bootstrap.LOCALHOST);
        RadixAddress addressOfOtherPlayer = RadixAddress.from(ChessApp.radixAddressOfOtherPlayer);
        RadixIdentity myIdentity = RadixIdentities.createNew();
        RadixAddress myAddress = universe.getAddressFrom(myIdentity.getPublicKey());
        this.submitter = new MiniAtomSubmitter(myIdentity);
        this.submitter.setUp();
        this.fenExporter = DataTransferFactory.getExporterInstance(TransferFormat.FEN);
        this.fenImporter = DataTransferFactory.getImporterInstance(TransferFormat.FEN);

        HumanPlayer selfPlayer = new HumanPlayer(myAddress.toString().substring(0, 5), Colors.WHITE);
        NetworkPlayer otherPlayer = new NetworkPlayer(addressOfOtherPlayer.toString().substring(0, 5), Colors.BLACK);

        this.game = new Game() {
            @Override
            public void nextMove() {
                super.nextMove();

                ArrayList<String> moves = this.moves.getMoves();
                if (!moves.isEmpty()) {
                    String move = moves.get(moves.size() - 1);
                    move = move.replace("-", "");
                    radixChess.makeMove(move, getBoardOnFEN());
                    System.out.println("MOVE " + move);
                }
            }
        };
        Settings gameSettings = new Settings(selfPlayer, otherPlayer);
        gameSettings.setGameType(GameTypes.LOCAL);
        this.game.setSettings(gameSettings);
        this.game.newGame();

        this.radixChess = ChessGame.create(myIdentity, addressOfOtherPlayer, spunParticles
            -> this.submitter.submitAtom(spunParticles).subscribe(), universe);
        this.radixChess.initialiseBoard(getBoardOnFEN());
        this.radixChess.chessboardParticles()
            .subscribe(board -> {
                if (this.radixChess.onBoardChanged(board)) {
                    System.out.println("Ledger board forced change to: '" + board.getBoardState() + "'");
                    this.setBoardFromFENString(board.getBoardState());
                } else {
                    System.out.println("Ledger updated board but already have latest");
                }
            });

        setupChessView();
    }

    private void setupChessView() {
        this.javaChessView = new JChessView(this);
        this.javaChessView.addNewTab(game);
        this.show(javaChessView);
    }

    private void setBoardFromFENString(String fenString) {
        try {
            if (!this.getBoardOnFEN().equals(fenString)) {
                this.game.importGame(fenString, fenImporter);
            }
        } catch (ReadGameError e) {
            System.err.println("Unable to import game from '" + fenString + "'");
            e.printStackTrace(System.err);
        }
    }

    public String getBoardOnFEN() {
        return this.game.exportGame(fenExporter);
    }
}
