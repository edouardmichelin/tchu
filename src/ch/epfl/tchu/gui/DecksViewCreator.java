package ch.epfl.tchu.gui;

import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

/**
 * Vue des decks
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class DecksViewCreator {
    public static Node createHandView(ObservableGameState gameState) {
        // todo
        return null;
    }

    public static Node createCardsView(
            ObservableGameState gameState,
            ObjectProperty<DrawTicketsHandler> drawTickets,
            ObjectProperty<DrawCardHandler> drawCard
    ) {
        // todo
        return null;
    }
}