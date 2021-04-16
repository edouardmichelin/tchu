/**
 * Classe de vérification des préconditions
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
package ch.epfl.tchu;

/**
 * Les pré-conditions
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Preconditions {
    private Preconditions() {}

    /**
     * Lève une exception de type IllegalArgumentException si l'argument passé est faux
     * @exception IllegalArgumentException
     * @param shouldBeTrue condition à tester
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
