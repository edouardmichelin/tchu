package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Partie de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Game {
    private static final int NUMBER_OF_PLAYERS = PlayerId.COUNT;

    // WIP
    public static void play(
            Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames,
            SortedBag<Ticket> tickets,
            Random rng
    ) {
        Preconditions.checkArgument(players.size() == NUMBER_OF_PLAYERS && playerNames.size() == NUMBER_OF_PLAYERS);

        // Initialize the state of the game (playerstates are being created already there too)
        GameState currentGameState = GameState.initial(tickets, rng);

        // Initialize player infos
        Map<PlayerId, Info> playerInfos = new HashMap<>();
        for (PlayerId id : playerNames.keySet()) {
            playerInfos.put(id, new Info(playerNames.get(id)));
        }

        // first call for initPlayers to inform players of their identity
        players.forEach((k, v) -> v.initPlayers(k, playerNames));

        // Announce the first player to play
        announce(players, playerInfos.get(currentGameState.currentPlayerId()).willPlayFirst());

        // Show the players their initial ticket choice
        for (PlayerId id : players.keySet()) {
            SortedBag<Ticket> ticketChoice = currentGameState.topTickets(Constants.INITIAL_TICKETS_COUNT);

            currentGameState = currentGameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            players.get(id).setInitialTicketChoice(ticketChoice);
        }

        update(players, currentGameState);

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
        boolean hasLastTurnBegun = false;
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

            if (hasLastTurnBegun)
                finalTurns++;

            update(players, currentGameState);
            announce(players, currentPlayerInfo.canPlay());

            // The player plays here
            switch (currentPlayer.nextTurn()) {
                default:
                    update(players, currentGameState);
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
                        update(players, currentGameState);
                    }
                    break;
                // yolo
                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();

                    // est ce que canClaimRoute a déja été appelé ??
                    boolean canPlayerClaimRoute = currentPlayerState.canClaimRoute(claimedRoute);

                    if (claimedRoute.level().equals(Route.Level.UNDERGROUND)) {
                        // est ce qu'il faut annoncer ici ??
                        announce(players, currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

                        // est ce que c'est ça la bonne condition ??
                        if (claimedRoute.possibleClaimCards().contains(initialClaimCards)) {
                            // quelque chose mais je sais pas trop quoi...
                        }
                    } else {
                        // est ce que le joueur s'est emparé de la route ici ??
                    }

                    update(players, currentGameState);

                    break;

                case DRAW_TICKETS:
                    announce(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    tmpTicketChoice = currentGameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    tmpChosenTickets = currentPlayer.chooseTickets(tmpTicketChoice);

                    currentGameState = currentGameState.withChosenAdditionalTickets(tmpTicketChoice, tmpChosenTickets);
                    announce(players, currentPlayerInfo.keptTickets(tmpChosenTickets.size()));
                    update(players, currentGameState);

                    break;
            }

            // When the last turn begins
            if (currentGameState.lastTurnBegins() && !hasLastTurnBegun) {
                hasLastTurnBegun = true;
                announce(players, currentPlayerInfo.lastTurnBegins(currentPlayerState.carCount()));
            }

            currentGameState = currentGameState.forNextTurn();
        } while (finalTurns < players.size());

        // Game over and scoring under here
        final GameState gameOverState = currentGameState;

        // Map of scores without bonus
        Map<PlayerId, Integer> playerScores = new HashMap<>();
        players.forEach((k, v) -> playerScores.put(k, gameOverState.playerState(k).finalPoints()));

        // Map of the longest length from player routes
        Map<PlayerId, Trail> playersLongestTrails = new HashMap<>();

        players
                .forEach((k, v) -> playersLongestTrails.put(k, Trail.longest(gameOverState.playerState(k).routes())));

        Stream<Entry<PlayerId, Trail>> playerLongestTrailsStream = playersLongestTrails
                .entrySet()
                .stream();

        // Filter to the longest trail
        Optional<Entry<PlayerId, Trail>> maxLength = playerLongestTrailsStream
                .max(Comparator.comparingInt(entry -> entry.getValue().length()));



        Map<PlayerId, Trail> playersForBonus = playerLongestTrailsStream
                .filter(entry -> entry.getValue().length() == (maxLength.get().getValue().length()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        // Add bonus to the right scores and announce bonus earning player(s)
        playersForBonus.forEach((k, v) -> {
            announce(players, playerInfos.get(k).getsLongestTrailBonus(v));
            playerScores.put(k, playerScores.get(k) + Constants.LONGEST_TRAIL_BONUS_POINTS);
        });

        // Filter scores to the highest one
        Entry<PlayerId, Integer> bestScore = playerScores
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Entry::getValue))
                .orElseThrow();

        Map<PlayerId, Integer> winningPlayers = playerScores
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(bestScore.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        update(players, currentGameState);

        // Final announcements results (it's designed for only 2 players here because of how Player works)
        if (winningPlayers.size() > 1) {
            List<String> winners = playerNames
                    .entrySet()
                    .stream()
                    .filter(entry -> winningPlayers.containsKey(entry.getKey()))
                    .map(Entry::getValue)
                    .collect(Collectors.toList());

            announce(players, Info.draw(winners, bestScore.getValue()));
        } else {
            int loserPoints = playerScores
                    .entrySet()
                    .stream()
                    .min(Comparator.comparingInt(Entry::getValue))
                    .orElseThrow()
                    .getValue();

            int winnerPoints = bestScore.getValue();

            announce(players, playerInfos.get(bestScore.getKey()).won(winnerPoints, loserPoints));
        }
    }

    private static void announce(Map<PlayerId, Player> players, String message) {
        players.forEach((k, v) -> v.receiveInfo(message));
    }

    private static void update(Map<PlayerId, Player> players, GameState gameState) {
        players.forEach((k, v) -> v.updateState(gameState, gameState.playerState(k)));
    }
}
