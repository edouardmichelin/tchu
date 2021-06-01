package ch.epfl.tchu.gui;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    public static GridPane createPlayerPrompt() {
        Label label = new Label("Adresse du serveur :");
        TextField addressField = new TextField();

        GridPane promptBox = new GridPane();
        promptBox.addRow(0, label, addressField);

        return promptBox;
    }

    private static StackPane createMenuButton(String buttonLabel, String styleId) {
        Text label = new Text(buttonLabel);
        label.getStyleClass().add("text");

        Rectangle border = new Rectangle(400, 250);
        border.getStyleClass().add("border");

        Rectangle background = new Rectangle(394, 244);
        background.getStyleClass().add("background");

        Rectangle icon = new Rectangle(180, 180);
        icon.getStyleClass().add("image");
        icon.setId(styleId);

        StackPane interior = new StackPane(icon, label);
        interior.getStyleClass().add("interior");

        StackPane button = new StackPane(border, background, interior);
        button.getStyleClass().add("menu-button");

        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(icon, Pos.TOP_CENTER);

        return button;
    }
}
