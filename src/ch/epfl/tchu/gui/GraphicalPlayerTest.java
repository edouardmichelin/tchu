package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class GraphicalPlayerTest extends Application {
    public static void main(String[] args) { launch(args); }

    private void setState(GraphicalPlayer player) {
        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 25)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 5));

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PlayerId.PLAYER_1, p1State, PlayerId.PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PlayerId.PLAYER_1, pubPlayerStates, null);

        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles");
        GraphicalPlayer p = new GraphicalPlayer(PlayerId.PLAYER_1, playerNames);
        setState(p);

        ActionHandlers.DrawTicketsHandler drawTicketsH =
                () -> p.receiveInfo("Je tire des billets !");
        ActionHandlers.DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
        ActionHandlers.ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                };

        ActionHandlers.ChooseTicketsHandler chooseTickets = tickets -> {
            String names = tickets.stream().map(Ticket::toString).collect(Collectors.joining(" "));
            p.receiveInfo(String.format("J'ai tiré : %s\n", names));
            p.chooseTickets(SortedBag.of(ChMap.tickets().subList(5, 10)), tkts -> {
                String namesT = tkts.stream().map(Ticket::toString).collect(Collectors.joining(" "));
                p.receiveInfo(String.format("J'ai tiré : %s", namesT));
                p.startTurn(drawTicketsH, drawCardH, claimRouteH);
            });
        };

        // p.startTurn(drawTicketsH, drawCardH, claimRouteH);
        p.chooseTickets(SortedBag.of(ChMap.tickets().subList(0, 3)), chooseTickets);
    }
}
