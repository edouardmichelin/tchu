package ch.epfl.tchu.game;

import java.util.List;

/**
 * Identité du joueur
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3;

    /**
     * Liste tous les IDs des joueurs
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * Retourne le nombre de joueurs
     */
    public final static int COUNT = ALL.size();

    /**
     * Retourne l'identité du joueur qui suit celui auquel on l'applique
     *
     * @return l'identité du joueur suivant
     */
    public PlayerId next() {
        return ALL.get((this.ordinal() + 1) % COUNT);
    }
}
