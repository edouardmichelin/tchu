package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicReference;

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

        GridPane launcherBox = LauncherViewCreator.createLauncherView(primaryStage);

        Scene scene = new Scene(launcherBox);
        scene.getStylesheets().add("launcher.css");

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setHeight(604);
        primaryStage.setWidth(604);


        primaryStage.show();
    }
}
