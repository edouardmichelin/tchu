package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Classe de test pour CardState
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class CardStateTest {

    //Un multi-ensemble de cartes non-trivial 17 cartes + 5 locomotives
    SortedBag<Card> cardsA = new SortedBag.Builder<Card>()
            .add(5, Card.LOCOMOTIVE)
            .add(3, Card.BLACK)
            .add(4, Card.BLUE)
            .add(1, Card.WHITE)
            .add(6, Card.RED)
            .add(2, Card.GREEN)
            .add(1, Card.ORANGE)
            .build();

    //Un CardState non-trivial
    CardState cardStateA = CardState.of(Deck.of(cardsA, new Random()));


    @Test
    void ofFailsWithIllegalDeckSize() {
        Deck a = Deck.of(SortedBag.of(Card.WHITE), new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(a);
        });
    }

    @Test
    void ofWorksWithNonTrivialDeck() {
        Deck a = Deck.of(cardsA, new Random());
        CardState test = CardState.of(a);

        assertEquals(5, test.faceUpCards().size());
    }

    @Test
    void withDrawnFaceUpCardFailsOnOutOfBoundsIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cardStateA.withDrawnFaceUpCard(5);
        });
    }

    @Test
    void withDrawnFaceUpCardFailsOnEmtpyDeck() {
        Deck<Card> deck = Deck.of(SortedBag.of(5, Card.BLUE), new Random());
        CardState a = CardState.of(deck);
        assertThrows(IllegalArgumentException.class, () -> {
            a.withDrawnFaceUpCard(0);
        });
    }

    @Test
    void withDrawnFaceUpCardWorksOnNonTrivialCardState() {
        cardStateA.withDrawnFaceUpCard(0);
        assertEquals(16, cardStateA.deckSize());
        assertEquals(5, cardStateA.faceUpCards().size());
    }
}
