package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerType;
import ch.epfl.tchu.net.MyIpAddress;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Représente la vue du launcher
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class LauncherViewCreator {
    private static Scene LAUNCHER_VIEW;

    private LauncherViewCreator() {
    }

    /**
     * Permet de créer la vue du Launcher du jeu
     *
     * @return la vue du Launcher
     */
    public static Scene createLauncherView(
            Stage owner,
            GameLauncher.CreateServerHandler onCreateServer,
            GameLauncher.JoinServerHandler onJoinServer
    ) {
        StackPane playAsClientButton = createMenuButton(StringsFr.PLAY_AS_CLIENT, "client-button");
        playAsClientButton.setOnMouseClicked(event -> {
            createPlayerPrompt(owner, onJoinServer);
        });

        StackPane spectatorButton = createMenuButton(StringsFr.SPECTATE_GAME, "spec-button");
        spectatorButton.setOnMouseClicked(event -> {
            createSpectatorPrompt(owner, onJoinServer);
        });

        StackPane hostServerButton = createMenuButton(StringsFr.HOST_GAME, "host-button");
        hostServerButton.setOnMouseClicked(event -> {
            createHostPrompt(owner, onCreateServer);
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

        LAUNCHER_VIEW = new Scene(launcherBox);

        return LAUNCHER_VIEW;
    }

    private static void createPlayerPrompt(Stage owner, GameLauncher.JoinServerHandler onJoinServer) {
        TextField addressField = new TextField();
        Button confirmButton = new Button(StringsFr.CONFIRM);
        StringProperty textProperty = addressField.textProperty();
        confirmButton.disableProperty().bind(textProperty.isEmpty());

        confirmButton.setOnAction(e -> onJoinServer.onJoinServer(textProperty.get(), PlayerType.PLAYER));

        createConnectPrompt(owner, confirmButton, addressField);
    }

    private static void createHostPrompt(Stage owner, GameLauncher.CreateServerHandler onCreateServer) {

        Text ipsLavel = new Text("Vous devriez être joignable à ces adresses");
        ipsLavel.getStyleClass().add("label");
        TextFlow ips = new TextFlow();
        ips.getChildren().setAll(MyIpAddress.show().stream().map(text -> {
            Text res = new Text(text);
            res.setFill(Color.WHITE);
            return res;
        }).collect(Collectors.toList()));
        ips.getStyleClass().add("ips");

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
        promptBox.add(ipsLavel, 0, 0, 2, 1);
        promptBox.add(ips, 0, 1, 2, 1);
        promptBox.add(new Separator(), 0, 2, 2, 1);
        promptBox.addRow(3, playerLabel, playerDropDown);
        promptBox.addRow(4, spectatorLabel, spectatorDropDown);
        promptBox.addRow(5, playerOne, playerOneTextField);
        promptBox.addRow(6, playerTwo, playerTwoTextField);
        promptBox.addRow(7, playerThree, playerThreeTextField);

        promptBox.add(backButton, 0, 8);
        promptBox.add(confirmButton, 1, 8);

        GridPane.setHalignment(confirmButton, HPos.RIGHT);
        GridPane.setHalignment(backButton, HPos.LEFT);

        Scene scene = new Scene(promptBox);
        scene.getStylesheets().add("launcher-prompt.css");

        confirmButton.setOnAction(e -> {
            List<String> names = List.of(
                    playerOneTextField.textProperty().get(),
                    playerTwoTextField.textProperty().get(),
                    playerThreeTextField.textProperty().get());

            int numberOfPlayers = Integer.parseInt(playerDropDown.getSelectionModel().getSelectedItem());
            int numberOfSpectators = Integer.parseInt(spectatorDropDown.getSelectionModel().getSelectedItem());

            try {
                onCreateServer.onCreateServer(names, numberOfPlayers, numberOfSpectators);
            } catch (IOException error) {
                throw new Error(error);
            }
        });

        activateMouseDrag(promptBox, owner);

        owner.setScene(scene);
    }

    private static void createSpectatorPrompt(Stage owner, GameLauncher.JoinServerHandler onJoinServer) {
        TextField addressField = new TextField();
        Button confirmButton = new Button(StringsFr.CONFIRM);
        StringProperty textProperty = addressField.textProperty();
        confirmButton.disableProperty().bind(textProperty.isEmpty());

        confirmButton.setOnAction(e -> onJoinServer.onJoinServer(textProperty.get(), PlayerType.SPECTATOR));

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
            // owner.setScene(new Scene(createLauncherView(owner, onCreateServer, onJoinServer)));
            owner.setScene(LAUNCHER_VIEW);
        });
        return backButton;
    }
}
