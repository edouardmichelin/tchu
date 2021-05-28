package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Partie de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Game {
    private static final int NUMBER_OF_PLAYERS = PlayerId.COUNT;

    private Game() {
    }

    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table <i>playerNames</i> ; les
     * billets disponibles pour cette partie sont ceux de <i>tickets</i>, et le générateur pseudo aléatoire
     * <i>rng</i> est utilisé pour créer l'état initial du jeu et pour mélanger les cartes de la défausse pour en
     * faire une nouvelle pioche quand cela est nécessaire ; lève <i>IllegalArgumentException</i> si l'une des deux
     * tables associatives
     * a une taille différente de <i>2</i>
     *
     * @param players     les joueurs qui vont jouer la partie
     * @param playerNames les noms des joueurs de <code>players</code>
     * @param tickets     les billets disponibles pour la partie
     * @param rng         le générateur pseudo aléatoire
     * @throws IllegalArgumentException si l'une des deux tables associatives a une taille différente de <code>2</code>
     */
    public static void play(
            Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames,
            SortedBag<Ticket> tickets,
            Random rng
    ) {
        Preconditions.checkArgument(players.size() == NUMBER_OF_PLAYERS);
        Preconditions.checkArgument(playerNames.size() == NUMBER_OF_PLAYERS);

        // region init
        // Initialize the state of the game (playerstates are being created already there too)
        GameState currentGameState = GameState.initial(tickets, rng);

        // Initialize player infos
        Map<PlayerId, Info> playerInfos = new HashMap<>();
        for (Entry<PlayerId, String> entry : playerNames.entrySet()) {
            playerInfos.put(entry.getKey(), new Info(entry.getValue()));
        }

        // first call for initPlayers to inform players of their identity
        players.forEach((k, v) -> v.initPlayers(k, playerNames));

        // Announce the first player to play
        announce(players, playerInfos.get(currentGameState.currentPlayerId()).willPlayFirst());

        // Show the players their initial ticket choice
        for (Entry<PlayerId, Player> player : players.entrySet()) {
            SortedBag<Ticket> ticketChoice = currentGameState.topTickets(Constants.INITIAL_TICKETS_COUNT);

            currentGameState = currentGameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            player.getValue().setInitialTicketChoice(ticketChoice);
        }

        update(players, currentGameState);

        //Change the state of the game based on the chooseInitialTickets call of all players
        for (Entry<PlayerId, Player> player : players.entrySet()) {
            currentGameState.withInitiallyChosenTickets(player.getKey(), player.getValue().chooseInitialTickets());
        }

        //Announce how many tickets players have kept
        final GameState tmpGameState = currentGameState;
        playerInfos.forEach((k, v) -> announce(players, v.keptTickets(tmpGameState.playerState(k).ticketCount())));

        // endregion

        // region flow
        PlayerId currentPlayerId;
        Info currentPlayerInfo;
        Player currentPlayer;
        PlayerState currentPlayerState;

        int finalTurnsAmount = 0;
        boolean hasLastTurnBegun = false;

        do {
            currentPlayerId = currentGameState.currentPlayerId();
            currentPlayerInfo = playerInfos.get(currentPlayerId);
            currentPlayer = players.get(currentPlayerId);
            currentPlayerState = currentGameState.playerState(currentPlayerId);

            if (hasLastTurnBegun)
                finalTurnsAmount++;

            update(players, currentGameState);
            announce(players, currentPlayerInfo.canPlay());

            // The player plays here
            switch (currentPlayer.nextTurn()) {
                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        int slotDrawn = currentPlayer.drawSlot();
                        currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);

                        if (Constants.FACE_UP_CARD_SLOTS.contains(slotDrawn)) {
                            Card card = currentGameState.cardState().faceUpCard(slotDrawn);
                            currentGameState = currentGameState.withDrawnFaceUpCard(slotDrawn);
                            announce(players, currentPlayerInfo.drewVisibleCard(card));
                        } else if (slotDrawn == Constants.DECK_SLOT) {
                            currentGameState = currentGameState.withBlindlyDrawnCard();
                            announce(players, currentPlayerInfo.drewBlindCard());
                        }
                        currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);
                        if (i == 0) update(players, currentGameState);
                    }
                    break;

                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                    SortedBag<Card> additionalChosenCards = SortedBag.of();

                    if (claimedRoute.level().equals(Route.Level.UNDERGROUND)) {
                        announce(players, currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

                        var builder = new SortedBag.Builder<Card>();

                        for (int iteration = 0; iteration < Constants.ADDITIONAL_TUNNEL_CARDS; iteration++) {
                            currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);
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

                            if (possibleAdditionalCards.isEmpty()) {
                                announce(players, currentPlayerInfo.didNotClaimRoute(claimedRoute));
                                break;
                            } else {
                                additionalChosenCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);

                                if (additionalChosenCards.isEmpty()) {
                                    announce(players, currentPlayerInfo.didNotClaimRoute(claimedRoute));
                                    break;
                                }
                            }
                        }
                    }

                    SortedBag<Card> claimCards = initialClaimCards.union(additionalChosenCards);
                    announce(players, currentPlayerInfo.claimedRoute(claimedRoute, claimCards));
                    currentGameState = currentGameState.withClaimedRoute(claimedRoute, claimCards);

                    break;
                case DRAW_TICKETS:
                    announce(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> ticketChoice = currentGameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(ticketChoice);

                    currentGameState = currentGameState.withChosenAdditionalTickets(ticketChoice, chosenTickets);
                    announce(players, currentPlayerInfo.keptTickets(chosenTickets.size()));

                    break;
                default:
                    throw new Error();
            }

            // When the last turn begins
            if (currentGameState.lastTurnBegins() && !hasLastTurnBegun) {
                hasLastTurnBegun = true;
                announce(players, currentPlayerInfo.lastTurnBegins(currentPlayerState.carCount()));
            }

            currentGameState = currentGameState.withCardsDeckRecreatedIfNeeded(rng);
            currentGameState = currentGameState.forNextTurn();
        } while (finalTurnsAmount < players.size());

        // endregion

        // region ending

        final GameState gameOverState = currentGameState;
        update(players, gameOverState);

        // Map of scores without bonus
        Map<PlayerId, Integer> playerScores = new HashMap<>();
        players.forEach((k, v) -> playerScores.put(k, gameOverState.playerState(k).finalPoints()));

        // Map of the longest length from player routes
        Map<PlayerId, Trail> playersLongestTrails = new HashMap<>();

        players
                .forEach((k, v) -> playersLongestTrails.put(k, Trail.longest(gameOverState.playerState(k).routes())));

        Set<Entry<PlayerId, Trail>> playerLongestTrailsSet = playersLongestTrails.entrySet();

        // Filter to the longest trail
        int maxLength = playerLongestTrailsSet
                .stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().length())).get().getValue().length();


        Map<PlayerId, Trail> playersForBonus = playerLongestTrailsSet
                .stream()
                .filter(entry -> entry.getValue().length() == (maxLength))
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
        // endregion
    }

    private static void announce(Map<PlayerId, Player> players, String message) {
        players.forEach((k, v) -> v.receiveInfo(message));
    }

    private static void update(Map<PlayerId, Player> players, GameState gameState) {
        players.forEach((k, v) -> v.updateState(gameState, gameState.playerState(k)));
    }
}
