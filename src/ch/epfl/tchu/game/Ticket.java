package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.stream.Collectors;

public final class Ticket implements Comparable<Ticket> {
    private List<Trip> trips;
    private String text;

    Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());

        this.trips = tryStoreTrips(trips);
        this.text = computeText(trips);
    }

    Ticket(Station from, Station to, int points) {
        this(List.of((new Trip(from, to, points))));
    }

    public String text() {
        return this.text;
    }

    public int points(StationConnectivity connectivity) {
        return trips.stream().mapToInt(trip -> trip.points(connectivity)).max().getAsInt();
    }

    private static String computeText(List<Trip> trips) {
        Trip firstTrip = trips.get(0);

        if (trips.size() > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s - {", firstTrip.from().name()));

            sb.append(trips
                    .stream()
                    .map(trip -> String.format("%s (%s)", trip.to().name(), trip.points()))
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", ")));

            sb.append("}");

            return sb.toString();
        } else {
            return String
                    .format("%s - %s (%d)", firstTrip.from().name(), firstTrip.to().name(), firstTrip.points());
        }
    }

    private static List<Trip> tryStoreTrips(List<Trip> trips) {
        String witness = trips.get(0).from().name();
        boolean validator = trips
                .stream()
                .allMatch(trip -> trip.from().name().equals(witness));

        if (validator) return trips;

        throw new IllegalArgumentException();
    }

    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text());
    }

    @Override
    public String toString() {
        return this.text;
    }
}
