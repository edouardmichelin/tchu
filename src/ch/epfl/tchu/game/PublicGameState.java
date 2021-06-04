package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Représente la partie publique de l'état d'une partie de tCHu.
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class PublicGameState {
    private final int MIN_CARDS_LEFT_COUNT = 5;

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Construit la partie publique de l'état d'une partie de tCHu dans laquelle la pioche de billets a une taille de
     * ticketsCount, l'état public des cartes wagon/locomotive est cardState, le joueur courant est currentPlayerId,
     * l'état public des joueurs est contenu dans playerState, et l'identité du dernier joueur est lastPlayer (qui
     * peut être null si cette identité est encore inconnue) ; lève IllegalArgumentException si la taille de la
     * pioche est strictement négative ou si playerState ne contient pas exactement deux paires clef/valeur, et
     * NullPointerException si l'un des autres arguments (lastPlayer excepté !) est nul
     *
     * @param ticketsCount    le nombre de billets
     * @param cardState       l'état des cartes
     * @param currentPlayerId l'ID du joueur courrant
     * @param playerState     l'état du joueur
     * @param lastPlayer      le dernier joueur
     * @throws IllegalArgumentException si la taille de la pioche est strictement négative
     * @throws IllegalArgumentException si playerState ne contient pas exactement deux paires clef/valeur
     * @throws NullPointerException     si l'un des autres arguments (lastPlayer excepté !) est nul
     */
    public PublicGameState(
            int ticketsCount,
            PublicCardState cardState,
            PlayerId currentPlayerId,
            Map<PlayerId, PublicPlayerState> playerState,
            PlayerId lastPlayer
    ) {
        Preconditions.checkArgument(ticketsCount >= 0);
        // Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);

        this.ticketsCount = ticketsCount;
        this.playerState = Map.copyOf(playerState);

        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.lastPlayer = lastPlayer;
    }

    /**
     * Retourne la taille de la pioche de billets
     *
     * @return la taille de la pioche de billets
     */
    public int ticketsCount() {
        return this.ticketsCount;
    }

    /**
     * Retourne vrai ssi la pioche de billets n'est pas vide
     *
     * @return vrai ssi la pioche de billets n'est pas vide
     */
    public boolean canDrawTickets() {
        return this.ticketsCount > 0;
    }

    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive
     *
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        return this.cardState;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des cartes, c-à-d si la pioche et la défausse contiennent entre
     * elles au moins 5 cartes
     *
     * @return vrai ssi il est possible de tirer des cartes, c-à-d si la pioche et la défausse contiennent entre
     * elles au moins 5 cartes
     */
    public boolean canDrawCards() {
        return (this.cardState.deckSize() + this.cardState.discardsSize()) >= MIN_CARDS_LEFT_COUNT;
    }

    /**
     * Retourne l'identité du joueur actuel
     *
     * @return l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return this.currentPlayerId;
    }

    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée
     *
     * @param playerId le joueur dont on veut obtenir l'état public de la partie
     * @return la partie publique de l'état du joueur d'identité donnée
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return this.playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant
     *
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return this.playerState.get(this.currentPlayerId);
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     *
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>();

        this.playerState.forEach((player, playerState) -> claimedRoutes.addAll(playerState.routes()));

        return claimedRoutes;
    }

    /**
     * Retourne l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas
     * commencé
     *
     * @return l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas
     * commencé
     */
    public PlayerId lastPlayer() {
        return this.lastPlayer;
    }
}
