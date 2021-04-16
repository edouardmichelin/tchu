package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Représente un tas de cartes
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    private Deck(List<C> cards) {
        this.cards = cards;
    }

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> shuffledCards = cards.toList();
        Collections.shuffle(shuffledCards, rng);

        return new Deck<>(shuffledCards);
    }

    /**
     * Retourne la taille du tas, c-à-d le nombre de cartes qu'il contient
     *
     * @return la taille du tas, c-à-d le nombre de cartes qu'il contient
     */
    public int size() {
        return cards.size();
    }

    /**
     * Retourne vrai ssi le tas est vide
     *
     * @return <code>true</code> ssi le tas est vide
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Retourne la carte au sommet du tas, ou lève IllegalArgumentException si le tas est vide
     *
     * @return la carte au sommet du tas, ou lève IllegalArgumentException si le tas est vide
     */
    public C topCard() {
        if (this.isEmpty()) throw new IllegalArgumentException();

        return cards.get(0);
    }

    /**
     * Retourne un tas identique au récepteur (this) mais sans la carte au sommet, ou lève IllegalArgumentException
     * si le tas est vide
     *
     * @return un tas identique au récepteur (this) mais sans la carte au sommet, ou lève IllegalArgumentException si
     * le tas est vide
     */
    public Deck<C> withoutTopCard() {
        return new Deck<>(this.cards.subList(1, this.cards.size()));
    }

    /**
     * Retourne un multiensemble contenant les count cartes se trouvant au sommet du tas; lève
     * IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     *
     * @param count nombre de cartes à prendre en compte
     * @return un multiensemble contenant les count cartes se trouvant au sommet du tas; lève
     * IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     */
    public SortedBag<C> topCards(int count) {
        Objects.checkIndex(0, this.cards.size() + 1);

        return SortedBag.of(this.cards.subList(0, count));
    }

    /**
     * Retourne un tas identique au récepteur (this) mais sans les count cartes du sommet, ou lève
     * IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     *
     * @param count nombre de cartes à prendree en compte
     * @return un tas identique au récepteur (this) mais sans les count cartes du sommet, ou lève
     * IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     */
    public Deck<C> withoutTopCards(int count) {
        Objects.checkIndex(0, this.cards.size() + 1);

        return new Deck<>(this.cards.subList(count, this.cards.size()));
    }
}
