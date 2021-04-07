package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.stream.Collectors;

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
        final GameState tmpGameState = currentGameState;
        playerInfos.forEach((k, v) -> announce(players, v.keptTickets(tmpGameState.playerState(k).ticketCount())));

        //Game loop including final turns WIP
        PlayerId currentPlayerId;
        Info currentPlayerInfo;
        Player currentPlayer;
        PlayerState currentPlayerState;

        int finalTurns = 0;
        boolean lastTurnBegins = false;
        Card tmpCard;
        int tmpInt;
        SortedBag<Card> tempCards;
        SortedBag<Ticket> tmpTicketChoice;
        SortedBag<Ticket> tmpChosenTickets;

        do {
            currentPlayerId = currentGameState.currentPlayerId();
            currentPlayerInfo = playerInfos.get(currentPlayerId);
            currentPlayer = players.get(currentPlayerId);
            currentPlayerState = currentGameState.playerState(currentPlayerId);

            if (lastTurnBegins)
                finalTurns++;

            announce(players, currentPlayerInfo.canPlay());

            //The player plays here
            switch (currentPlayer.nextTurn()) {
                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        tmpInt = currentPlayer.drawSlot();

                        if (Constants.FACE_UP_CARD_SLOTS.contains(tmpInt)) {
                            tmpCard = currentGameState.cardState().faceUpCard(tmpInt);
                            currentGameState = currentGameState.withDrawnFaceUpCard(tmpInt);
                            announce(players, currentPlayerInfo.drewVisibleCard(tmpCard));
                        } else if (tmpInt == Constants.DECK_SLOT) {
                            currentGameState = currentGameState.withBlindlyDrawnCard();
                            announce(players, currentPlayerInfo.drewBlindCard());
                        }
                        currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);
                    }
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

            //When the last turn begins
            if (currentGameState.lastTurnBegins() && !lastTurnBegins) {
                lastTurnBegins = true;
                announce(players, currentPlayerInfo.lastTurnBegins(currentPlayerState.carCount()));
            }

            currentGameState = currentGameState.forNextTurn();
        } while (finalTurns < players.size());

        final GameState gameOverState = currentGameState;

        //Game over and scoring under here

        //Map of scores without bonus
        Map<PlayerId, Integer> playerScores = new HashMap<>();
        players.forEach((k, v) -> playerScores.put(k, gameOverState.playerState(k).finalPoints()));

        //Map of the longest length from player routes
        Map<PlayerId, Integer> playerLongestTrail = new HashMap<>();
        players.forEach((k, v) -> playerLongestTrail.put(k,
                Trail.longest(gameOverState.playerState(k).routes()).length()));
    }

    private static void announce(Map<PlayerId, Player> players, String message) {
        players.forEach((k, v) -> v.receiveInfo(message));
    }
}
