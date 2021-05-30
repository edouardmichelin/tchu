package ch.epfl.tchu.gui;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Représente la vue du launcher
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class LauncherViewCreator {
    private LauncherViewCreator(){}

    public Node createLauncherView() {
        StackPane playAsClientButton = createMenuButton(StringsFr.PLAY_AS_CLIENT, "client-button");
        StackPane hostServerButton = createMenuButton(StringsFr.HOST_GAME, "host-button");
        GridPane launcherBox = new GridPane();
        launcherBox.getStylesheets().add("launcher.css");
        launcherBox.addRow(0, playAsClientButton);
        launcherBox.addRow(1, hostServerButton);
        return null;
    }

    private StackPane createMenuButton(String buttonLabel, String styleId) {
        Text label = new Text(buttonLabel);
        label.getStyleClass().add("text");

        Rectangle border = new Rectangle(100, 100);
        border.getStyleClass().add("border");

        Rectangle background = new Rectangle(90, 90);
        background.getStyleClass().add("background");

        Rectangle icon = new Rectangle(90, 90);
        background.getStyleClass().add("image");
        background.setId(styleId);

        StackPane button = new StackPane();
        button.getStyleClass().add("menu-button");
        return button;
    }
}
