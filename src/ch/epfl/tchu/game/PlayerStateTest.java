package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour PlayerState
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class PlayerStateTest {

    //Stations cases
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

    //Tickets cases
    List<Ticket> ticketsA = List.of(
            new Ticket(s5, s7, 5), //ok on given example
            new Ticket(s5, s2, 3), //not ok
            new Ticket(s9, s11, 3), //ok
            new Ticket(s1, s4, 7), //ok
            new Ticket(s1, s8, 8), //not ok
            new Ticket(s3, s4, 3) //ok
    );

    //Player's cards cases
    //4 red, 3 green, 1 black, 2 locomotive
    SortedBag<Card> playerCardsA = new SortedBag.Builder<Card>()
            .add(3, Card.GREEN)
            .add(2, Card.LOCOMOTIVE)
            .add(4, Card.RED)
            .add(1, Card.BLACK)
            .build();

    @Test
    void initialFailsOnWrongInitialCardsNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState.initial(SortedBag.of());
        });
    }

    @Test
    void playerStateIsImmutable() {
        List<Route> routes = new ArrayList<>(givenCase);
        SortedBag<Ticket> tickets = SortedBag.of(ticketsA);
        SortedBag<Card> cards = playerCardsA;
        PlayerState a = new PlayerState(tickets, cards, routes);
        routes.clear();

        assertEquals(givenCase, a.routes());
        assertEquals(tickets, a.tickets());
        assertEquals(playerCardsA, a.cards());

        try {
            a.routes().clear();
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        assertEquals(givenCase, a.routes());

    }

    @Test
    void ticketsReturnsExpectedTickets() {
        PlayerState a = new PlayerState(SortedBag.of(ticketsA), playerCardsA, givenCase);
        assertEquals(SortedBag.of(ticketsA), a.tickets());
    }
}
