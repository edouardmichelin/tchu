package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class GameTest {

    /**
     * Ca lève une erreur parce que les joueurs ne peuvent plus claim de routes et choississent donc de DRAW_CARDS
     * mais la pioche est vide (le message que je t'ai envoyé sur whatsapp)
     */
    @Test
    void playWorks() {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random();
        List<String> names = List.of("Julien", "Edouard", "Bob");
        List<Route> allRoutes = ChMap.routes();

        PlayerId.ALL.forEach(playerId -> {
            players.put(playerId, new TestPlayer(rng.nextLong(), allRoutes));
            playerNames.put(playerId, names.get(playerId.ordinal()));
        });

        Game.play(players, playerNames, tickets, rng);
    }

    /**
     * Ca lève une erreur parce qu'il n'y a plus de cartes mais le joueur est encore capable de claim une route
     * (playerState.canClaimRoute retourne true dans TestPlayer.nextTurn lignes 64-65) donc il choisit CLAIM_ROUTES.
     */
    @Test
    void playWorks2() {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random();
        List<String> names = List.of("Julien", "Edouard", "Bob");

        // LA DIFFERENCE AVEC "playWorks" SE TROUVE ICI, ON FAIT LE JEU AVEC UNE PETITE LISTE DE ROUTES
        List<Route> allRoutes = ChMap.routes().subList(0, 10);

        PlayerId.ALL.forEach(playerId -> {
            players.put(playerId, new TestPlayer(rng.nextLong(), allRoutes));
            playerNames.put(playerId, names.get(playerId.ordinal()));
        });

        Game.play(players, playerNames, tickets, rng);
    }

    /**
     * Le constructeur est privé donc ça veut même pas compiler (donc c'est nickel)
     */
    @Test
    void gameInstanciationThrowsError() {
        assertThrows(Exception.class, () -> {
            // var game = new Game();
        }, "On ne doit pas pouvoir créer une instance de cette classe");
    }
}
