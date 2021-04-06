package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * Partie de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Game {
    //WIP
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        GameState currentGameState = GameState.initial(tickets, rng);

        //Initialise player infos
        Map<PlayerId, Info> playerInfos = new HashMap<>();
        for (PlayerId id : playerNames.keySet()) {
            playerInfos.put(id, new Info(playerNames.get(id)));
        }

        //first call for initPlayers
        players.forEach((k, v) -> v.initPlayers(k, playerNames));

        //Choose the first player at random and announce
        List<PlayerId> PlayerIds = new ArrayList<>(players.keySet());
        announce(players, playerInfos.get(PlayerIds.get(rng.nextInt(players.size()))).willPlayFirst());

        //set initial tickets for everyone
        for (PlayerId id : players.keySet()) {
            players.get(id).setInitialTicketChoice(currentGameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            currentGameState = currentGameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

    }

    private static void announce(Map<PlayerId, Player> players, String message) {
        players.forEach((k, v) -> v.receiveInfo(message));
    }
}
