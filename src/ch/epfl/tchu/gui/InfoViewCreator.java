package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * Classe de vue des informations du jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class InfoViewCreator {
    private InfoViewCreator() {}

    /**
     * Permet de créer la vue contenant les informations se trouvant à gauche de la fenêtre du jeu. Notamment les
     * statistiques observables des joueurs ainsi
     * qu'une liste des dernières actions qui se sont déroulée durant la partie.
     *
     * @param playerId    joueur concerné par la vue
     * @param playerNames le nom des joueurs
     * @param gameState   l'état observable du jeu
     * @param infos       liste des messages désignant les dernières actions jouées
     * @return la vue des informations des joueurs et des dernières actions jouées.
     */
    public static Node createInfoView(
            PlayerId playerId,
            Map<PlayerId, String> playerNames,
            ObservableGameState gameState,
            ObservableList<Text> infos
    ) {
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");

        PlayerId nextPlayer = playerId;
        for (int i = 0; i < PlayerId.COUNT; i++) {
            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");

            //%s :
            //– %s billets,
            //– %s cartes,
            //– %s wagons,
            //– %s points.
            var playerBelongings = gameState.playerBelongings(nextPlayer);
            Text playerInfos = new Text(StringsFr.PLAYER_STATS);
            playerInfos.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(nextPlayer),
                    playerBelongings.ownedTickets(), playerBelongings.ownedCards(), playerBelongings.ownedCars(),
                    playerBelongings.claimPoints()));

            TextFlow playerInfosBox = new TextFlow(circle, playerInfos);
            playerInfosBox.getStyleClass().add(nextPlayer.toString());

            playerStats.getChildren().add(playerInfosBox);
            nextPlayer = nextPlayer.next();
        }

        TextFlow actionsMessage = new TextFlow();
        actionsMessage.setId("game-info");
        Bindings.bindContent(actionsMessage.getChildren(), infos);

        VBox infoPanel = new VBox(playerStats, new Separator(), actionsMessage);
        infoPanel.getStylesheets().addAll("info.css", "colors.css");

        return infoPanel;
    }
}
