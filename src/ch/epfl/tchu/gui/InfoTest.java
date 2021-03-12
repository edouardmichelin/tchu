package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
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

    @Test
    public void test() {
        var info = new Info("Edouard");

        System.out.println(info.willPlayFirst());
        System.out.println(info.keptTickets(2));
        System.out.println(info.keptTickets(1));
        System.out.println(info.canPlay());
        System.out.println(info.drewTickets(2));
        System.out.println(info.drewTickets(1));
        System.out.println(info.drewBlindCard());
        System.out.println(info.drewVisibleCard(Card.GREEN));
        System.out.println(info.claimedRoute(ChMap.routes().get(0), SortedBag.of(3, Card.BLUE)));
        System.out.println(info.attemptsTunnelClaim(ChMap.routes().get(0), SortedBag.of(3, Card.BLUE)));
        System.out.println(info.drewAdditionalCards(SortedBag.of(3, Card.BLUE), 2));
        System.out.println(info.didNotClaimRoute(ChMap.routes().get(0)));
        System.out.println(info.lastTurnBegins(2));
        System.out.println(info.lastTurnBegins(1));
        System.out.println(info.won(2, 2));
        System.out.println(info.won(2, 1));
        System.out.println(info.won(1, 2));
        System.out.println(info.won(1, 1));
    }
}
