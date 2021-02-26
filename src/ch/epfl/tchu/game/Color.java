/**
 * Les couleures utilisées dans le jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

package ch.epfl.tchu.game;

import java.util.List;

public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * Liste de toutes les couleures
     */
    public final static List<Color> ALL = List.of(Color.values());

    /**
     * Nombre total de couleures dans le jeu
     */
    public final static int COUNT = ALL.size();
}
