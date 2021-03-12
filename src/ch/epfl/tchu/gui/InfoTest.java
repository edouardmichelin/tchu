package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class InfoTest {
    @Test
    public void cardNameReturnsExpectedCardNameWithPlural() {
        var card = Card.GREEN;
        var expectedResult = "vertes";

        assert Info.cardName(card, 4).equals(expectedResult);
    }

    @Test
    public void cardNameReturnsExpectedCardName() {
        var card = Card.RED;
        var expectedResult = "rouge";

        assert Info.cardName(card, 1).equals(expectedResult);
    }

    @Test
    public void cardNameReturnsExpectedCardNameWithNegativeValue() {
        var card = Card.GREEN;
        var expectedResult = "verte";

        assert Info.cardName(card, -1).equals(expectedResult);
    }

    @Test
    public void cardNameReturnsExpectedCardNameWithNegativeValuePlural() {
        var card = Card.RED;
        var expectedResult = "rouges";

        assert Info.cardName(card, -9).equals(expectedResult);
    }

    @Test
    public void drawReturnsExpectedValueWithMoreThanTwoPlayers() {
        var playerNames = List.of("Julien", "Edouard", "Schinz", "Lachowska");

        assert Info.draw(playerNames, 20).equals("\nJulien, Edouard, Schinz et Lachowska sont ex æqo avec 20 points !\n");
    }

    @Test
    public void drawReturnsExpectedValueWithExactlyTwoPlayers() {
        var playerNames = List.of("Julien", "Edouard");
        var expectedValue = "\nJulien et Edouard sont ex æqo avec 20 points !\n";

        assertEquals(expectedValue, Info.draw(playerNames, 20));
    }
}
