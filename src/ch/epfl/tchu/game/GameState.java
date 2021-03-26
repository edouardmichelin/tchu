package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * L'état d'une partie
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class GameState extends PublicGameState {

    private final CardState cardState;
    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;

    private GameState(
            Deck<Ticket> tickets,
            CardState cardState,
            PlayerId currentPlayerId,
            Map<PlayerId, PlayerState> playerState,
            PlayerId lastPlayer
    ) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.cardState = cardState;
        this.tickets = tickets;
        this.playerState = playerState;
    }

    /**
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient les billets donnés
     * et la pioche des cartes contient les cartes de Constants.ALL_CARDS, sans les 8 (2×4) du dessus, distribuées
     * aux joueurs ; ces pioches sont mélangées au moyen du générateur aléatoire donné, qui est aussi utilisé pour
     * choisir au hasard l'identité du premier joueur
     *
     * @param tickets les tickets
     * @param rng     le générateur pseudo-aléatoire
     * @return l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient les billets donnés et
     * la pioche des cartes contient les cartes de Constants.ALL_CARDS, sans les 8 (2×4) du dessus, distribuées aux
     * joueurs ; ces pioches sont mélangées au moyen du générateur aléatoire donné, qui est aussi utilisé pour
     * choisir au hasard l'identité du premier joueur
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Ticket> ticketsDeck = Deck.of(tickets, rng);

        List<Deck<Card>> cardsDeck = new ArrayList<>();
        cardsDeck.add(Deck.of(Constants.ALL_CARDS, rng));

        List<PlayerId> playerIds = PlayerId.ALL;

        PlayerId firstPlayerToPlay = playerIds.get(rng.nextInt() % PlayerId.COUNT);

        Map<PlayerId, PlayerState> playerState = new TreeMap<>();

        playerIds.forEach(playerId -> {
            playerState.put(playerId, PlayerState.initial(cardsDeck.get(0).topCards(Constants.INITIAL_CARDS_COUNT)));
            cardsDeck.set(0, cardsDeck.get(0).withoutTopCards(Constants.INITIAL_CARDS_COUNT));
        });


        return new GameState(ticketsDeck, CardState.of(cardsDeck.get(0)), firstPlayerToPlay, playerState, null);
    }

    /**
     * Retourne l'état complet du joueur d'identité donnée
     *
     * @param playerId le joueur dont on veut obtenir l'état de la partie
     * @return l'état complet du joueur d'identité donnée
     */
    public PlayerState playerState(PlayerId playerId) {
        return this.playerState.get(playerId);
    }

    /**
     * Retourne l'état complet du joueur courant
     *
     * @return l'état complet du joueur courant
     */
    public PlayerState currentPlayerState() {
        return this.playerState(currentPlayerId());
    }

    /**
     * Retourne les count billets du sommet de la pioche, ou lève IllegalArgumentException si count n'est pas compris
     * entre 0 et la taille de la pioche (inclus)
     *
     * @param count Le nombre de tickets au sommet du tas à retourner.
     * @return Les count billets du sommet de la pioche, ou lève IllegalArgumentException si count n'est pas
     * compris entre 0 et la taille de la pioche (inclus)
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= this.ticketsCount());
        return this.tickets.topCards(count);
    }

    /**
     * Retourne un état identique au récepteur, mais sans les count billets du sommet de la pioche, ou lève
     * IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     *
     * @param count Le nombre de billets au sommet du tas à retourner.
     * @return un état identique au récepteur, mais sans les count billets du sommet de la pioche, ou lève
     * IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= this.cardState.deckSize());
        return new GameState(this.tickets.withoutTopCards(count), this.cardState, this.currentPlayerId(),
                this.playerState, this.lastPlayer());
    }

    /**
     * Retourne la carte au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     *
     * @return la carte au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     */
    public Card topCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return this.cardState.topDeckCard();
    }

    /**
     * Retourne un état identique au récepteur mais sans la carte au sommet de la pioche, ou lève
     * IllegalArgumentException si la pioche est vide
     *
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche, ou lève
     * IllegalArgumentException si la pioche est vide
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return new GameState(this.tickets, this.cardState.withoutTopDeckCard(), this.currentPlayerId(),
                this.playerState, this.lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur mais avec les cartes données ajoutées à la défausse
     *
     * @param discardedCards les cartes défaussées à ajouter à la pile de défausse
     * @return un état identique au récepteur mais avec les cartes données ajoutées à la défausse
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(this.tickets, this.cardState.withMoreDiscardedCards(discardedCards),
                this.currentPlayerId(), this.playerState, this.lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée à
     * partir de la défausse, mélangée au moyen du générateur aléatoire donné
     *
     * @param rng générateur aléatoire à donner pour le mélange des cartes
     * @return un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est
     * recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (this.cardState.isDeckEmpty()) {
            return new GameState(this.tickets, this.cardState.withDeckRecreatedFromDiscards(rng),
                    this.currentPlayerId(), this.playerState, this.lastPlayer());
        }
        return this;
    }

    /**
     * Retourne un état identique au récepteur mais dans lequel les billets donnés ont été ajoutés à la main du
     * joueur donné; lève IllegalArgumentException si le joueur en question possède déjà au moins un billet
     *
     * @param playerId      l'id du joueur à qui ajouter les tickets
     * @param chosenTickets les tickets à ajouter
     * @return un état identique au récepteur mais dans lequel les billets donnés ont été ajoutés à la main
     * du joueur donné; lève IllegalArgumentException si le joueur en question possède déjà au moins un billet
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(!(this.playerState.get(playerId).ticketCount() > 0));
        GameState newGameState = this;
        newGameState.playerState.put(playerId, this.playerState.get(playerId).withAddedTickets(chosenTickets));
        return newGameState;
    }

    /**
     * Retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets drawnTickets du
     * sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket ; lève IllegalArgumentException si
     * l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     *
     * @param drawnTickets  les tickets tirés
     * @param chosenTickets les tickets choisis par le joueur parmis ceux tirés
     * @return un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets
     * drawnTickets du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket ; lève
     * IllegalArgumentException si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        SortedBag<Ticket> discardedTickets = drawnTickets.difference(chosenTickets);
        var newPlayerState = Map.copyOf(this.playerState);
        newPlayerState.put(this.currentPlayerId(),
                this.playerState.get(this.currentPlayerId()).withAddedTickets(chosenTickets));
        return new GameState(this.tickets.withoutTopCards(drawnTickets.size()), this.cardState,
                this.currentPlayerId(), newPlayerState, this.lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné a été
     * placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche; lève
     * IllegalArgumentException s'il n'est pas possible de tirer des cartes
     *
     * @param slot emplacement de la carte retournée piochée par le joueur
     * @return un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné
     * a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche; lève
     * IllegalArgumentException s'il n'est pas possible de tirer des cartes
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(this.canDrawCards());
        Card pickedCard = this.cardState.faceUpCard(slot);
        CardState newCardState = this.cardState.withDrawnFaceUpCard(slot);

        var newPlayerState = Map.copyOf(this.playerState);
        newPlayerState.put(this.currentPlayerId(),
                this.playerState.get(this.currentPlayerId()).withAddedCard(pickedCard));

        return new GameState(this.tickets, newCardState, this.currentPlayerId(), newPlayerState,
                this.lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans la
     * main du joueur courant; lève IllegalArgumentException s'il n'est pas possible de tirer des cartes
     *
     * @return un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée
     * dans la main du joueur courant; lève IllegalArgumentException s'il n'est pas possible de tirer des cartes
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(this.canDrawCards());
        Card drawnCard = this.cardState.topDeckCard();
        var newPlayerState = Map.copyOf(this.playerState);

        newPlayerState.put(this.currentPlayerId(),
                this.playerState.get(this.currentPlayerId()).withAddedCard(drawnCard));
        return new GameState(this.tickets, this.cardState.withoutTopDeckCard(), this.currentPlayerId(),
                newPlayerState, this.lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route donnée au
     * moyen des cartes données
     *
     * @param route route dont le joueur s'est emparé
     * @param cards cartes que le joueur à défaussé pour la route
     * @return un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route
     * donnée au moyen des cartes données
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        var newPlayerState = Map.copyOf(this.playerState);
        newPlayerState.put(this.currentPlayerId(),
                this.playerState.get(this.currentPlayerId()).withClaimedRoute(route, cards));
        return new GameState(this.tickets, this.cardState, this.currentPlayerId(), newPlayerState, this.lastPlayer());
    }

    /**
     * Retourne vrai ssi le dernier tour commence, c-à-d si l'identité du dernier joueur est actuellement inconnue
     * mais que le joueur courant n'a plus que deux wagons ou moins ; cette méthode doit être appelée uniquement à la
     * fin du tour d'un joueur
     *
     * @return <code>true</code> ssi le dernier tour commence, c-à-d si l'identité du dernier joueur est actuellement
     * inconnue mais que le joueur courant n'a plus que deux wagons ou moins ; cette méthode doit être appelée
     * uniquement à la fin du tour d'un joueur
     */
    public boolean lastTurnBegins() {
        boolean hasCurrentPlayerTwoCarsOrLess = this.playerState.get(this.currentPlayerId()).carCount() <= 2;
        boolean isLastPlayerUnknown = this.lastPlayer() == null;

        return isLastPlayerUnknown && hasCurrentPlayerTwoCarsOrLess;
    }

    /**
     * Termine le tour du joueur courant, c-à-d retourne un état identique au récepteur si ce n'est que le joueur
     * courant est celui qui suit le joueur courant actuel ; de plus, si lastTurnBegins retourne vrai, le joueur
     * courant actuel devient le dernier joueur
     *
     * @return un état identique au récépteur <code>this</code>
     */
    public GameState forNextTurn() {
        PlayerId lastPlayer = this.lastTurnBegins() ? null : this.currentPlayerId();
        return new GameState(this.tickets, this.cardState, this.currentPlayerId().next(), this.playerState, lastPlayer);
    }


}