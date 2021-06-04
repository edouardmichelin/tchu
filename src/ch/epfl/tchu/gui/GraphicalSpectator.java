package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Map;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * Représente l'interface graphique d'un spectateur
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class GraphicalSpectator {
    private final int MAX_DISPLAYED_INFORMATIONS = 10;

    private final ObservableGameState gameState;

    private final ObservableList<Text> infos = FXCollections.observableArrayList();


    /**
     * Créé l'interface graphique d'un joueur de tCHu
     *
     * @param playerNames Le nom des joueurs de la partie
     */
    public GraphicalSpectator(PlayerId playerId, Map<PlayerId, String> playerNames) {
        ObjectProperty<ClaimRouteHandler> claimRouteHandler = new SimpleObjectProperty<>();
        ObjectProperty<DrawTicketsHandler> drawTicketsHandler = new SimpleObjectProperty<>();
        ObjectProperty<DrawCardHandler> drawCardHandler = new SimpleObjectProperty<>();

        Stage primaryStage = new Stage();

        this.gameState = new ObservableGameState(playerId, playerNames.size());

        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, (ign, ored) -> {
        });
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHandler, drawCardHandler);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, this.infos);

        Menu menuView = new Menu("View");

        BorderPane root = new BorderPane(mapView, new MenuBar(menuView), cardsView, null, infoView);

        var rootScene = new Scene(root);

        menuView.getItems().add(MenuBarViewCreator.createDarkModeMenuItemView(rootScene));

        primaryStage.setScene(rootScene);
        primaryStage.setTitle("tCHu — mode spectateur");

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
}
