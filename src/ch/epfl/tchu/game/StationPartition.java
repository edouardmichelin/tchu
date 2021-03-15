package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

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
        return false;
    }

    public static final class Builder {
        private final List<Station> stations;

        private Builder() {
            this.stations = null;
        }

        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            this.stations = new ArrayList<>(stationCount);
        }

        /**
         * Joint les sous-ensembles contenant les deux gares passées en argument, en «élisant» l'un des deux représentants comme représentant du sous-ensemble joint ; retourne le bâtisseur (this)
         * @param s1
         * @param s2
         * @return le bâtisseur
         */
        public Builder connect(Station s1, Station s2) {
            return null;
        }

        /**
         * Retourne la partition aplatie des gares correspondant à la partition profonde en cours de construction par ce bâtisseur
         * @return la partition aplatie des gares correspondant à la partition profonde en cours de construction par ce bâtisseur
         */
        public StationPartition build() {}
    }
}
