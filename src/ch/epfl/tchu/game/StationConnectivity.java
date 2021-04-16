/**
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

package ch.epfl.tchu.game;

/**
 *
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public interface StationConnectivity {
    /**
     * Retourne vrai ssi les deux stations spécifiées en argument sont connectées
     *
     * @param s1 première station
     * @param s2 seconde station
     * @return <code>true</code> ssi les stations sont connectées
     */
    boolean connected(Station s1, Station s2);

}
