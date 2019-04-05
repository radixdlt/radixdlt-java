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
import pl.art.lach.mateusz.javaopenchess.core.moves.Move;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
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

	private static boolean joining;
    private static String myName;
    private static String otherName;
    private static String gameName;

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Not enough arguments, need to specify myname othername gamename [joining]");
        }

        launch(ChessApp.class, args);
        ChessApp.myName = args[0];
        ChessApp.otherName = args[1];
        ChessApp.gameName = args[2];
        ChessApp.joining = args.length > 3;
    }

    @Override
    protected void startup() {
        RadixUniverse universe = RadixUniverse.create(Bootstrap.LOCALHOST);
        RadixAddress otherAddress = universe.getAddressFrom(KeyUtils.fromSeed(otherName).getPublicKey());
        RadixIdentity myIdentity = RadixIdentities.from(KeyUtils.fromSeed(myName));
        RadixAddress myAddress = universe.getAddressFrom(myIdentity.getPublicKey());
        this.submitter = new MiniAtomSubmitter(myIdentity);
        this.submitter.setUp();
        this.fenExporter = DataTransferFactory.getExporterInstance(TransferFormat.FEN);
        this.fenImporter = DataTransferFactory.getImporterInstance(TransferFormat.FEN);

        System.out.println(joining ? "JOINING" : "CREATING");
        System.out.println("Me: " + myAddress.toString());
        System.out.println("Other: " + otherAddress.toString());
        System.out.println("Game: " + gameName);

        HumanPlayer myPlayer = new HumanPlayer(myName, joining ? Colors.BLACK : Colors.WHITE);
        NetworkPlayer otherPlayer = new NetworkPlayer(otherName, joining ? Colors.WHITE : Colors.BLACK);

        this.game = new Game() {
            @Override
            public void nextMove() {
                this.updateFenStateText();

                Move lastMove = game.getMoves().getLastMoveFromHistory();
                if (lastMove != null) {
	                String moveStr = lastMove.getFrom().getAlgebraicNotation() + lastMove.getTo().getAlgebraicNotation();
	                radixChess.makeMove(moveStr, getBoardOnFEN());
                    System.out.println("MOVE " + moveStr);
                }
            }
        };
        this.game.newGame();
        Settings gameSettings = joining ? new Settings(otherPlayer, myPlayer) : new Settings(myPlayer, otherPlayer);
        if (joining) {
            gameSettings.setUpsideDown(false);
            myPlayer.setGoDown(true);
            otherPlayer.setGoDown(false);
        }
        gameSettings.setGameType(GameTypes.LOCAL);
        this.game.setSettings(gameSettings);
        this.game.getGameClock().setVisible(false);
        this.game.getGameClock().setEnabled(false);
        this.game.getChat().setVisible(false);
        this.game.getChat().setEnabled(false);

        this.radixChess = joining ?
	        ChessGame.join(gameName, myIdentity, otherAddress, spunParticles
		        -> this.submitter.submitAtom(spunParticles).subscribe(), universe) :
	        ChessGame.create(gameName, myIdentity, otherAddress, spunParticles
            -> this.submitter.submitAtom(spunParticles).subscribe(), universe);
        if (!joining) {
	        this.radixChess.initialiseBoard(getBoardOnFEN());
        }
        this.radixChess.chessboardParticles()
            .subscribe(board -> {
                if (this.radixChess.onBoardChanged(board)) {
                    System.out.println("Ledger board forced change to: '" + board.getBoardState() + "'");
                    this.setBoardFromFENString(board.getBoardState());

                    boolean amNext = board.isLastMoveWhite() && joining || !board.isLastMoveWhite() && !joining;
                    Player activePlayer;
                    if (amNext) {
                        activePlayer = myPlayer;
                    } else {
                        activePlayer = otherPlayer;
                    }
                    game.setActivePlayer(activePlayer);
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
