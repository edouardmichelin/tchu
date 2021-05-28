package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
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


/**
 * Vue des decks, cartes et tickets
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class DecksViewCreator {
    private DecksViewCreator() {
    }

    /**
     * Permet de créer la vue de la main du joueur en fonction de l'état de jeu observable passé en argument. C'est à
     * dire la vue du panel du bas de la fenêtre du jeu. Elle content les cartes en main du joueur ainsi que la liste
     * de ses tickets.
     *
     * @param gameState l'état observable du jeu.
     * @return le node contenant la vue de la main de joueur, c'est à dire le panel du bas de la fenêtre du jeu.
     */
    public static Node createHandView(ObservableGameState gameState) {

        SortedBag<Card> hand = gameState.cards();

        ObservableList<Ticket> playerTickets = gameState.playerTickets();
        ListView<Ticket> ticketsView = new ListView<>(playerTickets);
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

    /**
     * Permet de créer la vue des piles de cartes et tickets ainsi que des 5 cartes retournées qui composent donc le
     * panel à droite de la fenêtre du jeu. Elle est composé des 5 cartes face retournées ainsi que des boutons pour
     * priocher des tickets ou des cartes.
     *
     * @param gameState   l'état observable du jeu.
     * @param drawTickets le gestionnaire d'action du tirage des tickets.
     * @param drawCard    le gestionnaire d'action du tirage des cartes.
     * @return le node contenant la vue des pioches de tickets et cartes ainsi que des 5 cartes de faces retournées.
     */
    public static Node createCardsView(
            ObservableGameState gameState,
            ObjectProperty<DrawTicketsHandler> drawTickets,
            ObjectProperty<DrawCardHandler> drawCard
    ) {
        Button drawTicketsButton = createDrawButton(StringsFr.TICKETS, gameState.ticketsPercentage());
        drawTicketsButton.setOnMouseClicked(event -> drawTickets.get().onDrawTickets());
        drawTicketsButton.disableProperty().bind(drawTickets.isNull());

        Button drawCardsButton = createDrawButton(StringsFr.CARDS, gameState.cardsPercentage());
        drawCardsButton.setOnMouseClicked(event -> drawCard.get().onDrawCard(-1));
        drawCardsButton.disableProperty().bind(drawCard.isNull());

        VBox decksPane = new VBox();
        decksPane.setId("card-pane");
        decksPane.getStylesheets().addAll("decks.css", "colors.css");

        decksPane.getChildren().add(drawTicketsButton);


        for (int slot : Constants.FACE_UP_CARD_SLOTS) {

            StackPane cardView = createCardView(gameState.faceUpCard(slot));
            cardView.setOnMouseClicked(event -> drawCard.get().onDrawCard(slot));
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

    private static StackPane createCardView(ReadOnlyObjectProperty<Card> card) {
        StackPane mainCardPane = createCardView(card.isNull().get() ? Card.LOCOMOTIVE : card.get(),
                new SimpleIntegerProperty(), true);
        card.addListener((p, o, n) -> {
            mainCardPane.getStyleClass().setAll(n.color() == null ? "NEUTRAL" : n.toString(), "card");
        });

        return mainCardPane;
    }
}