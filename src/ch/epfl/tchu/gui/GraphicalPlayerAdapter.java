package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;

    private final BlockingQueue<SortedBag<Ticket>> ticketsChoice = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> cardsChoice = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> slotChoice = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> routeChoice = new ArrayBlockingQueue<>(1);

    /**
     * Créé une instance de <i>GraphicalPlayerAdapter</i>
     */
    public GraphicalPlayerAdapter() {
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        this.graphicalPlayer = new GraphicalPlayer(ownId, playerNames);
    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));

    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, this.ticketsChoice::add));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return this.ticketsChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnKind = new ArrayBlockingQueue<>(1);

        runLater(() -> this.graphicalPlayer.startTurn(
                () -> turnKind.add(TurnKind.DRAW_TICKETS),
                slot -> {
                    turnKind.add(TurnKind.DRAW_CARDS);
                    this.slotChoice.add(slot);
                },
                (route, cards) -> {
                    turnKind.add(TurnKind.CLAIM_ROUTE);
                    this.routeChoice.add(route);
                    this.cardsChoice.add(cards);
                }
        ));

        try {
            return turnKind.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        this.setInitialTicketChoice(options);
        return this.chooseInitialTickets();
    }

    @Override
    public int drawSlot() {
        try {
            if (this.slotChoice.isEmpty()) {
                runLater(() -> this.graphicalPlayer.drawCard(this.slotChoice::add));
                return this.slotChoice.take();
            }

            return this.slotChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public Route claimedRoute() {
        try {
            return this.routeChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return this.cardsChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        try {
            runLater(() -> this.graphicalPlayer.chooseAdditionalCards(options, this.cardsChoice::add));
            return this.cardsChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }
}
