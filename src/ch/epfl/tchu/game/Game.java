package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

/**
 * Partie de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Game {
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

    }
}
