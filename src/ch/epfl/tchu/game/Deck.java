package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
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

    /**
     * Retourne un tas de cartes ayant les mêmes cartes que le multiensemble cards, mélangées au moyen du générateur
     * de nombres pseudo aléatoires <i>rng</i>
     *
     * @param cards les cartes
     * @param rng   le PRNG
     * @param <C>   types cartes du deck
     * @return un tas de cartes ayant les mêmes cartes que le multiensemble cards, mélangées au moyen du générateur
     * de nombres pseudo aléatoires <code>rng</code>
     */
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
     * Retourne la carte au sommet du tas, ou lève <i>IllegalArgumentException</i> si le tas est vide
     *
     * @return la carte au sommet du tas
     * @throws IllegalArgumentException si le tas est vide
     */
    public C topCard() {
        Preconditions.checkArgument(!this.isEmpty());

        return cards.get(0);
    }

    /**
     * Retourne un tas identique au récepteur <i>this</i> mais sans la carte au sommet, ou lève
     * <i>IllegalArgumentException</i>
     * si le tas est vide
     *
     * @return un tas identique au récepteur <code>this</code> mais sans la carte au sommet
     * @throws IllegalArgumentException si le tas est vide
     */
    public Deck<C> withoutTopCard() {
        return this.withoutTopCards(1);
    }

    /**
     * Retourne un multiensemble contenant les count cartes se trouvant au sommet du tas; lève
     * <i>IllegalArgumentException</i> si <i>count</i> n'est pas compris entre <i>0</i> (inclus) et la taille du tas
     * (incluse)
     *
     * @param count nombre de cartes à prendre en compte
     * @return un multiensemble contenant les count cartes se trouvant au sommet du tas
     * @throws IllegalArgumentException si <code>count</code> n'est pas compris entre <code>0</code> (inclus) et la
     *                                  taille du tas (incluse)
     */
    public SortedBag<C> topCards(int count) {
        Objects.checkIndex(0, this.cards.size() + 1);

        return SortedBag.of(this.cards.subList(0, count));
    }

    /**
     * Retourne un tas identique au récepteur <i>this</i> mais sans les <i>count</i> cartes du sommet, ou lève
     * <i>IllegalArgumentException</i> si <i>count</i> n'est pas compris entre <i>0</i> (inclus) et la taille du tas
     * (incluse)
     *
     * @param count nombre de cartes à prendree en compte
     * @return un tas identique au récepteur <code>this</code> mais sans les <code>count</code> cartes du sommet
     * @throws IllegalArgumentException si <code>count</code> n'est pas compris entre <code>0</code> (inclus) et la
     *                                  taille du tas (incluse)
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= this.size());

        return new Deck<>(this.cards.subList(count, this.cards.size()));
    }
}
