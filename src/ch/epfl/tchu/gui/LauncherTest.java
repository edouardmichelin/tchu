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
        final Stage modalClient = new Stage(StageStyle.UTILITY);
        final Stage modalHost = new Stage(StageStyle.UTILITY);

        final Scene clientPrompScene = LauncherViewCreator.createPlayerPrompt();
        final Scene hostPromptScene = LauncherViewCreator.createHostPrompt();

        primaryStage.setScene(new Scene(LauncherViewCreator.createLauncherView()));
        primaryStage.setTitle("Launcher - tCHu");
        primaryStage.show();

        modalClient.setScene(clientPrompScene);
        modalClient.setTitle("Se connecter à un serveur");
        modalClient.show();

        modalHost.setScene(hostPromptScene);
        modalHost.setTitle("Héberger une partie");
        modalHost.show();
    }
}
