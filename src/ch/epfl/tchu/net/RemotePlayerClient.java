package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;


/**
 * Représente un client de joueur distant
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class RemotePlayerClient implements AutoCloseable {
    private final Helpers.MessageHandler handler;
    private Player player;
    private Socket socket;


    private RemotePlayerClient() {
        this.handler = null;
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

        try {
            Socket socket = new Socket(host, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

            this.socket = socket;
            this.handler = new Helpers.MessageHandler(reader, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Effectue une boucle visant à attendre des messages provenant du mandataire et à les traiter.
     */
    public void run() {
        while (!this.socket.isClosed()) {
            if (this.handler.ready()) {
                Helpers.Payload message = this.handler.get();

                switch (message.id()) {
                    case CARDS:
                        SortedBag<Card> cards = this.player.initialClaimCards();
                        this.handler.post(message.id(), Serdes.BAG_CARD.serialize(cards));
                        break;
                    case ROUTE:
                        Route route = this.player.claimedRoute();
                        this.handler.post(message.id(), Serdes.ROUTE.serialize(route));
                        break;
                    case DRAW_SLOT:
                        int slot = this.player.drawSlot();
                        this.handler.post(message.id(), Serdes.INT.serialize(slot));
                        break;
                    case NEXT_TURN:
                        Player.TurnKind nextTurn = this.player.nextTurn();
                        this.handler.post(message.id(), Serdes.TURNKIND.serialize(nextTurn));
                        break;
                    case INIT_PLAYERS:
                        PlayerId ownId = Serdes.PLAYERID.deserialize(message.content().get(0));
                        List<String> playerNames = Serdes.LIST_STRING.deserialize(message.content().get(1));
                        Map<PlayerId, String> playerNamesMap = new HashMap<>();

                        Globals.NUMBER_OF_PLAYERS = playerNames.size();

                        List<PlayerId> ALL_PLAYERS = PlayerId.ALL;

                        for (int id = 0; id < Globals.NUMBER_OF_PLAYERS; id++) {
                            playerNamesMap.put(ALL_PLAYERS.get(id), playerNames.get(id));
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
                        this.handler.post(message.id(), Serdes.BAG_TICKET.serialize(chosenTickets));
                        break;
                    case SET_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = Serdes.BAG_TICKET.deserialize(message.content().get(0));
                        player.setInitialTicketChoice(tickets);
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> initialTicketsChoice = player.chooseInitialTickets();
                        this.handler.post(message.id(), Serdes.BAG_TICKET.serialize(initialTicketsChoice));
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> additionalCardsOptions =
                                Serdes.LIST_BAG_CARD.deserialize(message.content().get(0));
                        SortedBag<Card> chosenAdditionalCards =
                                player.chooseAdditionalCards(additionalCardsOptions);
                        this.handler.post(message.id(), Serdes.BAG_CARD.serialize(chosenAdditionalCards));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        this.handler.dispose();
        this.socket.close();
    }
}
