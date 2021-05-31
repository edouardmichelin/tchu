package ch.epfl.tchu.gui;

import javafx.geometry.HPos;
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

    public static GridPane createLauncherView() {
        StackPane playAsClientButton = createMenuButton(StringsFr.PLAY_AS_CLIENT, "client-button");
        StackPane hostServerButton = createMenuButton(StringsFr.HOST_GAME, "host-button");

        GridPane launcherBox = new GridPane();
        launcherBox.getStylesheets().add("launcher.css");
        launcherBox.setId("launcher");
        launcherBox.addRow(0, playAsClientButton);
        launcherBox.addRow(1, hostServerButton);

        GridPane.setHalignment(playAsClientButton, HPos.CENTER);
        GridPane.setHalignment(hostServerButton, HPos.CENTER);

        return launcherBox;
    }

    private static StackPane createMenuButton(String buttonLabel, String styleId) {
        Text label = new Text(buttonLabel);
        label.getStyleClass().add("text");

        Rectangle border = new Rectangle(500, 250);
        border.getStyleClass().add("border");

        Rectangle background = new Rectangle(494, 244);
        background.getStyleClass().add("background");

        Rectangle icon = new Rectangle(494, 244);
        icon.getStyleClass().add("image");
        icon.setId(styleId);

        StackPane button = new StackPane(border, icon, background, label);
        button.getStyleClass().add("menu-button");
        return button;
    }
}
