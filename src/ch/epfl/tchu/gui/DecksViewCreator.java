package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;


/**
 * Vue des decks, cartes et tickets
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
class DecksViewCreator {
    public static Node createHandView(ObservableGameState gameState) {
        ObservableList<Ticket> playerTickets = FXCollections.observableArrayList(gameState.playerTickets());
        ListView<Ticket> ticketsView = new ListView<Ticket>(playerTickets);
        ticketsView.setId("tickets");

        HBox handCardsView = new HBox(createCardView(Card.BLACK));
        handCardsView.setId("hand-pane");

        HBox playerHandPane = new HBox(handCardsView, ticketsView);
        playerHandPane.getStylesheets().addAll(List.of("decks.css", "colors.css"));

        return playerHandPane;
    }

    public static Node createCardsView(
            ObservableGameState gameState,
            ObjectProperty<DrawTicketsHandler> drawTickets,
            ObjectProperty<DrawCardHandler> drawCard
    ) {
        VBox faceUpCardsView = new VBox();
        for(Card card : gameState.faceUpCards()) {
            faceUpCardsView.getChildren().add(createCardView(card));
        }
        return null;
    }

    private static Node createCardView(Card card, int quantity) {
        Preconditions.checkArgument(quantity > 0);

        Rectangle cardBorder = new Rectangle();
        cardBorder.getStyleClass().add("outside");

        Rectangle cardColor = new Rectangle();
        cardColor.getStyleClass().addAll(List.of("filled", "inside"));

        Rectangle trainImage = new Rectangle();
        trainImage.getStyleClass().add("train-image");

        Text count = new Text();
        count.getStyleClass().add("count");
        if(quantity != 1) {
            count.setText(""+quantity);
        }

        StackPane mainCardPane = new StackPane(cardBorder, cardColor, trainImage, count);
        mainCardPane.getStyleClass().addAll(List.of(card.toString(), "card"));

        return mainCardPane;
    }

    private static Node createCardView(Card card) {
        return createCardView(card, 1);
    }

    private static HBox createHorizontalCardsView(SortedBag<Card> cards) {
        return null;
    }

    private static VBox createVerticalCardsView(SortedBag<Card> cards) {
        return null;
    }
}
