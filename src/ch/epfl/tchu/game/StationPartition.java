package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;

/**
 * Partission de stations
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] links;

    private StationPartition() { this.links = new int[0]; }

    private StationPartition(int[] links) {
        this.links = links;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        return this.links[s1.id()] == this.links[s2.id()];
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
         * Joint les sous-ensembles contenant les deux gares passées en argument, en «élisant» l'un des deux représentants comme représentant du sous-ensemble joint ; retourne le bâtisseur (this)
         * @param s1 station 1
         * @param s2 station 2
         * @return le bâtisseur
         */
        public Builder connect(Station s1, Station s2) {
            if (this.representatives[s2.id()])
                this.stations[s1.id()] = s2.id();
            else if (this.representatives[s1.id()])
                this.stations[s2.id()] = s1.id();
            else {
                if (this.representative(s1.id()) == s1.id())
                    this.representatives[s1.id()] = true;
                else {
                    this.stations[s2.id()] = this.representative(s1.id());
                    return this;
                }

                connect(s1, s2);
            }

            return this;
        }

        /**
         * Retourne la partition aplatie des gares correspondant à la partition profonde en cours de construction par ce bâtisseur
         * @return la partition aplatie des gares correspondant à la partition profonde en cours de construction par ce bâtisseur
         */
        public StationPartition build() {
            int[] links = Arrays.stream(this.stations).map(this::representative).toArray();
            return new StationPartition(links);
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
