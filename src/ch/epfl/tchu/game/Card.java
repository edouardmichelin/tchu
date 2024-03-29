/**
 * Tous les types de cartes wagon et locomotive
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

package ch.epfl.tchu.game;

import java.util.List;
import java.util.stream.Collectors;

public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;

    Card(Color color) {
        this.color = color;
    }

    /**
     * Liste tous les types de cartes
     */
    public final static List<Card> ALL = List.of(Card.values());

    /**
     * Donne le nombre des différents types de cartes
     */
    public final static int COUNT = ALL.size();

    /**
     * Liste uniquement les cartes wagons (celles qui ont des couleurs)
     */
    public final static List<Card> CARS = ALL.stream()
            .filter(card -> card.color != null)
            .collect(Collectors.toUnmodifiableList());

    /**
     * Retourne le type de carte correspondant à une couleur donnée
     *
     * @param color la couleur de la carte
     * @return le type de carte correspondant à la couleur ou <code>null</code> si
     * aucune carte n'est trouvée pour la couleur donnée.
     */
    public static Card of(Color color) {
        return ALL.stream()
                .filter(card -> card.color.equals(color))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retourne la couleur de cette carte
     *
     * @return la couleur de cette carte
     */
    public Color color() {
        return this.color;
    }
}
