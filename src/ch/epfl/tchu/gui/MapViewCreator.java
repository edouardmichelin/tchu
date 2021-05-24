package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

import ch.epfl.tchu.gui.ActionHandlers.*;

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
        // todo
        return null;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}
