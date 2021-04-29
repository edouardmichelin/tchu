package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class SerdesTest {
    @Test
    void serdePlayerIdWorks() {
        var a = PlayerId.PLAYER_1;
        var b = Serdes.PLAYERID.serialize(a);
        var c = Serdes.PLAYERID.deserialize(b);

        assertEquals("0", b);
        assertEquals(a, c);
    }

    @Test
    void serdeTurnKindWorks() {
        var a = Player.TurnKind.DRAW_CARDS;
        var b = Serdes.TURNKIND.serialize(a);
        var c = Serdes.TURNKIND.deserialize(b);

        assertEquals("1", b);
        assertEquals(a, c);
    }

    @Test
    void serdeCardWorks() {
        var a = Card.RED;
        var b = Serdes.CARD.serialize(a);
        var c = Serdes.CARD.deserialize(b);

        assertEquals("6", b);
        assertEquals(a, c);
    }

    @Test
    void serdeRouteWorks() {
        var a = ChMap.routes().get(18);
        var b = Serdes.ROUTE.serialize(a);
        var c = Serdes.ROUTE.deserialize(b);

        assertEquals("18", b);
        assertEquals(a, c);
    }

    @Test
    void serdeTicketWorks() {
        var a = ChMap.tickets().get(5);
        var b = Serdes.TICKET.serialize(a);
        var c = Serdes.TICKET.deserialize(b);

        assertEquals("5", b);
        assertEquals(a, c);
    }

    @Test
    void oneOfWorks() {
        var serdeColor = Serde.oneOf(Color.ALL);
        var serdeCard = Serde.oneOf(Card.ALL);
        var a = Color.YELLOW;
        var b = Card.BLUE;
        var encA = serdeColor.serialize(a);
        var encB = serdeCard.serialize(b);
        var decA = serdeColor.deserialize(encA);
        var decB = serdeCard.deserialize(encB);

        assertEquals("4", encA);
        assertEquals("2", encB);
        assertEquals(a, decA);
        assertEquals(b, decB);
    }

    @Test
    void serdeStringListWorks() {
        var a = List.of(
                "one",
                "two",
                "three"
        );
        var b = Serdes.LIST_STRING.serialize(a);
        var c = Serdes.LIST_STRING.deserialize(b);

        assertEquals(a, c);
    }


    @Test
    void serdeListCardWorks() {
        var a = List.of(
                Card.BLACK,
                Card.RED,
                Card.WHITE,
                Card.BLACK
        );
        var b = Serdes.LIST_CARD.serialize(a);
        var c = Serdes.LIST_CARD.deserialize(b);

        assertEquals("0,6,7,0", b);
        assertEquals(a, c);
    }

    @Test
    void serdeListRouteWorks() {
        var a = ChMap.routes();
        var b = Serdes.LIST_ROUTE.serialize(a);
        var c = Serdes.LIST_ROUTE.deserialize(b);

        assertEquals(a, c);
    }

    @Test
    void serdeBagCardWorks() {
        var a = SortedBag.of(Card.ALL);
        var b = Serdes.BAG_CARD.serialize(a);
        var c = Serdes.BAG_CARD.deserialize(b);

        assertEquals(a, c);
    }

    @Test
    void serdeBagTicketWorks() {
        var a = SortedBag.of(ChMap.tickets());
        var b = Serdes.BAG_TICKET.serialize(a);
        var c = Serdes.BAG_TICKET.deserialize(b);

        assertEquals(a, c);
    }

    @Test
    void serdeListBagCardWorks() {
        var a = List.of(
                SortedBag.of(2, Card.BLUE),
                SortedBag.of(4, Card.GREEN),
                SortedBag.of(1, Card.RED),
                SortedBag.of(Card.ALL)
        );
        var b = Serdes.LIST_BAG_CARD.serialize(a);
        var c = Serdes.LIST_BAG_CARD.deserialize(b);

        assertEquals(a, c);
    }

    @Test
    void serdePublicCardStateWorks() {
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        var a = new PublicCardState(fu, 30, 31);
        var b = Serdes.PUBLICCARDSTATE.serialize(a);
        var c = Serdes.PUBLICCARDSTATE.deserialize(b);

        assertEquals(a.deckSize(), c.deckSize());
        assertEquals(a.discardsSize(), c.discardsSize());
        assertEquals(a.faceUpCard(3), c.faceUpCard(3));
        assertEquals(a.totalSize(), c.totalSize());
        assertEquals(a.faceUpCards(), c.faceUpCards());
    }

    @Test
    void serdePublicPlayerStateWorks() {
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        var a = new PublicPlayerState(10, 11, rs1);
        var b = Serdes.PUBLICPLAYERSTATE.serialize(a);
        var c = Serdes.PUBLICPLAYERSTATE.deserialize(b);

        assertEquals(a.cardCount(), c.cardCount());
        assertEquals(a.carCount(), c.carCount());
        assertEquals(a.routes(), c.routes());
        assertEquals(a.ticketCount(), c.ticketCount());
        assertEquals(a.claimPoints(), c.claimPoints());
    }

    @Test
    void serdePlayerStateWorks() {
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        var a = PlayerState.initial(SortedBag.of(
                1, Card.RED,
                3, Card.BLUE
        ))
                .withAddedTickets(SortedBag.of(ChMap.tickets()));
        var b = Serdes.PLAYERSTATE.serialize(a);
        var c = Serdes.PLAYERSTATE.deserialize(b);

        assertEquals(a.cardCount(), c.cardCount());
        assertEquals(a.carCount(), c.carCount());
        assertEquals(a.routes(), c.routes());
        assertEquals(a.ticketCount(), c.ticketCount());
        assertEquals(a.claimPoints(), c.claimPoints());

        assertEquals(a.cards(), c.cards());
        assertEquals(a.tickets(), c.tickets());
    }

    @Test
    void serdePublicGameStateWorks() {
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs = new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);
        var b = Serdes.PUBLICGAMESTATE.serialize(gs);
        var c = Serdes.PUBLICGAMESTATE.deserialize(b);

        assertEquals(gs.cardState().deckSize(), c.cardState().deckSize());
        assertEquals(gs.cardState().discardsSize(), c.cardState().discardsSize());
        assertEquals(gs.ticketsCount(), c.ticketsCount());
        assertEquals(gs.currentPlayerId(), c.currentPlayerId());
        assertEquals(gs.lastPlayer(), c.lastPlayer());

        for (PlayerId id : PlayerId.ALL) {
            var gsPlayer = gs.playerState(id);
            var cPlayer = c.playerState(id);
            var gsPlayerRoutes = new StringBuilder();
            var cPlayerRoutes = new StringBuilder();
            assertEquals(gsPlayer.cardCount(), cPlayer.cardCount());
            assertEquals(gsPlayer.ticketCount(), cPlayer.ticketCount());

            gsPlayer.routes().forEach(gsPlayerRoutes::append);
            cPlayer.routes().forEach(cPlayerRoutes::append);
            assertEquals(gsPlayerRoutes.toString(), cPlayerRoutes.toString());
        }
    }
}
