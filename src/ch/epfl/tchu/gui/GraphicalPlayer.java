package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * Représente l'interface graphique d'un joueur de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
class GraphicalPlayer {
    private final int MAX_DISPLAYED_INFORMATIONS = 5;

    private final ObservableGameState gameState;

    private final Stage modalStage;
    private final Scene initialTicketsChoiceModal;
    private final Scene ticketsChoiceModal;
    private final Scene initialCardsChoiceModal;
    private final Scene additionalCardsChoiceModal;

    private final ObjectProperty<DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawCardHandler> drawCardHandler = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>();
    private final ObjectProperty<ChooseTicketsHandler> chooseTicketsHandler = new SimpleObjectProperty<>();
    private final ObjectProperty<ChooseCardsHandler> chooseCardsHandler = new SimpleObjectProperty<>();

    private final ObservableList<Text> infos = FXCollections.observableArrayList();

    private final ObservableList<Ticket> initialTicketsChoice = FXCollections.observableArrayList();
    private final ObservableList<Ticket> ticketsChoice = FXCollections.observableArrayList();
    private final ObservableList<SortedBag<Card>> initialCardsChoice = FXCollections.observableArrayList();
    private final ObservableList<SortedBag<Card>> additionalCardsChoice = FXCollections.observableArrayList();


    /**
     * Créé l'interface graphique d'un joueur de tCHu
     *
     * @param playerId    Le joueur concerné par l'interface
     * @param playerNames Le nom des joueurs de la partie
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();

        Stage primaryStage = new Stage();
        this.modalStage = new Stage(StageStyle.UTILITY);

        this.gameState = new ObservableGameState(playerId);

        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHandler, drawCardHandler);
        Node handView = DecksViewCreator.createHandView(gameState);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, this.infos);

        this.initialTicketsChoiceModal = ModalsViewCreator
                .createTicketsChoiceView(this.initialTicketsChoice, this.chooseTicketsHandler, modalStage, true);
        this.ticketsChoiceModal = ModalsViewCreator
                .createTicketsChoiceView(this.ticketsChoice, this.chooseTicketsHandler, modalStage);
        this.initialCardsChoiceModal = ModalsViewCreator
                .createCardsChoiceView(this.initialCardsChoice, this.chooseCardsHandler, modalStage);
        this.additionalCardsChoiceModal = ModalsViewCreator
                .createCardsChoiceView(this.additionalCardsChoice, this.chooseCardsHandler, modalStage, true);

        Menu menuView = new Menu("View");

        BorderPane root = new BorderPane(mapView, new MenuBar(menuView), cardsView, handView, infoView);

        var rootScene = new Scene(root);

        menuView.getItems().add(MenuBarViewCreator.createDarkModeMenuItemView(
                rootScene,
                initialTicketsChoiceModal,
                ticketsChoiceModal,
                initialCardsChoiceModal,
                additionalCardsChoiceModal
        ));

        primaryStage.setScene(rootScene);
        primaryStage.setTitle(String.format("tCHu — %s", playerNames.get(playerId)));


        this.modalStage.initOwner(primaryStage);
        this.modalStage.initModality(Modality.WINDOW_MODAL);
        this.modalStage.setOnCloseRequest(Event::consume);

        primaryStage.show();
    }

    /**
     * Permet de rafraîchir l'état observable du joueur
     *
     * @param gameState   l'état observable de la partie
     * @param playerState l'état complet du joueur
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        this.gameState.setState(gameState, playerState);
    }

    /**
     * Prend un message — de type <i>String</i> — et l'ajoute au bas des informations sur le déroulement de la partie,
     * qui sont présentées dans la partie inférieure de la vue des informations.
     *
     * @param info le message à passer
     */
    public void receiveInfo(String info) {
        assert isFxApplicationThread();

        Text text = new Text(info);
        ObservableList<Text> newInfos = FXCollections.observableArrayList();

        int effectiveSize = Math.min(this.infos.size(), MAX_DISPLAYED_INFORMATIONS - 1);

        newInfos.add(text);

        for (int index = 0; index < effectiveSize; index++) {
            newInfos.add(this.infos.get(index));
        }

        this.infos.setAll(newInfos);
    }

    /**
     * Prend en arguments trois gestionnaires d'action, un par types d'actions que le joueur peut effectuer lors d'un
     * tour, et qui permet au joueur d'en effectuer une
     *
     * @param drawTicketsHandler le gestionnaire de tirage des billets
     * @param drawCardHandler    le gestionnaire de tirage des cartes
     * @param claimRouteHandler  le gestionnaire de prise de possession d'une route
     */
    public void startTurn(
            DrawTicketsHandler drawTicketsHandler,
            DrawCardHandler drawCardHandler,
            ClaimRouteHandler claimRouteHandler
    ) {
        assert isFxApplicationThread();

        this.drawTicketsHandler.set(gameState.canDrawTickets() ? () -> {
            drawTicketsHandler.onDrawTickets();
            resetHandlers();
        } : null);

        this.drawCardHandler.set(gameState.canDrawCards() ? slot -> {
            drawCardHandler.onDrawCard(slot);
            resetHandlers();
        } : null);

        this.claimRouteHandler.set(((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            resetHandlers();
        }));
    }

