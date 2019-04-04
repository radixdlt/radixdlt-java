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
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.DataTransferFactory;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.TransferFormat;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;

public class ChessApp extends JChessApp {

    private JChessView javaChessView;
    private Game game;
    private DataExporter fenExporter;
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
        this.whitePlayer = new HumanPlayer(this.myRadixAddress.toString(), Colors.WHITE.getColorName());
        this.blackPlayer = new HumanPlayer(this.addressOfOtherPlayer.toString(), Colors.BLACK.getColorName());
        Settings gameSettings = new Settings(whitePlayer, blackPlayer);
        //gameSettings.setGameType(GameTypes.NETWORK);
        gameSettings.setGameType(GameTypes.LOCAL);
        this.game.setSettings(gameSettings);
        this.game.newGame();
        this.fenExporter = DataTransferFactory.getExporterInstance(TransferFormat.FEN);
    }

    public String getBoardOnFEN() {
        return this.game.exportGame(fenExporter);
    }
}
