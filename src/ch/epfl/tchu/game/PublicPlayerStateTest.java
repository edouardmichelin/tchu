package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour PublicPlayerSTate
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class PublicPlayerStateTest {
    //Stations
    Station s1 = new Station(1, "Lausanne");
    Station s2 = new Station(2, "Fribourg");
    Station s3 = new Station(3, "Berne");
    Station s4 = new Station(4, "Interlaken");
    Station s5 = new Station(5, "Neuchâtel");
    Station s6 = new Station(6, "Soleure");
    Station s7 = new Station(7, "Olten");
    Station s8 = new Station(8, "Lucerne");
    Station s9 = new Station(9, "Zoug");
    Station s10 = new Station(10, "Schwyz");
    Station s11 = new Station(11, "Wassen");

    //Route cases
    private List<Route> givenCase = List.of(
            new Route("LauFri", s1, s2, 3, Route.Level.UNDERGROUND, null),
            new Route("FriBer", s2, s3, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BerInt", s3, s4, 3, Route.Level.UNDERGROUND, Color.RED),
            new Route("NeuSol", s5, s6, 4, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("SolOlt", s6, s7, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("LucZou", s8, s9, 1, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("LucSch", s8, s10, 1, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("ZouSch", s9, s10, 1, Route.Level.OVERGROUND, null),
            new Route("SchWas", s10, s11, 2, Route.Level.UNDERGROUND, Color.ORANGE)
    );

    @Test
    void publicPlayerStateFailsOnIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(-2, 2, givenCase);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(1, -2, givenCase);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(-2, -2, givenCase);
        });
    }

    @Test
    void ticketCountReturnsExpectedValue() {
        int expectedValue = 7;
        PublicPlayerState a = new PublicPlayerState(expectedValue, 0, givenCase);
        assertEquals(expectedValue, a.ticketCount());
    }

    @Test
    void cardCountReturnsExpectedValue() {
        int expectedValue = 5;
        PublicPlayerState a = new PublicPlayerState(0, expectedValue, givenCase);
        assertEquals(expectedValue, a.cardCount());
    }

    @Test
    void routesReturnExpectedRoutes() {
        PublicPlayerState a = new PublicPlayerState(0, 0, givenCase);
        assertEquals(givenCase, a.routes());
    }

    @Test
    void carCountReturnsExpectedValue() {
        int expectedValue = 23;
        PublicPlayerState a = new PublicPlayerState(0, 0, givenCase);
        assertEquals(expectedValue, a.carCount());

        PublicPlayerState b = new PublicPlayerState(0, 0, List.of());
        assertEquals(Constants.INITIAL_CAR_COUNT, b.carCount());
    }

    @Test
    void claimPointsReturnsExpectedValue() {
        int expectedValue = 22;
        PublicPlayerState a = new PublicPlayerState(0, 0, givenCase);
        assertEquals(expectedValue, a.claimPoints());
    }

    @Test
    void publicPlayerStateIsImmutable() {
        List<Route> routes = new ArrayList<>(givenCase);
        PublicPlayerState a = new PublicPlayerState(0, 0, routes);
        routes.clear();
        assertEquals(givenCase, a.routes());

        try {
            a.routes().clear();
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        assertEquals(givenCase, a.routes());
    }
}
