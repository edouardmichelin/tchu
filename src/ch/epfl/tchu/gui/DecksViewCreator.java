package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

        SortedBag<Card> hand = gameState.cards();

        ObservableList<Ticket> playerTickets = FXCollections.observableArrayList(gameState.playerTickets());
        ListView<Ticket> ticketsView = new ListView<Ticket>(playerTickets);
        ticketsView.setId("tickets");

        HBox handCardsView = new HBox();
        handCardsView.setId("hand-pane");
        for (Card card : Card.ALL) {
            if (hand.contains(card)) {
                handCardsView.getChildren().add(createCardView(card, hand.countOf(card)));
            }
        }

        HBox playerHandPane = new HBox(ticketsView, handCardsView);
        playerHandPane.getStylesheets().addAll("decks.css", "colors.css");

        return playerHandPane;
    }

    public static Node createCardsView(
            ObservableGameState gameState,
            ObjectProperty<DrawTicketsHandler> drawTickets,
            ObjectProperty<DrawCardHandler> drawCard
    ) {
        VBox faceUpCardsView = new VBox();
        for (Card card : gameState.faceUpCards()) {
            faceUpCardsView.getChildren().add(createCardView(card));
        }

        VBox decksPane = new VBox();
        decksPane.setId("card-pane");
        decksPane.getStylesheets().addAll("decks.css", "colors.css");
        return null;
    }

    private static Node createDrawButton(String label, IntegerProperty percentage) {


        Button button = new Button();
        button.getStyleClass().add("gauged");
        return null;
    }

    private static Node createCardView(Card card, int quantity) {
        Preconditions.checkArgument(quantity > 0);
        String color = card.color() == null ? "NEUTRAL" : card.color().toString();


        Rectangle cardBorder = new Rectangle(60, 90);
        cardBorder.getStyleClass().add("outside");

        Rectangle cardColor = new Rectangle(40, 70);
        cardColor.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");

        Text count = new Text();
        count.getStyleClass().add("count");
        if (quantity != 1) {
            count.setText("" + quantity);
        }

        StackPane mainCardPane = new StackPane(cardBorder, cardColor, trainImage, count);
        mainCardPane.getStyleClass().addAll(color, "card");

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
