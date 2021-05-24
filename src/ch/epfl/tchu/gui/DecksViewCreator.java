package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
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
            StackPane cardView = createCardView(card, gameState.numberOfCard(card), false);
            cardView.visibleProperty().bind(Bindings.greaterThan(gameState.numberOfCard(card), 0));
            handCardsView.getChildren().add(cardView);
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
        Button drawTicketsButton = createDrawButton("Billets", gameState.ticketsPercentage());
        drawTicketsButton.setOnMouseClicked(event -> drawTickets.get().onDrawTickets());
        drawTicketsButton.disableProperty().bind(drawTickets.isNull());

        Button drawCardsButton = createDrawButton("Cartes", gameState.cardsPercentage());
        drawCardsButton.setOnMouseClicked(event -> drawCard.get().onDrawCard(-1));
        drawCardsButton.disableProperty().bind(drawCard.isNull());

        VBox decksPane = new VBox();
        decksPane.setId("card-pane");
        decksPane.getStylesheets().addAll("decks.css", "colors.css");

        decksPane.getChildren().add(drawTicketsButton);


        for (int index : Constants.FACE_UP_CARD_SLOTS) {

            StackPane cardView = createCardView(gameState.faceUpCards().get(index));
            cardView.setOnMouseClicked(event -> drawCard.get().onDrawCard(index));
            cardView.disableProperty().bind(drawCard.isNull());

            decksPane.getChildren().add(cardView);
        }

        decksPane.getChildren().add(drawCardsButton);

        return decksPane;
    }

    private static Button createDrawButton(String label, ReadOnlyIntegerProperty percentage) {
        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");

        Rectangle foreground = new Rectangle(50, 5);
        foreground.widthProperty().bind(percentage.multiply(50).divide(100));
        foreground.getStyleClass().add("foreground");

        Button button = new Button(label);
        button.getStyleClass().add("gauged");
        button.setGraphic(new Group(background, foreground));

        return button;
    }

    private static StackPane createCardView(Card card, ReadOnlyIntegerProperty count, boolean isFaceUpCard) {
        String color = card.color() == null ? "NEUTRAL" : card.color().toString();


        Rectangle cardBorder = new Rectangle(60, 90);
        cardBorder.getStyleClass().add("outside");

        Rectangle cardColor = new Rectangle(40, 70);
        cardColor.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");

        Text countText = new Text();
        countText.getStyleClass().add("count");
        countText.textProperty().bind(Bindings.convert(count));


        StackPane mainCardPane = isFaceUpCard || count.get() == 1 ? new StackPane(cardBorder, cardColor, trainImage) :
                new StackPane(cardBorder, cardColor, trainImage, countText);
        mainCardPane.getStyleClass().addAll(color, "card");

        return mainCardPane;
    }

    private static StackPane createCardView(Card card) {
        return createCardView(card, new SimpleIntegerProperty(), true);
    }
}