/**
 * Les gares du jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Représente les stations du tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Station {
    private final int id;
    private final String name;

    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.name = name;
    }

    /**
     * Retourne l'id de cette gare
     *
     * @return l'id de cette gare
     */
    public int id() {
        return this.id;
    }

    /**
     * Retourne le nom de cette gare
     *
     * @return le nom de cette gare
     */
    public String name() {
        return this.name;
    }

    /**
     * Retourne le nom de cette gare
     *
     * @return le nom de cette gare
     */
    @Override
    public String toString() {
        return this.name;
    }
}
