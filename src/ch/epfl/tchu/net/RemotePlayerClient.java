package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Représente un client de joueur distant
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class RemotePlayerClient {
    private Player player;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;


    private RemotePlayerClient() {
    }

    /**
     * Initialise la connexion avec le mandataire
     *
     * @param player le joueur courant
     * @param host   le nom d'hôte auquel se connecter
     * @param port   le port sur lequel se connecter
     */
    public RemotePlayerClient(Player player, String host, int port) {
        this.player = player;

        try (
                Socket socket = new Socket(host, port);
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

    /**
     * Effectue une boucle visant à attendre des messages provenant du mandataire et à les traiter.
     */
    public void run() {
        while (!this.socket.isClosed()) {
            try {
                if (this.reader.ready()) {
                    MessageContent message = this.getMessageContent();

                    switch (message.id()) {
                        case CARDS:
                            this.player.initialClaimCards();
                            break;
                        case ROUTE:
                            this.player.claimedRoute();
                            break;
                        case DRAW_SLOT:
                            this.player.drawSlot();
                            break;
                        case NEXT_TURN:
                            Player.TurnKind nextTurn = this.player.nextTurn();
                            sendMessage(message.id(), Serdes.TURNKIND.serialize(nextTurn));
                            break;
                        case INIT_PLAYERS:
                            PlayerId ownId = Serdes.PLAYERID.deserialize(message.content().get(0));
                            List<String> playerNames = Serdes.LIST_STRING.deserialize(message.content().get(1));
                            Map<PlayerId, String> playerNamesMap = new HashMap<>();

                            for (PlayerId playerId : PlayerId.ALL) {
                                playerNamesMap.put(playerId, playerNames.get(playerId.ordinal()));
                            }

                            player.initPlayers(ownId, playerNamesMap);
                            break;
                        case RECEIVE_INFO:
                            String info = Serdes.STRING.deserialize(message.content().get(0));
                            player.receiveInfo(info);
                            break;
                        case UPDATE_STATE:
                            PublicGameState newState = Serdes.PUBLICGAMESTATE.deserialize(message.content().get(0));
                            PlayerState ownState = Serdes.PLAYERSTATE.deserialize(message.content().get(1));
                            player.updateState(newState, ownState);
                            break;
                        case CHOOSE_TICKETS:
                            SortedBag<Ticket> options = Serdes.BAG_TICKET.deserialize(message.content().get(0));
                            SortedBag<Ticket> chosenTickets = player.chooseTickets(options);
                            sendMessage(message.id(), Serdes.BAG_TICKET.serialize(chosenTickets));
                            break;
                        case SET_INITIAL_TICKETS:
                            SortedBag<Ticket> tickets = Serdes.BAG_TICKET.deserialize(message.content().get(0));
                            player.setInitialTicketChoice(tickets);
                            break;
                        case CHOOSE_INITIAL_TICKETS:
                            SortedBag<Ticket> initialTicketsChoice = player.chooseInitialTickets();
                            sendMessage(message.id(), Serdes.BAG_TICKET.serialize(initialTicketsChoice));
                            break;
                        case CHOOSE_ADDITIONAL_CARDS:
                            List<SortedBag<Card>> additionalCardsOptions =
                                    Serdes.LIST_BAG_CARD.deserialize(message.content().get(0));
                            SortedBag<Card> chosenAdditionalCards =
                                    player.chooseAdditionalCards(additionalCardsOptions);
                            sendMessage(message.id(), Serdes.BAG_CARD.serialize(chosenAdditionalCards));
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }g


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
