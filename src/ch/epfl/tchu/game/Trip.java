/**
 * Les trajets qui relies les gares
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull((to));
        this.points = points;
    }

    /**
     * Composer et retourne une liste de toutes les possibilités de trajets entre deux
     * ensembles de gares avec le nombre de points correspondant attribué.
     * @param from
     * @param to
     * @param points
     * @return Une liste de tous les trajets possibles entre deux ensembles de gares
     *              avec leurs points attribués.
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty());
        Preconditions.checkArgument(!to.isEmpty());
        Preconditions.checkArgument(points > 0);

        List<Trip> trips = new ArrayList<>();

        from.forEach(departure -> to.forEach(arrival -> trips.add(new Trip(departure, arrival, points))));

        return trips;
    }

    /**
     * Retourne la gare de départ de ce trajet
     * @return la gare de départ de ce trajet
     */
    public Station from() { return this.from; }

    /**
     * Retourne la gare d'arrivée de ce trajet
     * @return la gare d'arrivée de ce trajet
     */
    public Station to() { return this.to; }

    /**
     * Retourne les points de ce trajet
     * @return les points de ce trajet
     */
    public int points() { return this.points; }

    /**
     * Retourne les points de ce trajet en fonction de sa connexion
     * @param connectivity
     * @return les points de ce trajet en fonction de sa connexion
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? this.points : (this.points * -1);
    }
}
