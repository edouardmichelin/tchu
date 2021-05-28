package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.gui.ActionHandlers.*;

import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import static javafx.application.Platform.isFxApplicationThread;


/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class ModalsViewCreator {
    private ModalsViewCreator() {
    }

    public static Scene createTicketsChoiceView(
            ObservableList<Ticket> ticketsChoice,
            ObjectProperty<ChooseTicketsHandler> chooseTicketsHandler,
            Stage owner,
            boolean isInitial
    ) {
        assert isFxApplicationThread();

        int choiceCount =
                (isInitial ? Constants.INITIAL_TICKETS_COUNT : Constants.IN_GAME_TICKETS_COUNT) - Constants.DISCARDABLE_TICKETS_COUNT;

        Text intro = new Text(String.format(StringsFr.CHOOSE_TICKETS,
                choiceCount, StringsFr.plural(choiceCount)));

        TextFlow introBox = new TextFlow(intro);

        ListView<Ticket> choiceList = createListView(ticketsChoice);

        Button confirmButton = createConfirmButton(choiceList, choiceCount);
        confirmButton.setOnAction(event -> {
            owner.hide();
            chooseTicketsHandler.get().onChooseTickets(SortedBag.of(choiceList.getSelectionModel().getSelectedItems()));
        });

        Scene scene = new Scene(new VBox(introBox, choiceList, confirmButton));

        scene.getStylesheets().add("chooser.css");

        return scene;
    }

    public static Scene createTicketsChoiceView(
            ObservableList<Ticket> ticketsChoice,
            ObjectProperty<ChooseTicketsHandler> chooseTicketsHandler,
            Stage owner
    ) {
        return createTicketsChoiceView(ticketsChoice, chooseTicketsHandler, owner, false);
    }

    public static Scene createCardsChoiceView(
            ObservableList<SortedBag<Card>> cardsChoice,
            ObjectProperty<ChooseCardsHandler> chooseCardsHandler,
            Stage owner,
            boolean isAdditional
    ) {
        assert isFxApplicationThread();

        Text intro = new Text(isAdditional ? StringsFr.CHOOSE_ADDITIONAL_CARDS : StringsFr.CHOOSE_CARDS);

        TextFlow introBox = new TextFlow(intro);

        ListView<SortedBag<Card>> choiceList = new ListView<>(cardsChoice);
        choiceList.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmButton = createConfirmButton(choiceList, isAdditional ? 0 : 1);

        confirmButton.setOnAction(event -> {
            owner.hide();
            chooseCardsHandler.get().onChooseCards(choiceList.getSelectionModel().getSelectedItem());
        });

        Scene scene = new Scene(new VBox(introBox, choiceList, confirmButton));

        scene.getStylesheets().add("chooser.css");

        return scene;
    }

    public static Scene createCardsChoiceView(
            ObservableList<SortedBag<Card>> cardsChoice,
            ObjectProperty<ChooseCardsHandler> chooseCardsHandler,
            Stage owner
    ) {
        return createCardsChoiceView(cardsChoice, chooseCardsHandler, owner, false);
    }

    private static <T> ListView<T> createListView(ObservableList<T> list) {
        ListView<T> view = new ListView<>(list);
        view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return view;
    }

    private static <T> Button createConfirmButton(ListView<T> choiceList, int requiredChoice) {
        Button confirmButton = new Button(StringsFr.CHOOSE);
        confirmButton.disableProperty().bind(Bindings.size(choiceList
                .getSelectionModel()
                .getSelectedItems())
                .greaterThanOrEqualTo(requiredChoice)
                .not()
        );

        return confirmButton;
    }

    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> object) {
            return Info.getCardsInfo(object);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
