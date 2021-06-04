package ch.epfl.tchu.net;

/**
 * Les types de messages que le serveur peut envoyer aux clients
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS,
    LOST,
    WON,
    CLAIMED;
}
