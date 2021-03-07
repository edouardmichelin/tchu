package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Représente l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class CardState extends PublicCardState {
    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        super(faceUpCards, deckSize, discardsSize);
    }

    /**
     * Retourne un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné, la pioche est constituée des cartes du tas restantes, et la défausse est vide ; lève IllegalArgumentException si le tas donné contient moins de 5 cartes
     * @param deck
     * @return  un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné, la pioche est constituée des cartes du tas restantes, et la défausse est vide ; lève IllegalArgumentException si le tas donné contient moins de 5 cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() < 5);

        return null;
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée lève IndexOutOfBoundsException (!) si l'index donné n'est pas compris entre 0 (inclus) et 5 (exclus), ou IllegalArgumentException si la pioche est vide
     * @param slot
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée lève IndexOutOfBoundsException (!) si l'index donné n'est pas compris entre 0 (inclus) et 5 (exclus), ou IllegalArgumentException si la pioche est vide
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, 5);
        return null;
    }

    /**
     * Retourne la carte se trouvant au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     * @return la carte se trouvant au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard() {
        return null;
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche; lève IllegalArgumentException si la pioche est vide
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche; lève IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard() {
        return null;
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées au moyen du générateur aléatoire donné afin de constituer la nouvelle pioche lève IllegalArgumentException si la pioche du récepteur n'est pas vide
     * @param rng
     * @return
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        return null;
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     * @param additionalDiscards
     * @return un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return null;
    }
}
