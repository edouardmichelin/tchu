package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Enregistrement des <i>Serde</i>s utiles au programme
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Serdes {

    private final static Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private final static Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    private final static List<PlayerId> ALL_PLAYERID = PlayerId.ALL;
    private final static List<Player.TurnKind> ALL_TURNKIND = Player.TurnKind.all();
    private final static List<Card> ALL_CARD = Card.ALL;
    private final static List<Route> ALL_ROUTE = ChMap.routes();
    private final static List<Ticket> ALL_TICKET = ChMap.tickets();

    public final static Serde<Integer> INT = Serde.of(Object::toString, Integer::parseInt);

    public final static Serde<String> STRING = Serde.of(
            str -> BASE64_ENCODER.encodeToString(str.getBytes(StandardCharsets.UTF_8)),
            b64 -> new String(BASE64_DECODER.decode(b64), StandardCharsets.UTF_8)
    );

    public final static Serde<PlayerId> PLAYERID = Serde.oneOf(ALL_PLAYERID);

    public final static Serde<Player.TurnKind> TURNKIND = Serde.oneOf(ALL_TURNKIND);

    public final static Serde<Card> CARD = Serde.oneOf(ALL_CARD);

    public final static Serde<Route> ROUTE = Serde.oneOf(ALL_ROUTE);

    public final static Serde<Ticket> TICKET = Serde.oneOf(ALL_TICKET);

    public final static Serde<List<String>> LIST_STRING = Serde.listOf(STRING, ",");
    public final static Serde<List<Card>> LIST_CARD = Serde.listOf(CARD, ",");
    public final static Serde<List<Route>> LIST_ROUTE = Serde.listOf(ROUTE, ",");

    public final static Serde<SortedBag<Card>> BAG_CARD = Serde.bagOf(CARD, ",");
    public final static Serde<SortedBag<Ticket>> BAG_TICKET = Serde.bagOf(TICKET, ",");

    public final static Serde<List<SortedBag<Card>>> LIST_BAG_CARD = Serde.listOf(BAG_CARD, ";");

    public final static Serde<PublicCardState> PUBLICCARDSTATE = Serde.of(
            publicCardState -> String.join(";", List.of(
                    LIST_CARD.serialize(publicCardState.faceUpCards()),
                    INT.serialize(publicCardState.deckSize()),
                    INT.serialize(publicCardState.discardsSize())
            )),
            str -> {
                /*
                 * 0: faceUpCards
                 * 1: deckSize
                 * 2: discardsSize
                 */
                String[] params = str.split(Pattern.quote(";"), -1);

                Preconditions.checkArgument(params.length == 3);

                return new PublicCardState(
                        LIST_CARD.deserialize(params[0]),
                        INT.deserialize(params[1]),
                        INT.deserialize(params[2])
                );
            }
    );

    public final static Serde<PublicPlayerState> PUBLICPLAYERSTATE = Serde.of(
            publicPlayerState -> String.join(";", List.of(
                    INT.serialize(publicPlayerState.ticketCount()),
                    INT.serialize(publicPlayerState.cardCount()),
                    LIST_ROUTE.serialize(publicPlayerState.routes())
            )),
            str -> {
                /*
                 * 0: ticketCount
                 * 1: carCount
                 * 2: routes
                 */
                String[] params = str.split(Pattern.quote(";"), -1);

                Preconditions.checkArgument(params.length == 3);

                return new PublicPlayerState(
                        INT.deserialize(params[0]),
                        INT.deserialize(params[1]),
                        LIST_ROUTE.deserialize(params[2])
                );
            }
    );

    public final static Serde<PlayerState> PLAYERSTATE = Serde.of(
            playerState -> String.join(";", List.of(
                    BAG_TICKET.serialize(playerState.tickets()),
                    BAG_CARD.serialize(playerState.cards()),
                    LIST_ROUTE.serialize(playerState.routes())
            )),
            str -> {
                /*
                 * 0: tickets
                 * 1: cards
                 * 2: routes
                 */
                String[] params = str.split(Pattern.quote(";"), -1);

                Preconditions.checkArgument(params.length == 3);

                return new PlayerState(
                        BAG_TICKET.deserialize(params[0]),
                        BAG_CARD.deserialize(params[1]),
                        LIST_ROUTE.deserialize(params[2])
                );
            }
    );

    private static final Serde<List<PublicPlayerState>> LIST_PUBLICPLAYERSTATE = Serde.listOf(PUBLICPLAYERSTATE, "!");

    public final static Serde<PublicGameState> PUBLICGAMESTATE = Serde.of(
            publicGameState -> String.join(":", List.of(
                    INT.serialize(publicGameState.ticketsCount()),
                    PUBLICCARDSTATE.serialize(publicGameState.cardState()),
                    PLAYERID.serialize(publicGameState.currentPlayerId()),
                    LIST_PUBLICPLAYERSTATE.serialize(ALL_PLAYERID.stream().map(publicGameState::playerState).collect(Collectors.toList())),
                    PLAYERID.serialize(publicGameState.lastPlayer())
            )),
            str -> {
                /*
                 * 0: ticketsCount
                 * 1: cardState
                 * 2: currentPlayerId
                 * 3: playerStates
                 * 4: lastPlayer
                 */
                String[] params = str.split(Pattern.quote(":"), -1);

                Preconditions.checkArgument(params.length == 5);

                List<PublicPlayerState> playerStates = LIST_PUBLICPLAYERSTATE.deserialize(params[3]);

                Preconditions.checkArgument(playerStates.size() == PlayerId.COUNT);

                return new PublicGameState(
                        INT.deserialize(params[0]),
                        PUBLICCARDSTATE.deserialize(params[1]),
                        PLAYERID.deserialize(params[2]),
                        ALL_PLAYERID
                                .stream()
                                .map(id -> new AbstractMap.SimpleEntry<>(id, playerStates.get(id.ordinal())))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                        PLAYERID.deserialize(params[4])
                );
            }
    );

    private Serdes() {
    }
}
