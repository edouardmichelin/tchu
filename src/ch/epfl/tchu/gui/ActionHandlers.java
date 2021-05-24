package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Gestionnaires d'actions
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public interface ActionHandlers {
    /**
     * Gestionnaire de tirage de billets
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Appelé lors du tirage de tickets
         */
        void onDrawTickets();
    }

    /**
     * Gestionnaire de tirage de cartes
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Appelé lors du tirage de cartes
         */
        void onDrawCard(int slot);
    }

    /**
     * Gestionnaire de réclamation de routes
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Appelé lorsque le joueur tente de s'emparer d'une route
         * @param route la route réclamée
         * @param cards la liste de carte choisie
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * Gestionnaire de choix de tickets
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Appelé lors du choix de tickets
         * @param tickets la liste de tickets choisis
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * Gestionnaire de choix de cartes
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Appelé lors du choix de cartes
         * @param cards la liste de cartes choisies
         */
        void onChooseCards(SortedBag<Card> cards);
    }
}
