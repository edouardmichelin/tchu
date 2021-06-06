package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Contient le programme principal du client tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class ClientMain extends Application {
    /**
     * Lance l'application sur le file JavaFX
     * @param args arguments
     */
    public static void main(String[] args) { launch(args); }

    /**
     * Démarre le client
     * @param primaryStage <code>Stage</code> principal
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> args = getParameters().getRaw();

        String host = args.size() > 0 ? args.get(0) : "localhost";
        int port = args.size() > 1 ? Integer.parseInt(args.get(1)) : 5108;

        RemotePlayerClient player = new RemotePlayerClient(
                new GraphicalPlayerAdapter(),
                host,
                port
        );

        Thread network = new Thread(player::run);

        network.start();
    }
}
