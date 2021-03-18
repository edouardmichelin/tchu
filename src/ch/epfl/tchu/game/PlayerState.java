package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;

/**
 * L'état complet d'un joueur
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;


    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Construit l'état initial du joueur en fonction des cartes données, lève IllegalArgumentException
     * si le nombre de cartes donné n'est pas égal à 4
     *
     * @param initialCards Les cartes initiale du joueur
     * @return l'état initial du joueur
     */
    static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Retourne les billets du joueur,
     *
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return this.tickets;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés
     *
     * @param newTickets Les tickets à ajouter à l'état du joueur
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag<Ticket> union = this.tickets.union(newTickets);
        return new PlayerState(union, this.cards, this.routes);
    }

    /**
     * Retourne les cartes wagon/locomotive du joueur
     *
     * @return les cartes wagon/locomotive du joueur
     */
    public SortedBag<Card> cards() {
        return this.cards;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     *
     * @param card la carte à ajouter
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> union = this.cards.union(SortedBag.of(card));
        return new PlayerState(this.tickets, union, this.routes);
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     *
     * @param additionalCards l'ensemble de cartes à ajouter
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        SortedBag<Card> union = this.cards.union(additionalCards);
        return new PlayerState(this.tickets, union, this.routes);
    }

    //WIP
    public boolean canClaimRoute(Route route) {
        if (this.carCount() >= route.length()) {

        }
        return false;
    }

    //WIP
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());
        return route.possibleClaimCards();
    }

    //WIP
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        Preconditions.checkArgument(!initialCards.isEmpty());
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == 3);
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au
     * moyen des cartes données
     *
     * @param route      route dont le joueur s'est emparé
     * @param claimCards cartes défaussées pour s'emparer de la route
     * @return un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au
     * moyen des cartes données
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routes = this.routes();
        routes.add(route);
        return new PlayerState(this.tickets, this.cards.difference(claimCards), routes);
    }

    //WIP
    public int ticketPoints() {
        return 0;
    }

    /**
     * Retourne la totalité des points obtenus par le joueur à la fin de la partie
     *
     * @return retourne la totalité des points obtenus par le joueur à la fin de la partie
     */
    public int finalPoints() {
        return this.ticketPoints() + this.claimPoints();
    }
}
