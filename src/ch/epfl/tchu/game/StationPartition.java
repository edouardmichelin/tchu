package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.Objects;

/**
 * Partition de stations
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] links;

    private StationPartition() {
        this.links = new int[0];
    }

    private StationPartition(int[] links) {
        this.links = links;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        try {
            return this.links[s1.id()] == this.links[s2.id()];
        } catch (Exception ignored) {
            return s1.id() == s2.id();
        }
    }

    public static final class Builder {
        private final int[] stations;
        private final boolean[] representatives;

        private Builder() {
            this.stations = null;
            this.representatives = null;
        }

        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            this.representatives = new boolean[stationCount];
            this.stations = new int[stationCount];
            Arrays.setAll(this.stations, p -> p);
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
            if (this.representatives[station])
                return station;

            int witness = -1;
            int representative = station;

            while (true) {
                representative = this.stations[representative];

                if (witness == representative)
                    return representative;

                witness = representative;
            }
        }
    }
}
