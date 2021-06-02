package ch.epfl.tchu.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Représente la vue du launcher
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class LauncherViewCreator {
    private LauncherViewCreator() {
    }

    /**
     * Permet de créer la vue du Launcher du jeu
     *
     * @return la vue du Launcher
     */
    public static GridPane createLauncherView(Stage owner) {
        StackPane playAsClientButton = createMenuButton(StringsFr.PLAY_AS_CLIENT, "client-button");
        playAsClientButton.setOnMouseClicked(event -> {
            createPlayerPrompt(owner);
        });

        StackPane hostServerButton = createMenuButton(StringsFr.HOST_GAME, "host-button");
        StackPane spectatorButton = createMenuButton(StringsFr.SPECTATE_GAME, "spec-button");


        StackPane exitButton = createMenuButton(StringsFr.EXIT_GAME, "exit-button");
        exitButton.setOnMouseClicked(event -> {
            Platform.exit();
        });

        GridPane launcherBox = new GridPane();
        launcherBox.getStylesheets().add("launcher.css");
        launcherBox.setId("launcher");
        launcherBox.add(playAsClientButton, 0, 0);
        launcherBox.add(hostServerButton, 1, 0);
        launcherBox.add(spectatorButton, 0, 1);
        launcherBox.add(exitButton, 1, 1);

        GridPane.setHalignment(playAsClientButton, HPos.CENTER);
        GridPane.setHalignment(hostServerButton, HPos.CENTER);

        activateMouseDrag(launcherBox, owner);

        return launcherBox;
    }

    /**
     * Permet de créer la vue de la fenêtre modale demandant les informations de connexion au joueur
     *
     * @return vue de la fenêtre modale quand l'on choisi de se connecter à un serveur
     */
    public static void createPlayerPrompt(Stage owner) {
        Text label = new Text(StringsFr.JOIN_PROMPT_LABEL);
        label.getStyleClass().addAll("label", "bigger");

        TextField addressField = new TextField();

        Button confirmButton = new Button(StringsFr.CONFIRM);
        Button backButton = new Button(StringsFr.BACK);

        GridPane promptBox = new GridPane();
        promptBox.getStyleClass().add("prompt");
        promptBox.add(label, 0, 0, 2, 1);
        promptBox.add(addressField, 0, 1, 2, 1);
        promptBox.add(confirmButton, 0, 2);
        promptBox.add(backButton, 1, 2);

        GridPane.setHalignment(confirmButton, HPos.LEFT);
        GridPane.setHalignment(backButton, HPos.RIGHT);

        Scene scene = new Scene(promptBox);
        scene.getStylesheets().add("launcher-prompt.css");

        activateMouseDrag(promptBox, owner);

        owner.setScene(scene);
    }

    public static Scene createHostPrompt() {
        Label playerLabel = new Label(StringsFr.CHOOSE_PLAYER_NUMBER);
        playerLabel.getStyleClass().add("label");

        Label spectatorLabel = new Label(StringsFr.CHOOSE_SPECTATOR_NUMBER);
        spectatorLabel.getStyleClass().add("label");

        ObservableList<String> playerOptions =
                FXCollections.observableArrayList("2", "3");

        ObservableList<String> spectatorOptions =
                FXCollections.observableArrayList("0", "1", "2");

        ComboBox<String> playerDropDown = new ComboBox<>(playerOptions);
        ComboBox<String> spectatorDropDown = new ComboBox<>(spectatorOptions);

        Button confirmButton = new Button(StringsFr.CONFIRM);

        GridPane promptBox = new GridPane();
        promptBox.getStyleClass().add("prompt");
        promptBox.addRow(0, playerLabel, playerDropDown);
        promptBox.addRow(1, spectatorLabel, spectatorDropDown);
        promptBox.add(confirmButton, 0, 2, 2, 1);

        GridPane.setHalignment(confirmButton, HPos.CENTER);

        Scene scene = new Scene(promptBox);
        scene.getStylesheets().add("launcher-prompt.css");

        return scene;
    }

    /**
     * Permet de créer les boutons principaux du Launcher
     *
     * @param buttonLabel Le texte du bouton
     * @param styleId     l'identifiant de style du bouton
     * @return Un bouton stylisé
     */
    private static StackPane createMenuButton(String buttonLabel, String styleId) {
        Text label = new Text(buttonLabel);
        label.getStyleClass().add("text");

        Rectangle border = new Rectangle(250, 250);
        border.getStyleClass().add("border");

        Rectangle background = new Rectangle(244, 244);
        background.getStyleClass().add("background");

        Rectangle icon = new Rectangle(180, 180);
        icon.setId(styleId);

        StackPane interior = new StackPane(icon, label);
        interior.getStyleClass().add("interior");

        StackPane button = new StackPane(border, background, interior);
        button.getStyleClass().add("menu-button");

        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(icon, Pos.TOP_CENTER);

        return button;
    }

    private static void activateMouseDrag(Node target, Stage owner) {
        target.setOnMousePressed(pressEvent -> {
            target.setOnMouseDragged(dragEvent -> {
                owner.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                owner.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }
}
