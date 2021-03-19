/**
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */

package ch.epfl.tchu.game;


public interface StationConnectivity {
    /**
     * Retourne vrai si les deux stations spécifiée sont connectées
     *
     * @param s1 première station
     * @param s2 seconde station
     * @return vrai si les stations sont connectées
     */
    boolean connected(Station s1, Station s2);

}
