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

    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty());
        Preconditions.checkArgument(!to.isEmpty());
        Preconditions.checkArgument(points > 0);

        List<Trip> trips = new ArrayList<>();

        from.forEach(departure -> to.forEach(arrival -> trips.add(new Trip(departure, arrival, points))));

        return trips;
    }

    public Station from() { return this.from; }
    public Station to() { return this.to; }
    public int points() { return this.points; }

    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? this.points : (this.points * -1);
    }
}
