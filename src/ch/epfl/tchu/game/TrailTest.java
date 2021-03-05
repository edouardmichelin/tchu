package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test du chemin
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class TrailTest {

    private final List<Route> NETWORK = List.of(
            ChMap.routes().get(13),
            ChMap.routes().get(16),
            ChMap.routes().get(18),
            ChMap.routes().get(19),
            ChMap.routes().get(65),
            ChMap.routes().get(66)
    );

    @Test
    void longestWorksOnEmptyRouteList() {
        Trail a = Trail.longest(new ArrayList<Route>());
        assertEquals(0, a.length());
        assertNull(a.station1());
        assertNull(a.station2());
    }

    @Test
    void lengthReturnsExpectedLength() {
        Trail a = Trail.longest(new ArrayList<Route>());
        assertEquals(0, a.length());

        a = Trail.longest(NETWORK);
        assertEquals(13, a.length());
    }

    @Test
    void station2ReturnsExpectedStation() {
        Trail a = Trail.longest(NETWORK);
        assertEquals(ChMap.stations().get(16), a.station2());
    }

    @Test
    void station1ReturnsExpectedStation() {
        Trail a = Trail.longest(NETWORK);
        assertEquals(ChMap.stations().get(9), a.station1());
    }

    @Test
    void toStringReturnsExpectedString() {
        String expectedString = "Fribourg - Berne - Neuchâtel - Soleure - Berne - Lucerne (13)";
        Trail a = Trail.longest(NETWORK);
        assertEquals(expectedString, a.toString());
    }
}