package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Vue de la carte du jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class MapViewCreator {
    public static Node createMapView(
            ObservableGameState gameState,
            ObjectProperty<ClaimRouteHandler> claimRouteHandler,
            CardChooser cardChooser
    ) {
        Pane node = new Pane(new ImageView());

        node.getStylesheets().addAll("map.css", "colors.css");

        createRoutesView(node, gameState, claimRouteHandler, cardChooser);

        return node;
    }

    private static void createRoutesView(
            Pane node,
            ObservableGameState gameState,
            ObjectProperty<ClaimRouteHandler> claimRouteHandler,
            CardChooser cardChooser
    ) {
        ObservableList<Node> children = node.getChildren();

        for (Route route : ChMap.routes()) {
            PlayerId owner;
            Group group = new Group();
            ObservableList<Node> groupChildren = group.getChildren();
            ObservableList<String> classes = group.getStyleClass();
            String id = route.id();

            group.setId(id);
            classes.add("route");
            if (route.color() == null)
                classes.add("NEUTRAL");
            else
                classes.add(route.color().name());
            if (route.level().equals(Route.Level.UNDERGROUND)) classes.add("UNDERGROUND");
            if ((owner = gameState.routes(id).get()) != null) classes.add(owner.name());

            group.disableProperty().bind(
                    claimRouteHandler
                    .isNull()
                            .or(gameState.canClaimRoute(id).not())
                            .or(gameState.routes(id).isNull().not())
            );

            for (int caseIndex = 1; caseIndex <= route.length(); caseIndex++) {
                Group routeCase = new Group();
                ObservableList<Node> routeCaseChildren = routeCase.getChildren();
                routeCase.setId(String.format("%s_%d", id, caseIndex));

                /*
                 * TRACK
                 */
                Rectangle track = new Rectangle(36, 12);
                track.getStyleClass().addAll("track", "filled");

                routeCaseChildren.add(track);

                /*
                 * WAGON
                 */
                Group wagon = new Group();
                wagon.getStyleClass().add("car");

                Rectangle rect = new Rectangle(36, 12);
                Circle circle1 = new Circle(3);
                Circle circle2 = new Circle(3);

                rect.getStyleClass().add("filled");
                circle1.setCenterX(12);
                circle1.setCenterY(6);
                circle2.setCenterX(24);
                circle2.setCenterY(6);

                wagon.getChildren().addAll(rect, circle1, circle2);

                routeCaseChildren.add(wagon);
                groupChildren.add(routeCase);
            }

            group.setOnMouseClicked(event ->
                    handleRouteClick(event, route, gameState, claimRouteHandler.get(), cardChooser));

            children.add(group);
        }
    }

    private static void handleRouteClick(
            MouseEvent event,
            Route route,
            ObservableGameState gameState,
            ClaimRouteHandler claimRouteHandler,
            CardChooser cardChooser
    ) {
        List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);
        int size = possibleClaimCards.size();

        if (size == 0) return;
        if (size == 1) {
            claimRouteHandler.onClaimRoute(route, possibleClaimCards.get(0));
            return;
        }

        ChooseCardsHandler chooseCardsHandler = chosenCards -> claimRouteHandler.onClaimRoute(route, chosenCards);
        cardChooser.chooseCards(possibleClaimCards, chooseCardsHandler);
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}
