package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

public class PublicCardStateTest {

    //Card list size 4
    List<Card> faceUpCards4 = List.of(
            Card.WHITE,
            Card.LOCOMOTIVE,
            Card.BLUE,
            Card.BLACK
    );

    //Card list size 5
    List<Card> faceUpCards5 = List.of(
            Card.WHITE,
            Card.LOCOMOTIVE,
            Card.BLUE,
            Card.BLACK,
            Card.LOCOMOTIVE
    );


    @Test
    void publicCardStateFailsOnIllegalArguments() {
        int deckSize = -2;
        int discardSize = -1;

        assertThrows(IllegalArgumentException.class, () -> {
            PublicCardState a = new PublicCardState(faceUpCards4, deckSize, discardSize);
            a = new PublicCardState(faceUpCards5, deckSize, discardSize);
            a = new PublicCardState(faceUpCards5, deckSize + 2, discardSize);
            a = new PublicCardState(faceUpCards5, deckSize, discardSize + 1);
            a = new PublicCardState(faceUpCards4, deckSize + 2, discardSize + 1);
        });
    }

    @Test
    void totalSizeReturnsExpectedValue() {
        int expectedSize = 15;
        PublicCardState a = new PublicCardState(faceUpCards5, 0, 10);
        assertEquals(expectedSize, a.totalSize());

        a = new PublicCardState(faceUpCards5, 10, 0);
        assertEquals(expectedSize, a.totalSize());

        a = new PublicCardState(faceUpCards5, 5, 5);
        assertEquals(expectedSize, a.totalSize());

        expectedSize = 5;
        a = new PublicCardState(faceUpCards5, 0, 0);
        assertEquals(expectedSize, a.totalSize());

        expectedSize = 36;
        a = new PublicCardState(faceUpCards5, 14, 17);
        assertEquals(expectedSize, a.totalSize());
    }

    @Test
    void faceUpCardsReturnsExpectedList() {
        PublicCardState a = new PublicCardState(faceUpCards5, 0, 0);
        assertEquals(faceUpCards5, a.faceUpCards());
    }

    @Test
    void faceUpCardsFailsOnIllegalSlot() {
        PublicCardState a = new PublicCardState(faceUpCards5, 0, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a.faceUpCard(6);
        });
    }

    @Test
    void faceUpCardsReturnsExpectedCard() {
        PublicCardState a = new PublicCardState(faceUpCards5, 0, 0);
        assertEquals(Card.BLUE, a.faceUpCard(2));
    }

    @Test
    void deckSizeReturnsExpectedValue() {
        int expectedSize = 15;
        PublicCardState a = new PublicCardState(faceUpCards5, expectedSize, 0);
        assertEquals(expectedSize, a.deckSize());

        expectedSize = 0;
        a = new PublicCardState(faceUpCards5, expectedSize, 0);
        assertEquals(expectedSize, a.deckSize());
    }

    @Test
    void isDeckEmptyReturnsExpectedBoolean() {
        PublicCardState a = new PublicCardState(faceUpCards5, 0, 0);
        assertTrue(a.isDeckEmpty());

        a = new PublicCardState(faceUpCards5, 1, 0);
        assertFalse(a.isDeckEmpty());
    }

    @Test
    void discardSizeReturnsExpectedSize() {
        int expectedSize = 15;
        PublicCardState a = new PublicCardState(faceUpCards5, 0, expectedSize);
        assertEquals(expectedSize, a.discardsSize());

        expectedSize = 0;
        a = new PublicCardState(faceUpCards5, 0, expectedSize);
        assertEquals(expectedSize, a.discardsSize());
    }
}
