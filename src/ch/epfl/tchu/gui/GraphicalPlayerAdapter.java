package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Adapter de <i>GraphicalPlayer</i>
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class GraphicalPlayerAdapter implements Player {
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

    /**
     * Construit, sur le fil JavaFX, l'instance du joueur graphique GraphicalPlayer qu'elle adapte; cette instance
     * est stockée dans un attribut (celui nommé graphicalPlayer dans les exemples ci-dessus) afin de pouvoir être
     * utilisée par les autres méthodes
     *
     * @param ownId       identité du joueur
     * @param playerNames nom des joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> this.graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * Appelle, sur le fil JavaFX, la méthode du même nom du joueur graphique
     *
     * @param info information à communiquer
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * Appelle, sur le fil JavaFX, la méthode setState du joueur graphique
     *
     * @param newState nouvel état du joueur
     * @param ownState propre état du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * Appelle, sur le fil JavaFX, la méthode chooseTickets du joueur graphique, pour lui demander de choisir ses
     * billets initiaux, en lui passant un gestionnaire de choix qui stocke le choix du joueur dans une file bloquante
     *
     * @param tickets billets qui ont été distribués au joueur
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, this.ticketsChoice::add));
    }

    /**
     * Bloque en attendant que la file utilisée également par setInitialTicketChoice contienne une valeur, puis la
     * retourne
     *
     * @return Les tickets choisis
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return this.ticketsChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**
     * Appelle, sur le fil JavaFX, la méthode startTurn du joueur graphique, en lui passant des gestionnaires
     * d'action qui placent le type de tour choisi, de même que les éventuels «arguments» de l'action—p.ex. la
     * route dont le joueur désire s'emparer—dans des files bloquantes, puis bloque en attendant qu'une valeur soit
     * placée dans la file contenant le type de tour, qu'elle retire et retourne
     *
     * @return Le type de tour à jouer qui a été choisi
     */
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

    /**
     * Enchaîne les actions effectuées par setInitialTicketChoice et chooseInitialTickets
     *
     * @param options les billets tirés
     * @return Les tickets choisis
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        this.setInitialTicketChoice(options);
        return this.chooseInitialTickets();
    }

    /**
     * Teste (sans bloquer !) si la file contenant les emplacements des cartes contient une valeur ; si c'est le cas,
     * cela signifie que drawSlot est appelée pour la première fois du tour, et que le gestionnaire installé par
     * nextTurn a placé l'emplacement de la première carte tirée dans cette file, qu'il suffit donc de retourner ;
     * sinon, cela signifie que drawSlot est appelée pour la seconde fois du tour, afin que le joueur tire sa seconde
     * carte, et il faut donc appeler, sur le fil JavaFX, la méthode drawCard du joueur graphique, avant de bloquer
     * en attendant que le gestionnaire qu'on lui passe place l'emplacement de la carte tirée dans la file, qui est
     * alors extrait et retourné,
     *
     * @return L'emplacement sur lequel la pioche s'effectue
     */
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

    /**
     * Extrait et retourne le premier élément de la file contenant les routes, qui y aura été placé par le
     * gestionnaire passé à startTurn par nextTurn,
     *
     * @return la route saisie
     */
    @Override
    public Route claimedRoute() {
        try {
            return this.routeChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**
     * Similaire à claimedRoute mais utilise la file contenant les multiensembles de cartes
     *
     * @return le choix initial des cartes pour s'emparer d'une route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return this.cardsChoice.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**
     * Appelle, sur le fil JavaFX, la méthode du même nom du joueur graphique puis bloque en attendant qu'un élément
     * soit placé dans la file contenant les multiensembles de cartes, qu'elle retourne.
     *
     * @param options les possibilités de cartes
     * @return Les cartes du cout additionnel pour s'emparer d'un tunnel
     */
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
