package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.gui.Info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        private Builder() {
            this.stations = null;
        }

        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            this.stations = new int[stationCount];
        }

        /**
         * Joint les sous-ensembles contenant les deux gares passées en argument, en «élisant» l'un des deux représentants comme représentant du sous-ensemble joint ; retourne le bâtisseur (this)
         * @param s1
         * @param s2
         * @return le bâtisseur
         */
        public Builder connect(Station s1, Station s2) {
            this.stations[this.representative(s1)] = s2.id();

            return this;
        }

        /**
         * Retourne la partition aplatie des gares correspondant à la partition profonde en cours de construction par ce bâtisseur
         * @return la partition aplatie des gares correspondant à la partition profonde en cours de construction par ce bâtisseur
         */
        public StationPartition build() {
            return null;
        }

        private int representative(Station station) {
            int representative = -1;
            while (representative != station.id()) {
                representative = this.stations[representative > -1 ? representative : station.id()];
            }

            return representative;
        }
    }
}
