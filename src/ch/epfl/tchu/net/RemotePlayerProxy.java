package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Représente un mandataire (proxy en anglais) de joueur distant.
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class RemotePlayerProxy implements Player {
    private BufferedReader reader;
    private BufferedWriter writer;

    private RemotePlayerProxy() {
    }

    public RemotePlayerProxy(Socket socket) {
        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(socket.getInputStream(),
                                        US_ASCII));
                BufferedWriter writer =
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream(),
                                        US_ASCII))) {
            this.reader = reader;
            this.writer = writer;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String formatMessage(MessageId messageId, String message) {
        return String.format("%s %s\n", messageId.name(), message);
    }

    private void sendMessage(MessageId messageId, String message) {
        try {
            this.writer.write(formatMessage(messageId, message));
            this.writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private MessageContent getMessageContent() {
        try {
            return new MessageContent(this.reader.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String message = String.format(
                "%s %s",
                Serdes.PLAYERID.serialize(ownId), Serdes.LIST_STRING.serialize(new ArrayList<>(playerNames.values()))
        );

        sendMessage(MessageId.INIT_PLAYERS, message);
    }

    @Override
    public void receiveInfo(String info) {
        String message = Serdes.STRING.serialize(info);

        sendMessage(MessageId.RECEIVE_INFO, message);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String message = String.format(
                "%s %s",
                Serdes.PUBLICGAMESTATE.serialize(newState),
                Serdes.PLAYERSTATE.serialize(ownState)
        );

        sendMessage(MessageId.UPDATE_STATE, message);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String message = Serdes.BAG_TICKET.serialize(tickets);

        sendMessage(MessageId.SET_INITIAL_TICKETS, message);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        MessageContent message = getMessageContent();

        Preconditions.checkArgument(message.id().equals(MessageId.CHOOSE_INITIAL_TICKETS));

        return Serdes.BAG_TICKET.deserialize(message.content().get(0));
    }

    @Override
    public TurnKind nextTurn() {
        MessageContent message = getMessageContent();

        Preconditions.checkArgument(message.id().equals(MessageId.NEXT_TURN));

        return Serdes.TURNKIND.deserialize(message.content().get(0));
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS, Serdes.BAG_TICKET.serialize(options));

        MessageContent message = getMessageContent();

        Preconditions.checkArgument(message.id().equals(MessageId.CHOOSE_TICKETS));

        return Serdes.BAG_TICKET.deserialize(message.content().get(0));
    }

    @Override
    public int drawSlot() {
        MessageContent message = getMessageContent();

        Preconditions.checkArgument(message.id().equals(MessageId.DRAW_SLOT));

        return Serdes.INT.deserialize(message.content().get(0));
    }

    @Override
    public Route claimedRoute() {
        MessageContent message = getMessageContent();

        Preconditions.checkArgument(message.id().equals(MessageId.ROUTE));

        return Serdes.ROUTE.deserialize(message.content().get(0));
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        MessageContent message = getMessageContent();

        Preconditions.checkArgument(message.id().equals(MessageId.CARDS));

        return Serdes.BAG_CARD.deserialize(message.content().get(0));
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, Serdes.LIST_BAG_CARD.serialize(options));

        MessageContent message = getMessageContent();

        Preconditions.checkArgument(message.id().equals(MessageId.CHOOSE_ADDITIONAL_CARDS));

        return Serdes.BAG_CARD.deserialize(message.content().get(0));
    }

    private static class MessageContent {
        private final MessageId id;
        private final List<String> content;

        public MessageContent(String message) {
            String[] messageParts = message.split(Pattern.quote(" "), -1);

            assert messageParts.length >= 2;

            this.id = MessageId.valueOf(messageParts[0]);
            this.content = List.of(messageParts).subList(1, messageParts.length);
        }

        public MessageId id() {
            return this.id;
        }

        public List<String> content() {
            return this.content;
        }
    }
}
