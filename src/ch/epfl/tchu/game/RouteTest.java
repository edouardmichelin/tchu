package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour Route
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
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

    private static final List<Color> COLORS =
            List.of(
                    Color.BLACK,
                    Color.VIOLET,
                    Color.BLUE,
                    Color.GREEN,
                    Color.YELLOW,
                    Color.ORANGE,
                    Color.RED,
                    Color.WHITE);
    private static final List<Card> CAR_CARDS =
            List.of(
                    Card.BLACK,
                    Card.VIOLET,
                    Card.BLUE,
                    Card.GREEN,
                    Card.YELLOW,
                    Card.ORANGE,
                    Card.RED,
                    Card.WHITE);

    @Test
    void routeConstructorFailsWhenBothStationsAreEqual() {
        var s = new Station(0, "Lausanne");
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s, s, 1, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenLengthIsInvalid() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s1, s2, 0, Level.OVERGROUND, Color.BLACK);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", s1, s2, 7, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenIdIsNull() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route(null, s1, s2, 1, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenOneStationIsNull() {
        var s = new Station(0, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route("id", null, s, 1, Level.OVERGROUND, Color.BLACK);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("id", s, null, 1, Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsWhenLevelIsNull() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        assertThrows(NullPointerException.class, () -> {
            new Route("id", s1, s2, 1, null, Color.BLACK);
        });
    }

    @Test
    void routeIdReturnsId() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var routes = new Route[100];
        for (int i = 0; i < routes.length; i++)
            routes[i] = new Route("id" + i, s1, s2, 1, Level.OVERGROUND, Color.BLACK);
        for (int i = 0; i < routes.length; i++)
            assertEquals("id" + i, routes[i].id());
    }

    @Test
    void routeStation1And2ReturnStation1And2() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var r = routes[i];
            assertEquals(s1, r.station1());
            assertEquals(s2, r.station2());
        }
    }

    @Test
    void routeLengthReturnsLength() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var routes = new Route[6];
        for (var l = 1; l <= 6; l++)
            routes[l - 1] = new Route(id, s1, s2, l, Level.OVERGROUND, Color.BLACK);
        for (var l = 1; l <= 6; l++)
            assertEquals(l, routes[l - 1].length());

    }

    @Test
    void routeLevelReturnsLevel() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var ro = new Route(id, s1, s2, 1, Level.OVERGROUND, Color.BLACK);
        var ru = new Route(id, s1, s2, 1, Level.UNDERGROUND, Color.BLACK);
        assertEquals(Level.OVERGROUND, ro.level());
        assertEquals(Level.UNDERGROUND, ru.level());
    }

    @Test
    void routeColorReturnsColor() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        var routes = new Route[8];
        for (var c : COLORS)
            routes[c.ordinal()] = new Route(id, s1, s2, 1, Level.OVERGROUND, c);
        for (var c : COLORS)
            assertEquals(c, routes[c.ordinal()].color());
        var r = new Route(id, s1, s2, 1, Level.OVERGROUND, null);
        assertNull(r.color());
    }

    @Test
    void routeStationsReturnsStations() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            assertEquals(List.of(s1, s2), routes[i].stations());
        }
    }

    @Test
    void routeStationOppositeFailsWithInvalidStation() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var s3 = new Station(1, "EPFL");
        var r = new Route("id", s1, s2, 1, Level.OVERGROUND, Color.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            r.stationOpposite(s3);
        });
    }

    @Test
    void routeStationOppositeReturnsOppositeStation() {
        var rng = TestRandomizer.newRandom();
        var stations = new Station[100];
        for (int i = 0; i < stations.length; i++)
            stations[i] = new Station(i, "Station " + i);
        var routes = new Route[100];
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var l = 1 + rng.nextInt(6);
            routes[i] = new Route("r" + i, s1, s2, l, Level.OVERGROUND, Color.RED);
        }
        for (int i = 0; i < stations.length; i++) {
            var s1 = stations[i];
            var s2 = stations[(i + 1) % 100];
            var r = routes[i];
            assertEquals(s1, r.stationOpposite(s2));
            assertEquals(s2, r.stationOpposite(s1));
        }
    }

    @Test
    void routePossibleClaimCardsWorksForOvergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Level.OVERGROUND, color);
                assertEquals(List.of(SortedBag.of(l, card)), r.possibleClaimCards());
            }
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnOvergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Level.OVERGROUND, null);
            var expected = List.of(
                    SortedBag.of(l, Card.BLACK),
                    SortedBag.of(l, Card.VIOLET),
                    SortedBag.of(l, Card.BLUE),
                    SortedBag.of(l, Card.GREEN),
                    SortedBag.of(l, Card.YELLOW),
                    SortedBag.of(l, Card.ORANGE),
                    SortedBag.of(l, Card.RED),
                    SortedBag.of(l, Card.WHITE));
            assertEquals(expected, r.possibleClaimCards());
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnUndergroundColoredRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var i = 0; i < COLORS.size(); i++) {
            var color = COLORS.get(i);
            var card = CAR_CARDS.get(i);
            for (var l = 1; l <= 6; l++) {
                var r = new Route(id, s1, s2, l, Level.UNDERGROUND, color);

                var expected = new ArrayList<SortedBag<Card>>();
                for (var locomotives = 0; locomotives <= l; locomotives++) {
                    var cars = l - locomotives;
                    expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
                assertEquals(expected, r.possibleClaimCards());
            }
        }
    }

    @Test
    void routePossibleClaimCardsWorksOnUndergroundNeutralRoute() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Level.UNDERGROUND, null);

            var expected = new ArrayList<SortedBag<Card>>();
            for (var locomotives = 0; locomotives <= l; locomotives++) {
                var cars = l - locomotives;
                if (cars == 0)
                    expected.add(SortedBag.of(locomotives, Card.LOCOMOTIVE));
                else {
                    for (var card : CAR_CARDS)
                        expected.add(SortedBag.of(cars, card, locomotives, Card.LOCOMOTIVE));
                }
            }
            assertEquals(expected, r.possibleClaimCards());
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithColoredCardsOnly() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 1; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                var claimCards = SortedBag.of(l, matchingCard);
                var r = new Route(id, s1, s2, l, Level.UNDERGROUND, color);
                for (var m = 0; m <= 3; m++) {
                    for (var locomotives = 0; locomotives <= m; locomotives++) {
                        var drawnB = new SortedBag.Builder<Card>();
                        drawnB.add(locomotives, Card.LOCOMOTIVE);
                        drawnB.add(m - locomotives, matchingCard);
                        drawnB.add(3 - m, nonMatchingCard);
                        var drawn = drawnB.build();
                        assertEquals(m, r.additionalClaimCardsCount(claimCards, drawn));
                    }
                }
            }
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithLocomotivesOnly() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 1; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                var claimCards = SortedBag.of(l, Card.LOCOMOTIVE);
                var r = new Route(id, s1, s2, l, Level.UNDERGROUND, color);
                for (var m = 0; m <= 3; m++) {
                    for (var locomotives = 0; locomotives <= m; locomotives++) {
                        var drawnB = new SortedBag.Builder<Card>();
                        drawnB.add(locomotives, Card.LOCOMOTIVE);
                        drawnB.add(m - locomotives, matchingCard);
                        drawnB.add(3 - m, nonMatchingCard);
                        var drawn = drawnB.build();
                        assertEquals(locomotives, r.additionalClaimCardsCount(claimCards, drawn));
                    }
                }
            }
        }
    }

    @Test
    void routeAdditionalClaimCardsCountWorksWithMixedCards() {
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        for (var l = 2; l <= 6; l++) {
            for (var color : COLORS) {
                var matchingCard = CAR_CARDS.get(color.ordinal());
                var nonMatchingCard = color == Color.BLACK
                        ? Card.WHITE
                        : Card.BLACK;
                for (var claimLoc = 1; claimLoc < l; claimLoc++) {
                    var claimCards = SortedBag.of(
                            l - claimLoc, matchingCard,
                            claimLoc, Card.LOCOMOTIVE);
                    var r = new Route(id, s1, s2, l, Level.UNDERGROUND, color);
                    for (var m = 0; m <= 3; m++) {
                        for (var locomotives = 0; locomotives <= m; locomotives++) {
                            var drawnB = new SortedBag.Builder<Card>();
                            drawnB.add(locomotives, Card.LOCOMOTIVE);
                            drawnB.add(m - locomotives, matchingCard);
                            drawnB.add(3 - m, nonMatchingCard);
                            var drawn = drawnB.build();
                            assertEquals(m, r.additionalClaimCardsCount(claimCards, drawn));
                        }
                    }
                }
            }
        }
    }

    @Test
    void routeClaimPointsReturnsClaimPoints() {
        var expectedClaimPoints =
                List.of(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15);
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";
        for (var l = 1; l <= 6; l++) {
            var r = new Route(id, s1, s2, l, Level.OVERGROUND, Color.BLACK);
            assertEquals(expectedClaimPoints.get(l), r.claimPoints());
        }
    }
}