    /**
     * Prend en arguments un multiensemble contenant cinq ou trois billets que le joueur peut choisir et un
     * gestionnaire de choix de billets — de type <i>ChooseTicketsHandler</i> —, et qui ouvre une fenêtre permettant au
     * joueur de faire son choix ; une fois celui-ci confirmé, le gestionnaire de choix est appelé avec ce choix en
     * argument
     *
     * @throws IllegalArgumentException ssi la taille de <code>tickets</code> est différente de 3 ou 5
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();

        Preconditions.checkArgument(
                tickets.size() == Constants.IN_GAME_TICKETS_COUNT ||
                        tickets.size() == Constants.INITIAL_TICKETS_COUNT
        );

        if (tickets.size() == Constants.IN_GAME_TICKETS_COUNT) {
            this.ticketsChoice.setAll(tickets.toList());
            this.modalStage.setScene(this.ticketsChoiceModal);
        } else {
            this.initialTicketsChoice.setAll(tickets.toList());
            this.modalStage.setScene(this.initialTicketsChoiceModal);
        }

        this.modalStage.show();

        this.chooseTicketsHandler.set(tks -> {
            this.initialTicketsChoice.clear();
            this.ticketsChoice.clear();
            this.chooseTicketsHandler.set(null);
            chooseTicketsHandler.onChooseTickets(tks);
        });
    }

    /**
     * Prend en argument un gestionnaire de tirage de carte — de type <i>DrawCardHandler</i> — et qui autorise le
     * joueur à choisir une carte wagon/locomotive, soit l'une des cinq dont la face est visible, soit celle du
     * sommet de la pioche ; une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire est appelé avec
     * le choix du joueur
     *
     * @param drawCardHandler le gestionnaire de tirage de carte
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        this.drawCardHandler.set(slot -> {
            drawCardHandler.onDrawCard(slot);
            resetHandlers();
        });
    }

    /**
     * Prend en arguments une liste de multiensembles de cartes, qui sont les cartes initiales qu'il peut utiliser
     * pour s'emparer d'une route, et un gestionnaire de choix de cartes — de type <i>ChooseCardsHandler</i> —, et
     * qui ouvre une fenêtre similaire à celle de la figure 5 permettant au joueur de faire son choix ; une fois que
     * celui-ci a été fait et confirmé, le gestionnaire de choix est appelé avec le choix du joueur en argument
     *
     * @param setsOfCards        la liste de multiensembles de cartes
     * @param chooseCardsHandler le gestionnaire de choix de cartes
     */
    private void chooseClaimCards(
            List<SortedBag<Card>> setsOfCards,
            ChooseCardsHandler chooseCardsHandler
    ) {
        assert isFxApplicationThread();

        this.initialCardsChoice.setAll(setsOfCards);
        this.modalStage.setScene(this.initialCardsChoiceModal);
        this.modalStage.show();

        this.chooseCardsHandler.set(cards -> {
            this.chooseCardsHandler.set(null);
            this.initialCardsChoice.clear();
            chooseCardsHandler.onChooseCards(cards);
        });
    }

    /**
     * Prend en arguments une liste de multiensembles de cartes, qui sont les cartes additionnelles qu'il peut
     * utiliser pour s'emparer d'un tunnel et un gestionnaire de choix de cartes — de type
     * <i>ChooseCardsHandler</i> —, et qui ouvre une fenêtre permettant au joueur de faire son choix ; une fois que
     * celui-ci a été fait et confirmé
     *
     * @param setsOfCards        la liste de multiensembles de cartes
     * @param chooseCardsHandler le gestionnaire de choix de cartes
     */
    public void chooseAdditionalCards(
            List<SortedBag<Card>> setsOfCards,
            ChooseCardsHandler chooseCardsHandler
    ) {
        assert isFxApplicationThread();

        this.additionalCardsChoice.setAll(setsOfCards);
        this.modalStage.setScene(this.additionalCardsChoiceModal);
        this.modalStage.show();

        this.chooseCardsHandler.set(cards -> {
            this.chooseCardsHandler.set(null);
            this.additionalCardsChoice.clear();
            chooseCardsHandler.onChooseCards(cards);
        });
    }

    private void resetHandlers() {
        this.drawCardHandler.set(null);
        this.drawTicketsHandler.set(null);
        this.claimRouteHandler.set(null);
    }
}
