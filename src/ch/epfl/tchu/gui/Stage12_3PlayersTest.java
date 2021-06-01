package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Random;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Stage12_3PlayersTest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        Map<PlayerId, String> names =
                Map.of(
                        PlayerId.PLAYER_1, "Ada",
                        PlayerId.PLAYER_2, "Charles",
                        PlayerId.PLAYER_3, "Mathis"
                );
        Map<PlayerId, Player> players =
                Map.of(
                        PlayerId.PLAYER_1, new GraphicalPlayerAdapter(),
                        PlayerId.PLAYER_2, new GraphicalPlayerAdapter(),
                        PlayerId.PLAYER_3, new GraphicalPlayerAdapter()
                );
        Random rng = new Random();
        new Thread(() -> Game.play(players, names, tickets, rng))
                .start();
    }
}
