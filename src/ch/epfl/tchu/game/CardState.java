package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Représente l'état complet des cartes wagon/locomotive qui ne sont pas en main des joueurs
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discards;

    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discarded, int deckSize, int discardsSize) {
        super(faceUpCards, deckSize, discardsSize);
        this.deck = deck;
        this.discards = discarded;
    }

    /**
     * Retourne un état dans lequel les cartes disposées faces visibles sont les 5 premières du tas donné, la pioche est constituée des cartes du tas restantes, et la défausse est vide ; lève IllegalArgumentException si le tas donné contient moins de 5 cartes
     *
     * @param deck
     * @return un état dans lequel les cartes disposées faces visibles sont les 5 premières du tas donné, la pioche est constituée des cartes du tas restantes, et la défausse est vide ; lève IllegalArgumentException si le tas donné contient moins de 5 cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(!(deck.size() < Constants.FACE_UP_CARDS_COUNT));
        List<Card> faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();
        Deck<Card> remainingDeck = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);

        return new CardState(faceUpCards, remainingDeck, SortedBag.of(), remainingDeck.size(), 0);
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée lève IndexOutOfBoundsException (!) si l'index donné n'est pas compris entre 0 (inclus) et 5 (exclus), ou IllegalArgumentException si la pioche est vide
     *
     * @param slot Identifiant d'emplacement de la carte visible qui est tirée
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée lève IndexOutOfBoundsException (!) si l'index donné n'est pas compris entre 0 (inclus) et 5 (exclus), ou IllegalArgumentException si la pioche est vide
     */
    public CardState withDrawnFaceUpCard(int slot) {
        List<Card> faceUpCards = this.faceUpCards();
        faceUpCards.set(Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT), this.deck.topCard());
        Deck<Card> remainingDeck = this.deck.withoutTopCard();

        return new CardState(faceUpCards, remainingDeck, this.discards, remainingDeck.size(), this.discardsSize());
    }

    /**
     * Retourne la carte se trouvant au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     *
     * @return la carte se trouvant au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(this.deckSize() > 0);
        return this.deck.topCard();
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche; lève IllegalArgumentException si la pioche est vide
     *
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche; lève IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard() {
        Deck<Card> deck = this.deck.withoutTopCard();
        return new CardState(this.faceUpCards(), deck, this.discards, deck.size(), this.discardsSize());
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées au moyen du générateur aléatoire donné afin de constituer la nouvelle pioche lève IllegalArgumentException si la pioche du récepteur n'est pas vide
     *
     * @param rng Un générateur aléatoire pour le mélange des cartes
     * @return
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(this.isDeckEmpty());
        return new CardState(this.faceUpCards(), Deck.of(this.discards, rng), SortedBag.of(), this.discardsSize(), 0);
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     *
     * @param additionalDiscards Les cartes à rajouter à la défausse
     * @return un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        SortedBag<Card> discards = this.discards.union(additionalDiscards);
        return new CardState(this.faceUpCards(), this.deck, discards, this.deckSize(), discards.size());
    }
}
