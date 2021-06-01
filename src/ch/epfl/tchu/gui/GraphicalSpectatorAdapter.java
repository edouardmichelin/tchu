package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;

import static javafx.application.Platform.runLater;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class GraphicalSpectatorAdapter implements Player {
    private GraphicalSpectator graphicalSpectator;

    private static final PlayerState DUMMY_PLAYER_STATE = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());

    /**
     * Créé, sur le fil JavaFx, l'instance du spectateur graphique
     * @param ownId       identité du joueur
     * @param playerNames nom des joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> this.graphicalSpectator = new GraphicalSpectator(PlayerId.PLAYER_1, playerNames));
    }


    /**
     * Appelle, sur le fil JavaFX, la méthode du même nom du spectateur graphique
     *
     * @param info information à communiquer
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalSpectator.receiveInfo(info));
    }

    /**
     * Appelle, sur le fil JavaFX, la méthode setState du spectateur graphique
     *
     * @param newState nouvel état du joueur
     * @param ownState propre état du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalSpectator.setState(newState, DUMMY_PLAYER_STATE));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return null;
    }

    @Override
    public TurnKind nextTurn() {
        return null;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        return null;
    }

    @Override
    public int drawSlot() {
        return 0;
    }

    @Override
    public Route claimedRoute() {
        return null;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return null;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        return null;
    }
}
