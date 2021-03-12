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
        var deck = Deck.of(blueCards, random);

        var newDeck = deck.withoutTopCards(2);

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
