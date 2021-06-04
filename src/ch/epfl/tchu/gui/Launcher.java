package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Launcher de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        JoinServerHandler onJoinServer = (hostName, playerType) -> {
            RemotePlayerClient player = new RemotePlayerClient(
                    playerType.equals(PlayerType.PLAYER) ?
                            new GraphicalPlayerAdapter() : new GraphicalSpectatorAdapter(),
                    hostName,
                    5108
            );

            Thread network = new Thread(player::run);

            network.start();
        };

        CreateServerHandler onCreateServer = (names, numberOfPlayers, numberOfSpectators) -> {
            Map<PlayerId, String> playerNames = new HashMap<>();
            Map<PlayerId, Player> players = new HashMap<>();
            Map<PlayerId, Player> spectators = new HashMap<>();

            var ALL_PLAYERS = PlayerId.ALL;
            var ALL_SPECTATORS = PlayerId.SPECTATORS;

            ServerSocket s0 = new ServerSocket(5108);
            Socket socket = s0.accept();

            Player me = new GraphicalPlayerAdapter();

            players.put(PlayerId.PLAYER_1, me);
            playerNames.put(PlayerId.PLAYER_1, names.get(0));

            for (int id = 1; id < numberOfPlayers; id++) {
                PlayerId playerId = ALL_PLAYERS.get(id);
                players.put(playerId, new RemotePlayerProxy(socket));
                playerNames.put(playerId, names.get(id));
            }

            for (int id = 0; id < numberOfSpectators; id++) {
                spectators.put(ALL_SPECTATORS.get(id), new RemotePlayerProxy(socket));
            }

            Random random = new Random();

            Thread game = new Thread(
                    () -> Game.play(players, spectators, playerNames, SortedBag.of(ChMap.tickets()), random)
            );

            game.start();
        };

        Scene launcherBox = LauncherViewCreator.createLauncherView(primaryStage, onCreateServer, onJoinServer);

        launcherBox.getStylesheets().add("launcher.css");

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setScene(launcherBox);
        primaryStage.setHeight(604);
        primaryStage.setWidth(604);


        primaryStage.show();
    }

    @FunctionalInterface
    interface JoinServerHandler {
        void onJoinServer(String hostName, PlayerType playerType);
    }

    @FunctionalInterface
    interface CreateServerHandler {
        void onCreateServer(List<String> names, int numberOfPlayers, int numberOfSpectators) throws IOException;
    }
}
