package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Tests
 *
 * @author Michel Schinz & co.
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Stage9Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        ObservableGameState gameState = new ObservableGameState(PlayerId.PLAYER_1);

        setState(gameState);

        ObjectProperty<ClaimRouteHandler> claimRoute =
                new SimpleObjectProperty<>(Stage9Test::claimRoute);
        ObjectProperty<DrawTicketsHandler> drawTickets =
                new SimpleObjectProperty<>(Stage9Test::drawTickets);
        ObjectProperty<DrawCardHandler> drawCard =
                new SimpleObjectProperty<>(Stage9Test::drawCard);

        Node mapView = MapViewCreator
                .createMapView(gameState, claimRoute, Stage9Test::chooseCards);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator
                .createHandView(gameState);

        Map<PlayerId, String> playerNames =
                Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles");
        ObservableList<Text> infos = FXCollections.observableArrayList(
                new Text("Première information.\n"),
                new Text("\nSeconde information.\n"));
        Node infoView = InfoViewCreator
                .createInfoView(PlayerId.PLAYER_1, playerNames, gameState, infos);

        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();

        dumpTree(mapView);
    }

    private void setState(ObservableGameState gameState) {
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
        gameState.setState(publicGameState, p1State);
    }

    private static void claimRoute(Route route, SortedBag<Card> cards) {
        System.out.printf("Prise de possession d'une route : %s - %s %s%n",
                route.station1(), route.station2(), cards);
    }

    private static void chooseCards(List<SortedBag<Card>> options,
                                    ChooseCardsHandler chooser) {
        chooser.onChooseCards(options.get(0));
    }

    private static void drawTickets() {
        System.out.println("Tirage de billets !");
    }

    private static void drawCard(int slot) {
        System.out.printf("Tirage de cartes (emplacement %s)!\n", slot);
    }

    private static void dumpTree(Node root) {
        dumpTree(0, root);
    }

    private static void dumpTree(int indent, Node root) {
        System.out.printf("%s%s (id: %s, classes: [%s])%n",
                " ".repeat(indent),
                root.getTypeSelector(),
                root.getId(),
                String.join(", ", root.getStyleClass()));
        if (root instanceof Parent) {
            Parent parent = ((Parent) root);
            for (Node child : parent.getChildrenUnmodifiable())
                dumpTree(indent + 2, child);
        }
    }
}
