import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
    public void receiveInfo(String message) {
        System.out.println(message);
    }
}