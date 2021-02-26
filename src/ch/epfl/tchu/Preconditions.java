/**
 * Classe de vérification des préconditions
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
package ch.epfl.tchu;

public final class Preconditions {
    private Preconditions() {}

    /**
     * Lève une exception si l'argument passé est faux
     * @exception IllegalArgumentException
     * @param shouldBeTrue
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
