package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * Classe de vue des informations du jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
class InfoViewCreator {
    public static Node createInfoView(
            PlayerId playerId,
            Map<PlayerId, String> playerNames,
            ObservableGameState gameState,
            ObservableList<Text> infos
    )
    {
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");

        TextFlow actions = new TextFlow();
        actions.setId("game-info");

        VBox infoPanel = new VBox();
        infoPanel.getStylesheets().addAll("info.css", "colors.css");
        return infoPanel;
    }
}
