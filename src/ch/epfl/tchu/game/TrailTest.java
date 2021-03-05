package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Trail tests
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class TrailTest {

    @Test
    void findAllRouteConnectionsReturnsExpectedRoutes() {
        var allRoutes = ChMap.routes();
        var expectedLongest = 13;

        var result = Trail.longest(List.of(allRoutes.get(13), allRoutes.get(16), allRoutes.get(18), allRoutes.get(19), allRoutes.get(65), allRoutes.get(66)));
        // var result = Trail.longest(allRoutes.subList(0, 40));

        assertEquals(expectedLongest, result.length());
    }
}
