/**
 * Classe de test pour Route
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    @Test
    void routeFailsOnEqualsStations() {
        Station station = ChMap.stations().get(0);
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("test", station, station, 2, Level.OVERGROUND, Color.values()[0]);
        });
    }

    @Test
    void routeFailsOnOutOfBoundsLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                    Constants.MAX_ROUTE_LENGTH + 1, Level.UNDERGROUND, Color.values()[0]);
        });
    }

    @Test
    void routeFailsOnNullLevel() {
        assertThrows(NullPointerException.class, () -> {
            new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                    Constants.MAX_ROUTE_LENGTH, null, Color.values()[0]);
        });
    }

    @Test
    void routeFailsOnNullStation1() {
        assertThrows(NullPointerException.class, () -> {
            new Route("test", null, ChMap.stations().get(0),
                    Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);
        });
    }

    @Test
    void routeFailsOnNullStation2() {
        assertThrows(NullPointerException.class, () -> {
            new Route("test", ChMap.stations().get(0), null,
                    Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);
        });
    }

    @Test
    void routeWorksOnNullColor() {
        Color expectedColor = null;
        Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                Constants.MIN_ROUTE_LENGTH, Level.OVERGROUND, null);

        assertTrue(expectedColor == a.color());
    }

    @Test
    void idReturnsExpectedId() {
        String expectedId = "ExpectedString1234";
        Route a = new Route(expectedId, ChMap.stations().get(0), ChMap.stations().get(1),
                Constants.MIN_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);

        assertEquals(expectedId, a.id());
    }

    @Test
    void station1ReturnsExpectedStation1() {
        Station expectedStation = ChMap.stations().get(0);

        Route a = new Route("test", expectedStation, ChMap.stations().get(1),
                Constants.MIN_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);

        assertEquals(expectedStation, a.station1());
    }

    @Test
    void station2ReturnsExpectedStation2() {
        Station expectedStation = ChMap.stations().get(1);
        Route a = new Route("test", ChMap.stations().get(0), expectedStation,
                Constants.MIN_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);

        assertEquals(expectedStation, a.station2());
    }

    @Test
    void lengthReturnsExpectedLength() {
        for (int i = Constants.MIN_ROUTE_LENGTH; i <= Constants.MAX_ROUTE_LENGTH; i++) {
            Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                    i, Level.OVERGROUND, Color.values()[0]);

            assertEquals(i, a.length());
        }
    }

    @Test
    void levelReturnsExpectedLevel() {
        for (Level level : Level.values()) {
            Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                    Constants.MAX_ROUTE_LENGTH, level, Color.values()[0]);
            assertEquals(level, a.level());
        }
    }

    @Test
    void colorReturnsExpectedColor() {
        for (Color color : Color.values()) {
            Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                    Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, color);
            assertEquals(color, a.color());
        }

        Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, null);
        assertNull(a.color());
    }

    @Test
    void stationsReturnsExpectedStations() {
        List<Station> expectedStations = List.of(ChMap.stations().get(0), ChMap.stations().get(1));
        Route a = new Route("test", expectedStations.get(0), expectedStations.get(1),
                Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);
        assertEquals(expectedStations, a.stations());
    }

    @Test
    void stationOppositeFailsOnIllegalStation() {
        Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);

        assertThrows(IllegalArgumentException.class, () -> {
            a.stationOpposite(ChMap.stations().get(2));
        });
    }

    @Test
    void stationOppositeReturnsExpectedStation() {
        Station expectedStation1 = ChMap.stations().get(0);
        Station expectedStation2 = ChMap.stations().get(1);

        Route a = new Route("test", expectedStation1, expectedStation2,
                Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);
        assertEquals(expectedStation1, a.stationOpposite(expectedStation2));
        assertEquals(expectedStation2, a.stationOpposite(expectedStation1));
    }

    @Test
    void possibleClaimCardsWorksOnColoredTunnel() {
        //tunnel bleu de taille 3
        List<SortedBag<Card>> expectedList = List.of(
                SortedBag.of(3, Card.BLUE),
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.BLUE, 2, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.LOCOMOTIVE)
        );

        Route a = new Route("Tunnel bleu taille 3", ChMap.stations().get(0), ChMap.stations().get(1),
                3, Level.UNDERGROUND, Color.BLUE);

        assertEquals(expectedList, a.possibleClaimCards());
    }

    @Test
    void possibleClaimCardsWorksOnNeutralTunnel() {
        //Tunnel neutre taille 3
        List<SortedBag<Card>> expectedList = List.of(
                SortedBag.of(3, Card.BLACK),
                SortedBag.of(3, Card.VIOLET),
                SortedBag.of(3, Card.BLUE),
                SortedBag.of(3, Card.GREEN),
                SortedBag.of(3, Card.YELLOW),
                SortedBag.of(3, Card.ORANGE),
                SortedBag.of(3, Card.RED),
                SortedBag.of(3, Card.WHITE),
                SortedBag.of(2, Card.BLACK, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.VIOLET, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.GREEN, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.YELLOW, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.ORANGE, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.RED, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.WHITE, 1, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.BLACK, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.VIOLET, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.BLUE, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.GREEN, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.YELLOW, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.ORANGE, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.RED, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.WHITE, 2, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.LOCOMOTIVE)
        );

        Route a = new Route("Tunnel neutre taille 3", ChMap.stations().get(0), ChMap.stations().get(1),
                3, Level.UNDERGROUND, null);

        assertEquals(expectedList, a.possibleClaimCards());

        //Tunnel neutre taille 1
        expectedList = List.of(
                SortedBag.of(3, Card.BLACK),
                SortedBag.of(3, Card.VIOLET),
                SortedBag.of(3, Card.BLUE),
                SortedBag.of(3, Card.GREEN),
                SortedBag.of(3, Card.YELLOW),
                SortedBag.of(3, Card.ORANGE),
                SortedBag.of(3, Card.RED),
                SortedBag.of(3, Card.WHITE),
                SortedBag.of(2, Card.BLACK, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.VIOLET, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.GREEN, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.YELLOW, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.ORANGE, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.RED, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.WHITE, 1, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.BLACK, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.VIOLET, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.BLUE, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.GREEN, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.YELLOW, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.ORANGE, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.RED, 2, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.WHITE, 2, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.LOCOMOTIVE)
        );

        a = new Route("Tunnel neutre taille 3", ChMap.stations().get(0), ChMap.stations().get(1),
                3, Level.UNDERGROUND, null);

        assertEquals(expectedList, a.possibleClaimCards());
    }

    @Test
    void possibleClaimCardsWorksOnColoredRoad() {
        //Route bleu de taille 3
        List<SortedBag<Card>> expectedList = List.of(
                SortedBag.of(3, Card.BLUE)
        );

        Route a = new Route("Tunnel bleu taille 3", ChMap.stations().get(0), ChMap.stations().get(1),
                3, Level.OVERGROUND, Color.BLUE);

        assertEquals(expectedList, a.possibleClaimCards());

        //Route bleu de taille 1
        expectedList = List.of(
                SortedBag.of(Card.BLUE)
        );

        a = new Route("Tunnel bleu taille 3", ChMap.stations().get(0), ChMap.stations().get(1),
                1, Level.OVERGROUND, Color.BLUE);

        assertEquals(expectedList, a.possibleClaimCards());
    }

    @Test
    void possibleClaimCardsWorksOnNeutralRoad() {
        //Route neutre de taille 3
        List<SortedBag<Card>> expectedList = List.of(
                SortedBag.of(3, Card.BLACK),
                SortedBag.of(3, Card.VIOLET),
                SortedBag.of(3, Card.BLUE),
                SortedBag.of(3, Card.GREEN),
                SortedBag.of(3, Card.YELLOW),
                SortedBag.of(3, Card.ORANGE),
                SortedBag.of(3, Card.RED),
                SortedBag.of(3, Card.WHITE)
        );

        Route a = new Route("Tunnel bleu taille 3", ChMap.stations().get(0), ChMap.stations().get(1),
                3, Level.OVERGROUND, null);

        assertEquals(expectedList, a.possibleClaimCards());

        //Route neutre de taille 1
        expectedList = List.of(
                SortedBag.of(Card.BLACK),
                SortedBag.of(Card.VIOLET),
                SortedBag.of(Card.BLUE),
                SortedBag.of(Card.GREEN),
                SortedBag.of(Card.YELLOW),
                SortedBag.of(Card.ORANGE),
                SortedBag.of(Card.RED),
                SortedBag.of(Card.WHITE)
        );

        a = new Route("Tunnel bleu taille 3", ChMap.stations().get(0), ChMap.stations().get(1),
                1, Level.OVERGROUND, null);

        assertEquals(expectedList, a.possibleClaimCards());
    }

    @Test
    void additionalClaimCardsCountFailsOnIncorrectDrawnCardsNumber() {
        SortedBag<Card> claimCards = SortedBag.of(4, Card.values()[0]);
        SortedBag<Card> drawnCards = SortedBag.of(4, Card.values()[1]);

        Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                Constants.MAX_ROUTE_LENGTH, Level.UNDERGROUND, Color.values()[0]);

        assertThrows(IllegalArgumentException.class, () -> {
            a.additionalClaimCardsCount(claimCards, drawnCards);
        });
    }

    @Test
    void additionalClaimCardsCountFailsOnOverground() {
        SortedBag<Card> claimCards = SortedBag.of(5, Card.values()[0]);
        SortedBag<Card> drawnCards = SortedBag.of(3, Card.values()[1]);

        Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                Constants.MAX_ROUTE_LENGTH, Level.OVERGROUND, Color.values()[0]);

        assertThrows(IllegalArgumentException.class, () -> {
            a.additionalClaimCardsCount(claimCards, drawnCards);
        });
    }

    @Test
    void additionalClaimCardsCountReturnsExpectedCount() {
        SortedBag<Card> claimCards = SortedBag.of(5, Card.RED);
        SortedBag<Card> drawnCards = SortedBag.of(List.of(
                Card.BLACK,
                Card.LOCOMOTIVE,
                Card.RED
        ));

        Route a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                Constants.MAX_ROUTE_LENGTH, Level.UNDERGROUND, Color.values()[0]);

        assertEquals(2, a.additionalClaimCardsCount(claimCards, drawnCards));

        claimCards = SortedBag.of(Card.WHITE);
        assertEquals(1, a.additionalClaimCardsCount(claimCards, drawnCards));

        drawnCards = SortedBag.of(3, Card.LOCOMOTIVE);
        assertEquals(3, a.additionalClaimCardsCount(claimCards, drawnCards));

        drawnCards = SortedBag.of(2, Card.WHITE, 1, Card.LOCOMOTIVE);
        assertEquals(3, a.additionalClaimCardsCount(claimCards, drawnCards));

        claimCards = SortedBag.of(2, Card.LOCOMOTIVE);
        assertEquals(1, a.additionalClaimCardsCount(claimCards, drawnCards));

        drawnCards = SortedBag.of(3, Card.BLACK);
        assertEquals(0, a.additionalClaimCardsCount(claimCards, drawnCards));
    }

    @Test
    void claimPointsReturnsExpectedValue() {
        Route a;
        int maxIteration = Constants.MAX_ROUTE_LENGTH - Constants.MIN_ROUTE_LENGTH;

        for (int i = 0; i < maxIteration; i++) {
            int expectedValue = Constants.ROUTE_CLAIM_POINTS.get(i + 1);
            a = new Route("test", ChMap.stations().get(0), ChMap.stations().get(1),
                    Constants.MIN_ROUTE_LENGTH + i, Level.UNDERGROUND, Color.values()[0]);

            assertEquals(expectedValue, a.claimPoints());
        }
    }
}