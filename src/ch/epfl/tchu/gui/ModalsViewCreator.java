package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.gui.ActionHandlers.*;

import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class ModalsViewCreator {
    private ModalsViewCreator() {}


    public static Node createInitialTicketsChoiceView(
            ObservableList<Ticket> initialTicketsChoice,
            ObjectProperty<ChooseTicketsHandler> chooseTicketsHandler
    ) {
        return new Pane();
    }

    public static Node createTicketsChoiceView(
            ObservableList<Ticket> ticketsChoice,
            ObjectProperty<ChooseTicketsHandler> chooseTicketsHandler
    ) {
        return new Pane();
    }

    public static Node createInitialCardsChoiceView(
            ObservableList<SortedBag<Card>> initialCardsChoice,
            ObjectProperty<ChooseCardsHandler> chooseCardsHandler
    ) {
        return new Pane();
    }

    public static Node createAdditionalCardsChoiceView(
            ObservableList<SortedBag<Card>> additionalCardsChoice,
            ObjectProperty<ChooseCardsHandler> chooseCardsHandler
    ) {
        return new Pane();
    }
}
