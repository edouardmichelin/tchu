package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Représente l'état observable d'une partie de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class ObservableGameState {
    private final static List<Route> ALL_ROUTES = ChMap.routes();
    private final static int INITIAL_TICKETS_COUNT = ChMap.tickets().size();

    private PublicGameState currentGameState;
    private PlayerState currentPlayerState;

    private final PlayerId playerId;

    private final IntegerProperty ticketsPercentage = new SimpleIntegerProperty(0);
    private final IntegerProperty cardsPercentage = new SimpleIntegerProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<String, ObjectProperty<PlayerId>> routes;

    private final Map<PlayerId, ObjectProperty<PlayerBelongingsDTO>> playersBelongings;

    private final ListProperty<Ticket> playerTickets;
    private final Map<Card, IntegerProperty> playerHand;
    private final Map<String, BooleanProperty> claimableRoutes;

    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;

        this.faceUpCards = initializeFaceUpCards();
        this.routes = initializeRoutes();

        this.playersBelongings = initializePlayersBelongings();

        this.playerTickets = new SimpleListProperty<>();
        this.playerHand = initializePlayerHand();
        this.claimableRoutes = initializeClaimableRoutes();
    }

    // region initializers

    private static List<ObjectProperty<Card>> initializeFaceUpCards() {
        List<ObjectProperty<Card>> fuc = new ArrayList<>();
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            fuc.set(slot, new SimpleObjectProperty<>());
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

    private static Map<PlayerId, ObjectProperty<PlayerBelongingsDTO>> initializePlayersBelongings() {
        Map<PlayerId, ObjectProperty<PlayerBelongingsDTO>> r = new HashMap<>();
        for (PlayerId player : PlayerId.ALL) {
            r.put(player, new SimpleObjectProperty<>(new PlayerBelongingsDTO(
                    Constants.INITIAL_TICKETS_COUNT,
                    0,
                    0,
                    0
                    )));
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

    // endregion

    public void setState(PublicGameState gameState, PlayerState playerState) {
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = gameState.cardState().faceUpCard(slot);
            this.faceUpCards.get(slot).set(newCard);
        }

        for (PlayerId player : PlayerId.ALL) {
            var iterationPlayerState = gameState.playerState(player);
            for (Route playerRoute : iterationPlayerState.routes()) {
                this.routes.get(playerRoute.id()).set(playerId);
            }

            this.playersBelongings
                    .get(player)
                    .set(new PlayerBelongingsDTO(
                            iterationPlayerState.ticketCount(),
                            iterationPlayerState.cardCount(),
                            iterationPlayerState.carCount(),
                            iterationPlayerState.claimPoints())
                    );
        }

        this.ticketsPercentage.set((gameState.ticketsCount() / INITIAL_TICKETS_COUNT) * 100);
        this.cardsPercentage.set((gameState.cardState().deckSize() / Constants.INITIAL_CAR_COUNT) * 100);

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

    private ReadOnlyIntegerProperty ticketsPercentage() {
        return this.ticketsPercentage;
    }

    private ReadOnlyIntegerProperty cardsPercentage() {
        return this.cardsPercentage;
    }

    private ReadOnlyObjectProperty<PlayerBelongingsDTO> playerBelongings(PlayerId player) {
        return this.playersBelongings.get(player);
    }

    private ReadOnlyListProperty<Ticket> playerTickets() {
        return this.playerTickets;
    }

    private ReadOnlyIntegerProperty playerTickets(Card card) {
        return this.playerHand.get(card);
    }

    private ReadOnlyBooleanProperty claimableRoutes(String id) {
        return this.claimableRoutes.get(id);
    }

    // endregion

    // region méthodes-de-PublicGameState




    // endregion

    // region méthodes-de-PlayerState

    // endregion

    protected static class PlayerBelongingsDTO {
        public final int ownedTickets;
        public final int ownedCards;
        public final int ownedCars;
        public final int claimPoints;

        public PlayerBelongingsDTO(int ownedTickets, int ownedCards, int ownedCars, int claimPoints) {
            this.ownedTickets = ownedTickets;
            this.ownedCards = ownedCards;
            this.ownedCars = ownedCars;
            this.claimPoints = claimPoints;
        }
    }
}
