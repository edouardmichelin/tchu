package ch.epfl.tchu.gui;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Serveur local de test
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class FakeServer {
    // Messages d'exemple de la §2.1 de l'étape 7
    private static final List<String> MESSAGES = List.of(
            "INIT_PLAYERS 0 QWRh,Q2hhcmxlcw==",
            "RECEIVE_INFO QWRhIGpvdWVyYSBlbiBwcmVtaWVyLgoK",
            "SET_INITIAL_TICKETS 6,1,44,42,42",
            "UPDATE_STATE 36:6,7,4,7,1;97;0:0:0;4;:0;4;: ;0,1,5,5;",
            // "UPDATE_STATE 36:6,7,2,0,6;97;0:1:0;4;!0;4;: ;0,1,5,5;",
            "CHOOSE_INITIAL_TICKETS");

    public static void main(String[] args) throws IOException {
        try (ServerSocket s0 = new ServerSocket(5108);
             Socket s = s0.accept();
             BufferedReader r = new BufferedReader(
                     new InputStreamReader(s.getInputStream(),
                             US_ASCII));
             BufferedWriter w = new BufferedWriter(
                     new OutputStreamWriter(s.getOutputStream(),
                             US_ASCII))) {
            // Envoi des messages
            for (String m : MESSAGES) {
                w.write(m + '\n');
                w.flush();
            }
            // Attente et impression de la réponse
            System.out.println(r.readLine());
        }
    }
}
