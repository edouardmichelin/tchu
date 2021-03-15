package ch.epfl.tchu.game;

import java.util.List;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    /**
     * Liste tous les IDs des joueurs
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * Donne le nombre de joueurs
     */
    public final static int COUNT = ALL.size();

    public PlayerId next() {
        return ALL.get((this.ordinal() + 1) % COUNT);
    }
}
