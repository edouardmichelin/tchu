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
    private Game() { }

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
        update(players, currentGameState);

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
            players.get(id)
                    .setInitialTicketChoice(ticketChoice);
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
                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        tmpInt = currentPlayer.drawSlot();
                        currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);

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
                    SortedBag<Card> additionalChosenCards = SortedBag.of();

                    if (claimedRoute.level().equals(Route.Level.UNDERGROUND)) {
                        announce(players, currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

                        var builder = new SortedBag.Builder<Card>();

                        for (int iteration = 0; iteration < Constants.ADDITIONAL_TUNNEL_CARDS; iteration++) {                            currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);
                            builder.add(currentGameState.topCard());
                            currentGameState = currentGameState.withoutTopCard();
                            currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);
                        }

                        SortedBag<Card> drawnCards = builder.isEmpty() ? SortedBag.of() : builder.build();

                        int additionalCardsCount = claimedRoute
                                .additionalClaimCardsCount(initialClaimCards, drawnCards);

                        announce(players, currentPlayerInfo.drewAdditionalCards(drawnCards, additionalCardsCount));

                        if (additionalCardsCount >= 1) {
                            List<SortedBag<Card>> possibleAdditionalCards =
                                    currentPlayerState.possibleAdditionalCards(additionalCardsCount,
                                            initialClaimCards, drawnCards);

                            currentGameState = currentGameState.withMoreDiscardedCards(drawnCards);

                            update(players, currentGameState);

                            additionalChosenCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);

                            if (additionalChosenCards.isEmpty()) {
                                announce(players, currentPlayerInfo.didNotClaimRoute(claimedRoute));
                                break;
                            }
                        }
                    }

                    SortedBag<Card> claimCards = initialClaimCards.union(additionalChosenCards);

                    announce(players, currentPlayerInfo.claimedRoute(claimedRoute, claimCards));

                    currentGameState = currentGameState.withClaimedRoute(claimedRoute, claimCards);
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
                default:
                    throw new Error("Oh bah non");
            }

            // When the last turn begins
            if (currentGameState.lastTurnBegins() && !hasLastTurnBegun) {
                hasLastTurnBegun = true;
                announce(players, currentPlayerInfo.lastTurnBegins(currentPlayerState.carCount()));
            }

            currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);
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

        Set<Entry<PlayerId, Trail>> playerLongestTrailsSet = playersLongestTrails.entrySet();

        // Filter to the longest trail
        Optional<Entry<PlayerId, Trail>> maxLength = playerLongestTrailsSet
                .stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().length()));


        Map<PlayerId, Trail> playersForBonus = playerLongestTrailsSet
                .stream()
                .filter(entry -> entry.getValue().length() == (maxLength.get().getValue().length()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        // Add bonus to the right scores and announce bonus earning player(s)
        playersForBonus.forEach((k, v) -> {
            announce(players, playerInfos.get(k).getsLongestTrailBonus(v));
            playerScores.put(k, playerScores.get(k) + Constants.LONGEST_TRAIL_BONUS_POINTS);
        });

        Set<Entry<PlayerId, Integer>> playerScoresSet = playerScores
                .entrySet();

        // Filter scores to the highest one
        Entry<PlayerId, Integer> bestScore = playerScoresSet
                .stream()
                .max(Comparator.comparingInt(Entry::getValue))
                .orElseThrow();

        Map<PlayerId, Integer> winningPlayers = playerScoresSet
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
