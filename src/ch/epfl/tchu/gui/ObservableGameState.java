package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente l'état observable d'une partie de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class ObservableGameState {
    private final static List<Route> ALL_ROUTES = ChMap.routes();
    private final static int TICKETS_COUNT = ChMap.tickets().size();
    private final PlayerId playerId;
    private final IntegerProperty ticketsPercentage = new SimpleIntegerProperty(0);
    private final IntegerProperty cardsPercentage = new SimpleIntegerProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<String, ObjectProperty<PlayerId>> routes;
    private final Map<PlayerId, PlayerBelongingsProperty> playersBelongings;
    private final ObservableList<Ticket> playerTickets;
    private final Map<Card, IntegerProperty> playerHand;
    private final Map<String, BooleanProperty> claimableRoutes;
    private PublicGameState currentGameState;
    private PlayerState currentPlayerState;

    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;

        this.faceUpCards = initializeFaceUpCards();
        this.routes = initializeRoutes();

        this.playersBelongings = initializePlayersBelongings();

        this.playerTickets = FXCollections.observableArrayList();
        this.playerHand = initializePlayerHand();
        this.claimableRoutes = initializeClaimableRoutes();

        this.currentGameState = initializeGameState(playerId);
        this.currentPlayerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
    }

    // region initializers

    private static List<ObjectProperty<Card>> initializeFaceUpCards() {
        List<ObjectProperty<Card>> fuc = new ArrayList<>(Constants.FACE_UP_CARDS_COUNT);
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            fuc.add(new SimpleObjectProperty<>());
        }

        return fuc;
    }

    private static Map<String, ObjectProperty<PlayerId>> initializeRoutes() {
        Map<String, ObjectProperty<PlayerId>> r = new HashMap<>();
        for (Route route : ALL_ROUTES) {
            r.put(route.id(), new SimpleObjectProperty<>(null));
        }

        return r;
    }

    private static Map<PlayerId, PlayerBelongingsProperty> initializePlayersBelongings() {
        Map<PlayerId, PlayerBelongingsProperty> r = new HashMap<>();
        for (PlayerId player : PlayerId.ALL) {
            r.put(player, new PlayerBelongingsProperty(
                    new SimpleIntegerProperty(Constants.INITIAL_TICKETS_COUNT),
                    new SimpleIntegerProperty(0),
                    new SimpleIntegerProperty(0),
                    new SimpleIntegerProperty(0)
            ));
        }

        return r;
    }

    private static Map<Card, IntegerProperty> initializePlayerHand() {
        Map<Card, IntegerProperty> playerHand = new HashMap<>();
        for (Card card : Card.ALL) {
            playerHand.put(card, new SimpleIntegerProperty(0));
        }

        return playerHand;
    }

    private static Map<String, BooleanProperty> initializeClaimableRoutes() {
        Map<String, BooleanProperty> r = new HashMap<>();
        for (Route route : ALL_ROUTES) {
            r.put(route.id(), new SimpleBooleanProperty(false));
        }

        return r;
    }

    private static PublicGameState initializeGameState(PlayerId playerId) {
        Map<PlayerId, PublicPlayerState> playerState = new HashMap<>();

        for (PlayerId player : PlayerId.ALL) {
            playerState.put(player, new PublicPlayerState(0, 0, List.of()));
        }

        return new PublicGameState(
                TICKETS_COUNT,
                new PublicCardState(Card.ALL.subList(0, Constants.FACE_UP_CARDS_COUNT), 0, 0),
                playerId,
                playerState,
                null);
    }

    // endregion

    public void setState(PublicGameState gameState, PlayerState playerState) {
        this.currentGameState = gameState;
        this.currentPlayerState = playerState;

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = gameState.cardState().faceUpCard(slot);
            this.faceUpCards.get(slot).set(newCard);
        }

        for (PlayerId player : PlayerId.ALL) {
            var iterationPlayerState = gameState.playerState(player);
            for (Route playerRoute : iterationPlayerState.routes()) {
                this.routes.get(playerRoute.id()).set(playerId);
            }

            var pb = this.playersBelongings.get(player);
            pb.ownedTickets().set(iterationPlayerState.ticketCount());
            pb.ownedCards().set(iterationPlayerState.cardCount());
            pb.ownedCars().set(iterationPlayerState.carCount());
            pb.claimPoints().set(iterationPlayerState.claimPoints());
        }

        this.ticketsPercentage.set((gameState.ticketsCount() * 100 / TICKETS_COUNT));
        this.cardsPercentage.set((gameState.cardState().deckSize() * 100 / Constants.TOTAL_CARDS_COUNT));

        this.playerTickets.setAll(playerState.tickets().toList());

        for (Card card : Card.ALL) {
            this.playerHand.get(card).set(playerState.cards().countOf(card));
        }

        if (gameState.currentPlayerId().equals(this.playerId)) {
            for (Route route : ALL_ROUTES) {
                if (playerState.canClaimRoute(route)) {
                    this.claimableRoutes.get(route.id()).set(true);
                }
            }
        } else {
            for (Route route : ALL_ROUTES) {
                this.claimableRoutes.get(route.id()).set(true);
            }
        }

    }

    // region getters

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyObjectProperty<PlayerId> routes(String id) {
        return routes.get(id);
    }

    public ReadOnlyIntegerProperty ticketsPercentage() {
        return this.ticketsPercentage;
    }

    public ReadOnlyIntegerProperty cardsPercentage() {
        return this.cardsPercentage;
    }

    public ReadOnlyPlayerBelongingsProperty playerBelongings(PlayerId player) {
        return this.playersBelongings.get(player);
    }

    public ObservableList<Ticket> playerTickets() {
        return FXCollections.unmodifiableObservableList(this.playerTickets);
    }

    public ReadOnlyIntegerProperty numberOfCard(Card card) {
        return this.playerHand.get(card);
    }

    public ReadOnlyBooleanProperty canClaimRoute(String id) {
        return this.claimableRoutes.get(id);
    }

    // endregion

    // region méthodes-de-PublicGameState

    public boolean canDrawTickets() {
        return this.currentGameState.canDrawTickets();
    }

    public boolean canDrawCards() {
        return this.currentGameState.canDrawCards();
    }

    public List<Card> faceUpCards() {
        return this.currentGameState.cardState().faceUpCards();
    }

    // endregion

    // region méthodes-de-PlayerState

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return this.currentPlayerState.possibleClaimCards(route);
    }

    public SortedBag<Card> cards() {
        return this.currentPlayerState.cards();
    }

    // endregion

    public interface ReadOnlyPlayerBelongingsProperty {
        ReadOnlyIntegerProperty ownedTickets();

        ReadOnlyIntegerProperty ownedCards();

        ReadOnlyIntegerProperty ownedCars();

        ReadOnlyIntegerProperty claimPoints();
    }

    public static class PlayerBelongingsProperty implements ReadOnlyPlayerBelongingsProperty {
        private final IntegerProperty ownedTickets;
        private final IntegerProperty ownedCards;
        private final IntegerProperty ownedCars;
        private final IntegerProperty claimPoints;

        public PlayerBelongingsProperty(
                IntegerProperty ownedTickets,
                IntegerProperty ownedCards,
                IntegerProperty ownedCars,
                IntegerProperty claimPoints
        ) {
            this.ownedTickets = ownedTickets;
            this.ownedCards = ownedCards;
            this.ownedCars = ownedCars;
            this.claimPoints = claimPoints;
        }

        @Override
        public IntegerProperty ownedTickets() {
            return this.ownedTickets;
        }

        @Override
        public IntegerProperty ownedCards() {
            return this.ownedCards;
        }

        @Override
        public IntegerProperty ownedCars() {
            return this.ownedCars;
        }

        @Override
        public IntegerProperty claimPoints() {
            return this.claimPoints;
        }
    }
}
