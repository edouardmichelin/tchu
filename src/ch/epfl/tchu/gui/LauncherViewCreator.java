package ch.epfl.tchu.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
        hostServerButton.setOnMouseClicked(event -> {
            createHostPrompt(owner);
        });

        StackPane spectatorButton = createMenuButton(StringsFr.SPECTATE_GAME, "spec-button");
        spectatorButton.setOnMouseClicked(event -> {
            createSpectatorPrompt(owner);
        });


        StackPane exitButton = createMenuButton(StringsFr.EXIT_GAME, "exit-button");
        exitButton.setOnMouseClicked(event -> {
            Platform.exit();
        });

        GridPane launcherBox = new GridPane();
        launcherBox.getStylesheets().add("launcher.css");
        launcherBox.setId("launcher");
        launcherBox.add(playAsClientButton, 0, 0);
        launcherBox.add(spectatorButton, 1, 0);
        launcherBox.add(hostServerButton, 0, 1);
        launcherBox.add(exitButton, 1, 1);

        GridPane.setHalignment(playAsClientButton, HPos.CENTER);
        GridPane.setHalignment(hostServerButton, HPos.CENTER);

        activateMouseDrag(launcherBox, owner);

        return launcherBox;
    }

    private static void createPlayerPrompt(Stage owner) {
        TextField addressField = new TextField();
        Button confirmButton = new Button(StringsFr.CONFIRM);
        confirmButton.disableProperty().bind(addressField.textProperty().isEmpty());

        createConnectPrompt(owner, confirmButton, addressField);
    }

    private static void createHostPrompt(Stage owner) {

        Text playerLabel = new Text(StringsFr.CHOOSE_PLAYER_NUMBER);
        playerLabel.getStyleClass().add("label");

        Text playerOne = new Text(StringsFr.PLAYER_ONE);
        playerOne.getStyleClass().add("label");

        TextField playerOneTextField = new TextField();

        Text playerTwo = new Text(StringsFr.PLAYER_TWO);
        playerTwo.getStyleClass().add("label");

        TextField playerTwoTextField = new TextField();

        Text playerThree = new Text(StringsFr.PLAYER_THREE);
        playerThree.getStyleClass().add("label");

        TextField playerThreeTextField = new TextField();

        Text spectatorLabel = new Text(StringsFr.CHOOSE_SPECTATOR_NUMBER);
        spectatorLabel.getStyleClass().add("label");

        ObservableList<String> playerOptions =
                FXCollections.observableArrayList("2", "3");

        ObservableList<String> spectatorOptions =
                FXCollections.observableArrayList("0", "1", "2");

        ComboBox<String> playerDropDown = new ComboBox<>(playerOptions);
        playerDropDown.getSelectionModel().select("2");

        ComboBox<String> spectatorDropDown = new ComboBox<>(spectatorOptions);
        spectatorDropDown.getSelectionModel().select("0");

        BooleanBinding threePlayerCondition = playerDropDown
                .getSelectionModel().selectedItemProperty().isEqualTo("3");

        BooleanBinding twoPlayerCondition = playerDropDown
                .getSelectionModel().selectedItemProperty().isEqualTo("2");

        playerThreeTextField.setVisible(false);
        playerThreeTextField.visibleProperty()
                .bind(threePlayerCondition);

        playerThree.visibleProperty().bind(threePlayerCondition);

        Button confirmButton = new Button(StringsFr.CONFIRM);
        confirmButton.disableProperty().bind(
                Bindings.not(threePlayerCondition
                        .and(
                                playerOneTextField.textProperty().isEmpty().not()
                                .and(playerTwoTextField.textProperty().isEmpty().not())
                                .and(playerThreeTextField.textProperty().isEmpty().not())
                        ).or(twoPlayerCondition
                                .and(playerOneTextField.textProperty().isEmpty().not()
                                            .and(playerTwoTextField.textProperty().isEmpty().not())))

                ));

        Button backButton = createBackButton(owner);


        GridPane promptBox = new GridPane();
        promptBox.getStyleClass().add("prompt");
        promptBox.addRow(0, playerLabel, playerDropDown);
        promptBox.addRow(1, spectatorLabel, spectatorDropDown);
        promptBox.addRow(2, playerOne, playerOneTextField);
        promptBox.addRow(3, playerTwo, playerTwoTextField);
        promptBox.addRow(4, playerThree, playerThreeTextField);

        promptBox.add(backButton, 0, 5);
        promptBox.add(confirmButton, 1, 5);

        GridPane.setHalignment(confirmButton, HPos.RIGHT);
        GridPane.setHalignment(backButton, HPos.LEFT);

        Scene scene = new Scene(promptBox);
        scene.getStylesheets().add("launcher-prompt.css");

        activateMouseDrag(promptBox, owner);

        owner.setScene(scene);
    }

    private static void createSpectatorPrompt(Stage owner) {
        TextField addressField = new TextField();
        Button confirmButton = new Button(StringsFr.CONFIRM);
        confirmButton.disableProperty().bind(addressField.textProperty().isEmpty());

        createConnectPrompt(owner, confirmButton, addressField);
    }

    private static void createConnectPrompt(Stage owner, Button confirm, TextField textField) {
        Text label = new Text(StringsFr.JOIN_PROMPT_LABEL);
        label.getStyleClass().addAll("label", "bigger");

        Button backButton = createBackButton(owner);

        GridPane promptBox = new GridPane();
        promptBox.getStyleClass().add("prompt");
        promptBox.add(label, 0, 0, 2, 1);
        promptBox.add(textField, 0, 1, 2, 1);
        promptBox.add(backButton, 0, 2);
        promptBox.add(confirm, 1, 2);

        GridPane.setHalignment(confirm, HPos.RIGHT);
        GridPane.setHalignment(backButton, HPos.LEFT);

        Scene scene = new Scene(promptBox);
        scene.getStylesheets().add("launcher-prompt.css");

        activateMouseDrag(promptBox, owner);

        owner.setScene(scene);
    }


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

    private static Button createBackButton(Stage owner) {
        Button backButton = new Button(StringsFr.BACK);
        backButton.setOnAction(event -> {
            owner.setScene(new Scene(createLauncherView(owner)));
        });
        return backButton;
    }
}
