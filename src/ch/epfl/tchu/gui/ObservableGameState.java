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
    private final ObservableList<Map.Entry<Ticket, Integer>> playerTickets = FXCollections.observableArrayList();
    private final Map<Card, IntegerProperty> playerHand;
    private final Map<String, BooleanProperty> claimableRoutes;
    private PublicGameState currentGameState;
    private PlayerState currentPlayerState;
    private int numberOfPlayers;

    /**
     * Créé l'état observable d'une partie de tCHu
     *
     * @param playerId l'identité du joueur auquelle correspond la vue
     * @param numberOfPlayers nombre de joueur dans la partie
     */
    public ObservableGameState(PlayerId playerId, int numberOfPlayers) {
        this.playerId = playerId;

        this.numberOfPlayers = numberOfPlayers;

        this.faceUpCards = initializeFaceUpCards();
        this.routes = initializeRoutes();

        this.playersBelongings = initializePlayersBelongings();

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

    private Map<PlayerId, PlayerBelongingsProperty> initializePlayersBelongings() {
        Map<PlayerId, PlayerBelongingsProperty> r = new HashMap<>();
        for (PlayerId player : PlayerId.ALL) {
            if (player.ordinal() >= this.numberOfPlayers) continue;
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

    private PublicGameState initializeGameState(PlayerId playerId) {
        Map<PlayerId, PublicPlayerState> playerState = new HashMap<>();

        for (PlayerId player : PlayerId.ALL) {
            if (player.ordinal() >= this.numberOfPlayers) continue;
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

    /**
     * Mets à jour la totalité des propriétés de l'état observable du jeu
     *
     * @param gameState   état observable du jeu
     * @param playerState état complet du joueur
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        this.currentGameState = gameState;
        this.currentPlayerState = playerState;

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = gameState.cardState().faceUpCard(slot);
            this.faceUpCards.get(slot).set(newCard);
        }

        for (PlayerId player : PlayerId.ALL) {
            if (player.ordinal() >= this.numberOfPlayers) continue;
            var iterationPlayerState = gameState.playerState(player);
            for (Route playerRoute : iterationPlayerState.routes()) {
                this.routes.get(playerRoute.id()).set(player);
            }

            var pb = this.playersBelongings.get(player);
            pb.ownedTickets().set(iterationPlayerState.ticketCount());
            pb.ownedCards().set(iterationPlayerState.cardCount());
            pb.ownedCars().set(iterationPlayerState.carCount());
            pb.claimPoints().set(iterationPlayerState.claimPoints());
        }

        this.ticketsPercentage.set((gameState.ticketsCount() * 100 / TICKETS_COUNT));
        this.cardsPercentage.set((gameState.cardState().deckSize() * 100 / Constants.TOTAL_CARDS_COUNT));

        this.playerTickets.setAll(playerState.ticketsWithPoints().entrySet());

        for (Card card : Card.ALL)
            this.playerHand.get(card).set(playerState.cards().countOf(card));

        // compliqué à lire pour les non-initiés
        // une route est considérée comme prenable par le joueur courant si
        // 1. Il a les moyens de l'acheter ET
        // 2. Personne ne la possède encore ET
        // 3.1 S'il y a plus de 2 joueurs : il ne possède pas sa voisine
        // 3.2 sinon : personne ne possède sa voisine
        if (gameState.currentPlayerId().equals(this.playerId))
            for (Route route : ALL_ROUTES)
                this.claimableRoutes
                        .get(route.id())
                        .set(
                                playerState.canClaimRoute(route) &&
                                        this.routes.get(route.id()).isNull().get() &&
                                        (
                                                this.numberOfPlayers > 2 ?
                                                        this.getRouteNeighbor(route).isNotEqualTo(this.playerId).get() :
                                                        this.getRouteNeighbor(route).isNull().get())
                        );
        else
            for (Route route : ALL_ROUTES)
                this.claimableRoutes.get(route.id()).set(this.routes.get(route.id()).isNull().not().get());

    }

    // region getters

    /**
     * Retourne la propriété en lecture seule pour la carte à l'emplacement spécifié
     *
     * @param slot emplacement de la pioche
     * @return la propriété en lecture seule d'une carte face visible
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * Retourne la propriété en lecture seule du joueur propriétaire d'une route donnée
     *
     * @param id l'identifiant de la route
     * @return la propriété du joueur propriétaire
     */
    public ReadOnlyObjectProperty<PlayerId> routesOwner(String id) {
        return routes.get(id);
    }

    /**
     * Retourne la propriété en lecture seule du pourcentage restant de tickets dans la pioche
     *
     * @return pourcentage restant de tickets
     */
    public ReadOnlyIntegerProperty ticketsPercentage() {
        return this.ticketsPercentage;
    }

    /**
     * Retourne la propriété en lecture seule du pourcentage restant de cartes dans la pioche
     *
     * @return pourcentage restant de cartes
     */
    public ReadOnlyIntegerProperty cardsPercentage() {
        return this.cardsPercentage;
    }

    /**
     * Retourne la propriété en lecture seule contenant l'ensemble des éléments de jeu que le joueur possède
     *
     * @param player le joueur dont on souhaite récupérer les possessions
     * @return les possessions du joueur
     */
    public ReadOnlyPlayerBelongingsProperty playerBelongings(PlayerId player) {
        return this.playersBelongings.get(player);
    }

    /**
     * Créé une liste observable des tickets dans la main du joueur
     *
     * @return une liste observable des tickets dans la main du joueur
     */
    public ObservableList<Map.Entry<Ticket, Integer>> playerTickets() {
        return FXCollections.unmodifiableObservableList(this.playerTickets);
    }

    /**
     * Retourne une propriété en lecture seule du nombre d'une certaine carte que le joueur a en main
     *
     * @param card la carte dont on cherche la multiplicité dans la main du joueur
     * @return le nombre de la carte donnée en main du joueur
     */
    public ReadOnlyIntegerProperty numberOfCard(Card card) {
        return this.playerHand.get(card);
    }

    /**
     * Retourne une propriété en lecture seule qui est vrai si le joueur peut s'emparer de la route donnée, faux
     * autrement
     *
     * @param id identifiant de la route
     * @return vrai si le joueur peut s'emparer de la route donnée, faux autrement
     */
    public ReadOnlyBooleanProperty canClaimRoute(String id) {
        return this.claimableRoutes.get(id);
    }

    // endregion

    // region méthodes-de-PublicGameState

    /**
     * Retourne vrai si le joueur peut tirer des tickets, faux autrement
     *
     * @return vrai si le joueur peut tirer des tickets, faux autrement
     */
    public boolean canDrawTickets() {
        return this.currentGameState.canDrawTickets();
    }

    /**
     * Retourne vrai si le joueur peut piocher des cartes, faux autrement
     *
     * @return vrai si le joueur peut piocher des cartes, faux autrement
     */
    public boolean canDrawCards() {
        return this.currentGameState.canDrawCards();
    }

    // endregion

    // region méthodes-de-PlayerState

    /**
     * Retourne la liste des cartes possibles à jouer pour le coût initial d'une route
     *
     * @param route la route dont on souhaite s'emparer
     * @return Le multi-ensembles contenant les possibilités de cartes à jouer
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return this.currentPlayerState.possibleClaimCards(route);
    }

    /**
     * Retourne les cartes en main du joueur
     *
     * @return les cartes en main du joueur
     */
    public SortedBag<Card> cards() {
        return this.currentPlayerState.cards();
    }

    // endregion

    private ObjectProperty<PlayerId> getRouteNeighbor(Route route) {
        ObjectProperty<PlayerId> prop;

        if (route.id().endsWith("_1"))
            prop = this.routes.get(route.id().replace("_1", "_2"));
        else
            prop = this.routes.get(route.id().replace("_2", "_1"));

        return prop == null ? new SimpleObjectProperty<>() : prop;
    }

    /**
     * Interface permettant le retour en lecture seule de quelques propriétés
     */
    public interface ReadOnlyPlayerBelongingsProperty {
        ReadOnlyIntegerProperty ownedTickets();

        ReadOnlyIntegerProperty ownedCards();

        ReadOnlyIntegerProperty ownedCars();

        ReadOnlyIntegerProperty claimPoints();
    }

    /**
     * L'ensemble des possessions du joueur
     */
    public static class PlayerBelongingsProperty implements ReadOnlyPlayerBelongingsProperty {
        private final IntegerProperty ownedTickets;
        private final IntegerProperty ownedCards;
        private final IntegerProperty ownedCars;
        private final IntegerProperty claimPoints;

        /**
         * Permet de créer un ensemble de possessions
         *
         * @param ownedTickets les tickets en main du joueur
         * @param ownedCards   les cartes en main du joueur
         * @param ownedCars    les wagons restants du joueur
         * @param claimPoints  les points de constructions du joueur
         */
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

        /**
         * Retourne la propriété des tickets en possession du joueur
         *
         * @return la propriété des tickets en possession du joueur
         */
        @Override
        public IntegerProperty ownedTickets() {
            return this.ownedTickets;
        }

        /**
         * Retourne la propriété des cartes en possession du joueur
         *
         * @return la propriété des cartes en possession du joueur
         */
        @Override
        public IntegerProperty ownedCards() {
            return this.ownedCards;
        }

        /**
         * Retourne la propriété des wagons restants du joueur
         *
         * @return la propriété des wagons restants du joueur
         */
        @Override
        public IntegerProperty ownedCars() {
            return this.ownedCars;
        }

        /**
         * Retourne la propriété des points de constructions du joueur
         *
         * @return la propriété des points de constructions du joueur
         */
        @Override
        public IntegerProperty claimPoints() {
            return this.claimPoints;
        }
    }
}
