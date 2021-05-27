package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Partition de stations
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] links;

    private StationPartition(int[] links) {
        this.links = links;
    }

    /**
     * Retourne vrai ssi les deux stations spécifiées en argument sont connectées
     *
     * @param s1 première station
     * @param s2 seconde station
     * @return <code>true</code> ssi les stations sont connectées
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() < links.length && s2.id() < links.length)
            return this.links[s1.id()] == this.links[s2.id()];

        return s1.id() == s2.id();
    }

    /**
     * Représente un bâtisseur de partition de gare
     */
    public static final class Builder {
        private final int[] stations;

        /**
         * Construit un bâtisseur de partition d'un ensemble de gares dont l'identité est comprise entre 0 (inclus)
         * et stationCount (exclus), ou lève IllegalArgumentException si stationCount est strictement négatif (< 0)
         *
         * @param stationCount le nombre de stations
         * @throws IllegalArgumentException si stationCount est strictement négatif (< 0)
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            this.stations = IntStream.range(0, stationCount).toArray();
        }

        /**
         * Connecte deux gares entre elles en élisant au besoin l'une des deux comme représentante ; retourne le
         * bâtisseur (this)
         *
         * @param s1 station 1
         * @param s2 station 2
         * @return le bâtisseur (this)
         */
        public Builder connect(Station s1, Station s2) {
            if (this.stations[s1.id()] == this.stations[s2.id()])
                return this;

            int representative1 = this.representative(s1.id());
            int representative2 = this.representative(s2.id());

            for (int i = 0; i < this.stations.length; i++) {
                if (this.stations[i] == representative2) {
                    this.stations[i] = representative1;
                }
            }

            return this;
        }

        /**
         * Retourne la partition aplatie des gares correspondant à la partition profonde en cours de construction par
         * ce bâtisseur
         *
         * @return la partition aplatie des gares correspondant à la partition profonde en cours de construction par
         * ce bâtisseur
         */
        public StationPartition build() {
            return new StationPartition(Arrays.copyOf(this.stations, this.stations.length));
        }

        private int representative(int station) {
            int witness = -1;
            int representative = station;

            while (witness != representative) {
                representative = this.stations[representative];
                witness = representative;
            }
            return representative;
        }
    }
}
