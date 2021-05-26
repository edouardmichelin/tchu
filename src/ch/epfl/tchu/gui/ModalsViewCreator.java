package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.gui.ActionHandlers.*;

import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class ModalsViewCreator {
    private ModalsViewCreator() {
    }


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

    private static VBox createTicketsChoice(
            ObservableList<Ticket> choices,
            ObjectProperty<ChooseTicketsHandler> handler
    ) {
        int choiceCount = choices.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        Text intro = new Text(String.format(StringsFr.CHOOSE_TICKETS,
                choiceCount, StringsFr.plural(choiceCount)));

        TextFlow introBox = new TextFlow(intro);

        Button confirmButton = new Button(StringsFr.CHOOSE);

        return new VBox(introBox, createListView(choices), confirmButton);
    }

    private static Stage createCardsChoice(
            ObservableList<SortedBag<Card>> choices,
            ObjectProperty<ChooseCardsHandler> handler,
            boolean isAdditional
    ) {
        Text intro = new Text(isAdditional ? StringsFr.CHOOSE_ADDITIONAL_CARDS : StringsFr.CHOOSE_CARDS);

        TextFlow introBox = new TextFlow(intro);

        ListView<SortedBag<Card>> choiceList = createListView(choices);
        choiceList.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        return null;
    }

    private static <T> ListView<T> createListView(ObservableList<T> list) {
        ListView<T> view = new ListView<T>(list);
        view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return view;
    }

    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> object) {
            return null;
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
