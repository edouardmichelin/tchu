/**
 * Les couleurs utilisées dans le jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

package ch.epfl.tchu.game;

import java.util.List;

/**
 * Couleurs du jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
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
     * Liste de toutes les couleurs
     */
    public final static List<Color> ALL = List.of(Color.values());

    /**
     * Nombre total de couleurs dans le jeu
     */
    public final static int COUNT = ALL.size();
}
