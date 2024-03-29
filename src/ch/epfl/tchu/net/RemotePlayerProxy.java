package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Représente un mandataire (proxy en anglais) de joueur distant.
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class RemotePlayerProxy implements Player, AutoCloseable, Runnable {
    private Helpers.MessageHandler handler;
    private final Socket socket;

    private RemotePlayerProxy() {
        this.handler = null;
        this.socket = null;
    }

    /**
     * Créé une instance de <i>RemotePlayerProxy</i>
     * @param socket le socket
     */
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String message = String.format(
                "%s %s",
                Serdes.PLAYERID.serialize(ownId), Serdes.LIST_STRING.serialize(new ArrayList<>(playerNames.values()))
        );

        this.handler.post(MessageId.INIT_PLAYERS, message);
    }

    @Override
    public void receiveInfo(String info) {
        String message = Serdes.STRING.serialize(info);

        this.handler.post(MessageId.RECEIVE_INFO, message);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String message = String.format(
                "%s %s",
                Serdes.PUBLICGAMESTATE.serialize(newState),
                Serdes.PLAYERSTATE.serialize(ownState)
        );

        this.handler.post(MessageId.UPDATE_STATE, message);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String message = Serdes.BAG_TICKET.serialize(tickets);

        this.handler.post(MessageId.SET_INITIAL_TICKETS, message);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        this.handler.post(MessageId.CHOOSE_INITIAL_TICKETS, "");

        Helpers.Payload message = this.handler.get();

        Preconditions.checkArgument(message.id().equals(MessageId.CHOOSE_INITIAL_TICKETS));

        return Serdes.BAG_TICKET.deserialize(message.content().get(0));
    }

    @Override
    public TurnKind nextTurn() {
        this.handler.post(MessageId.NEXT_TURN, "");

        Helpers.Payload message = this.handler.get();

        Preconditions.checkArgument(message.id().equals(MessageId.NEXT_TURN));

        return Serdes.TURNKIND.deserialize(message.content().get(0));
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        this.handler.post(MessageId.CHOOSE_TICKETS, Serdes.BAG_TICKET.serialize(options));

        Helpers.Payload message = this.handler.get();

        Preconditions.checkArgument(message.id().equals(MessageId.CHOOSE_TICKETS));

        return Serdes.BAG_TICKET.deserialize(message.content().get(0));
    }

    @Override
    public int drawSlot() {
        this.handler.post(MessageId.DRAW_SLOT, "");

        Helpers.Payload message = this.handler.get();

        Preconditions.checkArgument(message.id().equals(MessageId.DRAW_SLOT));

        return Serdes.INT.deserialize(message.content().get(0));
    }

    @Override
    public Route claimedRoute() {
        this.handler.post(MessageId.ROUTE, "");

        Helpers.Payload message = this.handler.get();

        Preconditions.checkArgument(message.id().equals(MessageId.ROUTE));

        return Serdes.ROUTE.deserialize(message.content().get(0));
    }

    @Override
    public void successfullyClaimedRoute(Route route) {
        this.handler.post(MessageId.CLAIMED, "");
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        this.handler.post(MessageId.CARDS, "");

        Helpers.Payload message = this.handler.get();

        Preconditions.checkArgument(message.id().equals(MessageId.CARDS));

        return Serdes.BAG_CARD.deserialize(message.content().get(0));
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        this.handler.post(MessageId.CHOOSE_ADDITIONAL_CARDS, Serdes.LIST_BAG_CARD.serialize(options));

        Helpers.Payload message = this.handler.get();

        Preconditions.checkArgument(message.id().equals(MessageId.CHOOSE_ADDITIONAL_CARDS));

        return Serdes.BAG_CARD.deserialize(message.content().get(0));
    }

    @Override
    public void lost(){
        this.handler.post(MessageId.LOST, "");
    }

    @Override
    public void won(){
        this.handler.post(MessageId.WON, "");
    }

    @Override
    public void close() throws Exception {
        this.handler.dispose();
    }

    @Override
    public void run() {
        try {
            assert socket != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            this.handler = new Helpers.MessageHandler(reader, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
