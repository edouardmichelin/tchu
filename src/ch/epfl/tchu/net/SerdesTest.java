package ch.epfl.tchu.net;

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

        assertEquals(a, c);
    }

    @Test
    void serdeCardWorks() {
        var a = Card.RED;
        var b = Serdes.CARD.serialize(a);
        var c = Serdes.CARD.deserialize(b);

        assertEquals(a, c);
    }

    @Test
    void serdeCardListWorks() {
        var a = Card.ALL;
        var b = Serdes.LIST_CARD.serialize(a);
        var c = Serdes.LIST_CARD.deserialize(b);

        assertEquals(a, c);
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
