package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Représente l'état complet des cartes wagon/locomotive qui ne sont pas en main des joueurs
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discards;

    private CardState(
            List<Card> faceUpCards,
            Deck<Card> deck,
            SortedBag<Card> discarded,
            int deckSize,
            int discardsSize
    ) {
        super(faceUpCards, deckSize, discardsSize);
        this.deck = deck;
        this.discards = discarded;
    }

    /**
     * Retourne un état dans lequel les cartes disposées faces visibles sont les 5 premières du tas donné, la pioche
     * est constituée des cartes du tas restantes, et la défausse est vide ; lève <i>IllegalArgumentException</i> si
     * le tas donné contient moins de 5 cartes
     *
     * @param deck le deck
     * @return un état dans lequel les cartes disposées faces visibles sont les 5 premières du tas donné, la pioche
     * est constituée des cartes du tas restantes, et la défausse est vide
     * @throws IllegalArgumentException si le tas donné contient moins de 5 cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(!(deck.size() < Constants.FACE_UP_CARDS_COUNT));
        List<Card> faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();
        Deck<Card> remainingDeck = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);

        return new CardState(faceUpCards, remainingDeck, SortedBag.of(), remainingDeck.size(), 0);
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur <i>this</i>, si ce n'est que la carte face visible d'index
     * <i>slot</i> a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée ; lève
     * <i>IndexOutOfBoundsException</i> si l'index donné n'est pas compris entre <i>0</i> (inclus) et <i>5</i>
     * (exclus), ou <i>IllegalArgumentException</i> si la pioche est vide
     *
     * @param slot Identifiant d'emplacement de la carte visible qui est tirée
     * @return un ensemble de cartes identique au récepteur <code>this</code>, si ce n'est que la carte face visible
     * d'index <code>slot</code> a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même
     * coup retirée
     * @throws IndexOutOfBoundsException si l'index donné n'est pas compris entre <code>0</code> (inclus) et
     *                                   <code>5</code> (exclus)
     * @throws IllegalArgumentException  si la pioche est vide
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!this.isDeckEmpty());
        List<Card> faceUpCards = this.faceUpCards();
        faceUpCards.set(Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT), this.deck.topCard());
        Deck<Card> remainingDeck = this.deck.withoutTopCard();

        return new CardState(faceUpCards, remainingDeck, this.discards, remainingDeck.size(), this.discardsSize());
    }

    /**
     * Retourne la carte se trouvant au sommet de la pioche, ou lève <i>IllegalArgumentException</i> si la pioche est
     * vide
     *
     * @return la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!this.isDeckEmpty());
        return this.deck.topCard();
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur <i>this</i>, mais sans la carte se trouvant au sommet de la
     * pioche; lève <i>IllegalArgumentException</i> si la pioche est vide
     *
     * @return un ensemble de cartes identique au récepteur <code>this</code>, mais sans la carte se trouvant au
     * sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard() {
        Deck<Card> deck = this.deck.withoutTopCard();
        return new CardState(this.faceUpCards(), deck, this.discards, deck.size(), this.discardsSize());
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur <i>this</i>, si ce n'est que les cartes de la défausse ont
     * été mélangées au moyen du générateur pseudo aléatoire donné afin de constituer la nouvelle pioche ; lève
     * <i>IllegalArgumentException</i> si la pioche du récepteur n'est pas vide
     *
     * @return un ensemble de cartes identique au récepteur <code>this</code>, si ce n'est que les cartes de la
     * défausse ont été mélangées au moyen du générateur pseudo aléatoire donné afin de constituer la nouvelle pioche
     * @throws IllegalArgumentException si la pioche du récepteur n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(this.isDeckEmpty());
        return new CardState(this.faceUpCards(), Deck.of(this.discards, rng), SortedBag.of(), this.discardsSize(), 0);
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur <i>this</i>, mais avec les cartes données ajoutées à la
     * défausse
     *
     * @param additionalDiscards Les cartes à rajouter à la défausse
     * @return un ensemble de cartes identique au récepteur <code>this</code>, mais avec les cartes données ajoutées
     * à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        SortedBag<Card> discards = this.discards.union(additionalDiscards);
        return new CardState(this.faceUpCards(), this.deck, discards, this.deckSize(), discards.size());
    }
}
