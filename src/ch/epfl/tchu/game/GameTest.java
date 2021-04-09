package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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



    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;
        private PlayerId ownId;
        private Map<PlayerId, String> playerNames;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;
        private SortedBag<Ticket> initialTicketChoice;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            this.playerNames = playerNames;
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initialTicketChoice = tickets;
            this.ownState = this.ownState.withAddedTickets(tickets);
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return this.initialTicketChoice;
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes =
                    this.allRoutes.stream().filter(ownState::canClaimRoute).collect(Collectors.toList());
            if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return options;
        }

        @Override
        public int drawSlot() {
            return this.rng.nextBoolean() ? this.rng.nextInt(5) : Constants.DECK_SLOT;
        }

        @Override
        public Route claimedRoute() {
            return this.routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return this.initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return !options.isEmpty() ? options.get(0) : SortedBag.of();
        }

        @Override
        public void receiveInfo(String message) {
            System.out.println(message);
        }
    }
}
