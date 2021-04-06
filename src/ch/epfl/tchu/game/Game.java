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

        //Initialise the state of the game (playerstates are being created already there too)
        GameState currentGameState = GameState.initial(tickets, rng);

        //Initialise player infos
        Map<PlayerId, Info> playerInfos = new HashMap<>();
        for (PlayerId id : playerNames.keySet()) {
            playerInfos.put(id, new Info(playerNames.get(id)));
        }

        //first call for initPlayers to inform players of their identity
        players.forEach((k, v) -> v.initPlayers(k, playerNames));

        //Announce the first player to play
        announce(players, playerInfos.get(currentGameState.currentPlayerId()).willPlayFirst());

        //Show the players their initial ticket choice
        for (PlayerId id : players.keySet()) {
            SortedBag<Ticket> ticketChoice = currentGameState.topTickets(Constants.INITIAL_TICKETS_COUNT);

            currentGameState = currentGameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            players.get(id).setInitialTicketChoice(ticketChoice);
        }

        //Change the state of the game based on the chooseInitialTickets call of all players
        for (PlayerId id : players.keySet()) {
            currentGameState.withInitiallyChosenTickets(id, players.get(id).chooseInitialTickets());
        }

        //Announce how many tickets players have kept
        final GameState tempGameState = currentGameState;
        playerInfos.forEach((k, v) -> announce(players, v.keptTickets(tempGameState.playerState(k).ticketCount())));

        //Game loop including final turns WIP
        int finalTurns = 0;
        boolean lastTurnBegins = false;
        SortedBag<Card> tempCards;
        SortedBag<Ticket> tmpTicketChoice;
        SortedBag<Ticket> tmpChosenTickets;

        do {
            PlayerId currentPlayerId = currentGameState.currentPlayerId();
            Info currentPlayerInfo = playerInfos.get(currentPlayerId);
            Player currentPlayer = players.get(currentPlayerId);

            if (lastTurnBegins)
                finalTurns++;

            announce(players, currentPlayerInfo.canPlay());

            //The player plays here
            switch (currentPlayer.nextTurn()) {
                case DRAW_CARDS:
                    announce(players, currentPlayerInfo.drewAdditionalCards());
                    break;

                case CLAIM_ROUTE:
                    break;

                case DRAW_TICKETS:
                    announce(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    tmpTicketChoice = currentGameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    tmpChosenTickets = currentPlayer.chooseTickets(tmpTicketChoice);

                    currentGameState = currentGameState.withChosenAdditionalTickets(tmpTicketChoice, tmpChosenTickets);
                    announce(players, currentPlayerInfo.keptTickets(tmpChosenTickets.size()));
                    break;
            }


            if (currentGameState.lastTurnBegins() && !lastTurnBegins)
                lastTurnBegins = true;

            currentGameState = currentGameState.forNextTurn();
        } while (finalTurns < players.size());

        //Game over and scoring under here

    }

    private static void announce(Map<PlayerId, Player> players, String message) {
        players.forEach((k, v) -> v.receiveInfo(message));
    }
}
