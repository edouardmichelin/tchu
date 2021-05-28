package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Contient le programme principal du serveur tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class ServerMain extends Application {
    private final static String[] DEFAULT_NAMES = new String[]{"Ada", "Charles", "Alan"};

    /**
     * Lance l'application sur le file JavaFX
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Démarre le serveur
     *
     * @param primaryStage <code>Stage</code> principal
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> args = getParameters().getRaw();

        Map<PlayerId, String> names = new HashMap<>();
        Map<PlayerId, Player> players = new HashMap<>();

        ServerSocket s0 = new ServerSocket(5108);
        Socket socket = s0.accept();

        Player me = new GraphicalPlayerAdapter();

        players.put(PlayerId.PLAYER_1, me);

        for (PlayerId player : PlayerId.ALL) {
            int ordinal = player.ordinal();
            names.put(player, args.size() > ordinal ? args.get(ordinal) : DEFAULT_NAMES[ordinal]);

            if (ordinal == 0) continue;

            players.put(player, new RemotePlayerProxy(socket));
        }

        Random random = new Random();

        Thread game = new Thread(() -> Game.play(players, names, SortedBag.of(ChMap.tickets()), random));

        game.start();

    }
}
