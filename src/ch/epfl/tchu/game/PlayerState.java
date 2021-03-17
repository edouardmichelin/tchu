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

    public SortedBag<Ticket> tickets(){
        return this.tickets;
    }
}
