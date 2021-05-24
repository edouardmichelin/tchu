package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

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
            ObjectProperty<ClaimRouteHandler> claimRoute,
            CardChooser chooseCards
    ) {
        Pane node = new Pane(new ImageView());
        
        node.getStylesheets().addAll("map.css", "colors.css");


        for (Route route : ChMap.routes()) {

        }

        return node;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}
