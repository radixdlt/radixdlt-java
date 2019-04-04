package com.radixdlt.client.examples.chessGUI;

import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.Bootstrap;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.crypto.ECKeyPair;
import com.radixdlt.client.core.crypto.ECKeyPairGenerator;
import com.radixdlt.client.examples.chessModel.ChessGame;
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
import pl.art.lach.mateusz.javaopenchess.network.Client;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;

public class ChessApp extends JChessApp {

    private JChessView javaChessView;
    private Game game;
    private DataExporter fenExporter;
    private DataImporter fenImporter;
    private HumanPlayer whitePlayer;
    private HumanPlayer blackPlayer;
    private ChessGame radixGame;
    private ECKeyPair myKeys;
    private RadixAddress myRadixAddress;
    private RadixAddress addressOfOtherPlayer;
    private RadixUniverse universe;

    private static String radixAddressOfOtherPlayer;

    public static void main(String[] args) {
        launch(ChessApp.class, args);
        ChessApp.radixAddressOfOtherPlayer = args[0];
    }

    @Override
    protected void startup() {
        this.universe = RadixUniverse.create(Bootstrap.LOCALHOST);
        this.addressOfOtherPlayer = RadixAddress.from(ChessApp.radixAddressOfOtherPlayer);
        this.myKeys = ECKeyPairGenerator.newInstance().generateKeyPair();
        this.myRadixAddress = new RadixAddress(this.universe.getConfig(), this.myKeys.getPublicKey());
        this.radixGame = ChessGame.gameBetween(this.universe, myKeys, addressOfOtherPlayer);

        this.javaChessView = new JChessView(this);
        this.show(javaChessView);
        this.game = javaChessView.addNewTab("The game");
        Client client = new Client("127.0.0.1", 12345);
        this.game.setClient(client);
        this.whitePlayer = new HumanPlayer(this.myRadixAddress.toString(), Colors.WHITE.getColorName());
        this.blackPlayer = new HumanPlayer(this.addressOfOtherPlayer.toString(), Colors.BLACK.getColorName());
        Settings gameSettings = new Settings(whitePlayer, blackPlayer);
        gameSettings.setGameType(GameTypes.NETWORK);
//        gameSettings.setGameType(GameTypes.LOCAL);
        this.game.setSettings(gameSettings);

        this.fenExporter = DataTransferFactory.getExporterInstance(TransferFormat.FEN);
        this.fenImporter = DataTransferFactory.getImporterInstance(TransferFormat.FEN);
        this.game.newGame();
//        setBoardFromFENString("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
//        System.out.println(getBoardOnFEN());
    }


    private void setBoardFromFENString(String fenString) {
        try {
            this.game.importGame(fenString, fenImporter);
        } catch (ReadGameError readGameError) {
            readGameError.printStackTrace();
        }
    }

        /*
        *     public void newGame() {
        this.getChessboard().setPieces4NewGame(this.getSettings().getPlayerWhite(), this.getSettings().getPlayerBlack());
        this.activePlayer = this.getSettings().getPlayerWhite();
        if (this.activePlayer.getPlayerType() != PlayerType.LOCAL_USER) {
            this.setBlockedChessboard(true);
        }

        this.runRenderingArtifactDirtyFix();
        this.updateFenStateText();
    }
        * */


    public String getBoardOnFEN() {
        return this.game.exportGame(fenExporter);
    }
}
