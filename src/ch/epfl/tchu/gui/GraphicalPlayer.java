package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

import java.util.List;
import java.util.Map;

/**
 * Représente l'interface graphique d'un joueur de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class GraphicalPlayer {
    private final ObservableGameState gameState;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.gameState = new ObservableGameState(playerId);

        ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler = null;
        ObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandler = null;
        ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler = null;

        MapViewCreator.CardChooser cardChooser = null;

        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, cardChooser);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHandler, drawCardHandler);
        Node handView = DecksViewCreator.createHandView(gameState);

    }

    public void setState(PublicGameState gameState, PlayerState playerState) {
        this.gameState.setState(gameState, playerState);
    }

    /**
     * Prend un message — de type String — et l'ajoute au bas des informations sur le déroulement de la partie,
     * qui sont présentées dans la partie inférieure de la vue des informations.
     *
     * @param info le message à passer
     */
    public void receiveInfo(String info) {

    }

    /**
     * Prend en arguments trois gestionnaires d'action, un par types d'actions que le joueur peut effectuer lors d'un
     * tour, et qui permet au joueur d'en effectuer une
     *
     * @param drawTicketsHandler le gestionnaire de tirage des billets
     * @param drawCardHandler    le gestionnaire de tirage des cartes
     * @param claimRouteHandler  le gestionnaire de prise de possession d'une route
     */
    public void startTurn(
            ActionHandlers.DrawTicketsHandler drawTicketsHandler,
            ActionHandlers.DrawCardHandler drawCardHandler,
            ActionHandlers.ClaimRouteHandler claimRouteHandler
    ) {
    }

    /**
     * Prend en arguments un multiensemble contenant cinq ou trois billets que le joueur peut choisir et un
     * gestionnaire de choix de billets — de type <i>ChooseTicketsHandler</i> —, et qui ouvre une fenêtre permettant au
     * joueur de faire son choix ; une fois celui-ci confirmé, le gestionnaire de choix est appelé avec ce choix en
     * argument
     *
     * @throws IllegalArgumentException ssi la taille de <code>tickets</code> est différente de 3 ou 5
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        Preconditions.checkArgument(tickets.size() == 3 || tickets.size() == 5);
    }

    /**
     * Prend en argument un gestionnaire de tirage de carte — de type <i>DrawCardHandler</i> — et qui autorise le
     * joueur à choisir une carte wagon/locomotive, soit l'une des cinq dont la face est visible, soit celle du
     * sommet de la pioche ; une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire est appelé avec
     * le choix du joueur
     *
     * @param drawCardHandler le gestionnaire de tirage de carte
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {

    }

    /**
     * Prend en arguments une liste de multiensembles de cartes, qui sont les cartes initiales qu'il peut utiliser
     * pour s'emparer d'une route, et un gestionnaire de choix de cartes — de type <i>ChooseCardsHandler</i> —, et
     * qui ouvre une fenêtre similaire à celle de la figure 5 permettant au joueur de faire son choix ; une fois que
     * celui-ci a été fait et confirmé, le gestionnaire de choix est appelé avec le choix du joueur en argument
     *
     * @param setsOfCards          la liste de multiensembles de cartes
     * @param chooseTicketsHandler le gestionnaire de choix de cartes
     */
    private void chooseClaimCards(
            List<SortedBag<Card>> setsOfCards,
            ActionHandlers.ChooseTicketsHandler chooseTicketsHandler
    ) {

    }

    /**
     * Prend en arguments une liste de multiensembles de cartes, qui sont les cartes additionnelles qu'il peut
     * utiliser pour s'emparer d'un tunnel et un gestionnaire de choix de cartes — de type
     * <i>ChooseCardsHandler</i> —, et qui ouvre une fenêtre permettant au joueur de faire son choix ; une fois que
     * celui-ci a été fait et confirmé
     *
     * @param setsOfCards        la liste de multiensembles de cartes
     * @param chooseCardsHandler le gestionnaire de choix de cartes
     */
    public void chooseAdditionalCards(
            List<SortedBag<Card>> setsOfCards,
            ActionHandlers.ChooseCardsHandler chooseCardsHandler
    ) {

    }
}
