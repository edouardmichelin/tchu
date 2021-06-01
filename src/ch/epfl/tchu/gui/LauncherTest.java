package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Test pour le launcher
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class LauncherTest extends Application{
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        final Stage modalStage = new Stage(StageStyle.UTILITY);
        final Scene clientPrompScene = LauncherViewCreator.createPlayerPrompt();

        primaryStage.setScene(new Scene(LauncherViewCreator.createLauncherView()));
        primaryStage.setTitle("Launcher - tCHu");
        primaryStage.show();

        modalStage.setScene(clientPrompScene);
        modalStage.setTitle("Se connecter à un serveur");
        modalStage.show();
    }
}
