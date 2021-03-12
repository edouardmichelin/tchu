package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;



/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class DeckTest {
    SortedBag<Card> cards = SortedBag.of(List.of(Card.BLUE, Card.WHITE, Card.RED, Card.BLACK));
    SortedBag<Card> blueCards = SortedBag.of(List.of(Card.BLUE, Card.BLUE, Card.BLUE, Card.BLUE));
    SortedBag<Card> manyCards = new SortedBag.Builder<Card>()
            .add(5, Card.LOCOMOTIVE)
            .add(3, Card.BLACK)
            .add(4, Card.BLUE)
            .add(1, Card.WHITE)
            .add(6, Card.RED)
            .add(2, Card.GREEN)
            .add(1, Card.ORANGE)
            .build();

    @Test
    public void sizeReturnsExpectedSize() {
        var expectedSize = 4;
        var random = new Random();
        var deck = Deck.of(cards, random);

        assertEquals(expectedSize, deck.size());
    }

    @Test
    public void isEmptyReturnsExpectedResultOnNonEmpty() {
        var random = new Random();
        var deck = Deck.of(cards, random);

        assertFalse(deck.isEmpty());
    }

    @Test
    public void withoutTopCardReturnsExpectedResult() {
        var random = new Random();
        var deck = Deck.of(cards, random);

        var newDeck = deck.withoutTopCard();

        System.out.println(deck);
    }

    @Test
    public void withoutTopCardsReturnsExpectedResult() {
        var random = new Random();
        var deck = Deck.of(manyCards, random);

        var newDeck = deck.withoutTopCards(5);

        System.out.println(deck);
    }

    @Test
    public void topCardsReturnsExpectedResult() {
    }

    @Test
    public void topCardsThrowsIllegalArgumentExceptionOnBadArgument() {
        var random = new Random();
        var deck = Deck.of(cards, random);

        // assertThrows(IndexOutOfBoundsException.class, () -> deck.topCards(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> deck.topCards(5));

    }


}
