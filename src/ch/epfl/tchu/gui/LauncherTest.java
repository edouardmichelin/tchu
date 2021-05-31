package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        primaryStage.setScene(new Scene(LauncherViewCreator.createLauncherView()));
        primaryStage.show();
    }
}
