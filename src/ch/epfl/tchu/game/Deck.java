package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Random;

/**
 * Représente un tas de cartes
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Deck<C extends Comparable<C>> {
    private Deck() {}

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        return null;
    }

    /**
     * Retourne la taille du tas, c-à-d le nombre de cartes qu'il contient
     * @return la taille du tas, c-à-d le nombre de cartes qu'il contient
     */
    public int size() {
        return 0;
    }

    /**
     * Retourne vrai ssi le tas est vide
     * @return vrai ssi le tas est vide
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Retourne la carte au sommet du tas, ou lève IllegalArgumentException si le tas est vide
     * @return la carte au sommet du tas, ou lève IllegalArgumentException si le tas est vide
     */
    public C topCard() {
        return null;
    }

    /**
     * Retourne un tas identique au récepteur (this) mais sans la carte au sommet, ou lève IllegalArgumentException si le tas est vide
     * @return un tas identique au récepteur (this) mais sans la carte au sommet, ou lève IllegalArgumentException si le tas est vide
     */
    public Deck<C> withoutTopCard() {
        return null;
    }

    /**
     * Retourne un multiensemble contenant les count cartes se trouvant au sommet du tas; lève IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @param count
     * @return un multiensemble contenant les count cartes se trouvant au sommet du tas; lève IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     */
    public SortedBag<C> topCards(int count) {
        return null;
    }

    /**
     * Retourne un tas identique au récepteur (this) mais sans les count cartes du sommet, ou lève IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @param count
     * @return un tas identique au récepteur (this) mais sans les count cartes du sommet, ou lève IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     */
    public Deck<C> withoutTopCards(int count) {
        return null;
    }
}
