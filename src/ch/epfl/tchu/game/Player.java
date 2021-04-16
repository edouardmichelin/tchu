package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Représente un joueur
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public interface Player {
    /**
     * Appelée au début de la partie pour communiquer au joueur sa propre identité ownId, ainsi que les noms des
     * différents joueurs, le sien inclus, qui se trouvent dans playerNames
     *
     * @param ownId       identité du joueur
     * @param playerNames nom des joueurs
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie
     *
     * @param info information à communiquer
     */
    void receiveInfo(String info);

    /**
     * Appelée chaque fois que l'état du jeu a changé, pour informer le joueur de la composante publique de ce nouvel
     * état, newState, ainsi que de son propre état, ownState
     *
     * @param newState nouvel état du joueur
     * @param ownState propre état du joueur
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Appelée au début de la partie pour communiquer au joueur les cinq billets qui lui ont été distribués
     *
     * @param tickets billets qui ont été distribués au joueur
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * appelée au début de la partie pour demander au joueur lesquels des billets qu'on lui a distribué initialement
     * il garde
     *
     * @return les billets gardés
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Appelée au début du tour d'un joueur, pour savoir quel type d'action il désire effectuer durant ce tour
     *
     * @return le type d'action du tour
     */
    TurnKind nextTurn();

    /**
     * Appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie, afin de lui
     * communiquer les billets tirés et de savoir lesquels il garde
     *
     * @param options les billets tirés
     * @return les billets choisis
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive, afin de savoir d'où il désire les
     * tirer : d'un des emplacements contenant une carte face visible — auquel cas la valeur retourne est comprise
     * entre 0 et 4 inclus —, ou de la pioche — auquel cas la valeur retournée vaut Constants.DECK_SLOT
     *
     * @return le slot
     */
    int drawSlot();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit
     *
     * @return la route
     */
    Route claimedRoute();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir quelle(s) carte(s) il
     * désire initialement utiliser pour cela
     *
     * @return les cartes que le joueur décide d'utiliser
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles sont
     * nécessaires, afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant
     * passées en argument ; si le multiensemble retourné est vide, cela signifie que le joueur ne désire pas (ou ne
     * peut pas) choisir l'une de ces possibilités
     *
     * @param options les possibilités de cartes
     * @return les cartes choisies par le joueur
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * Représente les trois types d'actions qu'un joueur de tCHu peut effectuer durant un tour
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * Retourne la liste de tous les TurnKind
         *
         * @return la liste de tous les <code>TurnKind</code>
         */
        public static List<TurnKind> all() {
            return List.of(TurnKind.values());
        }
    }
}
