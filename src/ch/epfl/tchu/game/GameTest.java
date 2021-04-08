package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class GameTest {
    @Test
    void playWorks() {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random();
        List<Route> allRoutes = ChMap.routes();
        List<String> names = List.of("Julien", "Edouard", "Bob");

        PlayerId.ALL.forEach(playerId -> {
            players.put(playerId, new TestPlayer(rng.nextLong(), allRoutes));
            playerNames.put(playerId, names.get(playerId.ordinal()));
        });

        Game.play(players, playerNames, tickets, rng);
    }
}
