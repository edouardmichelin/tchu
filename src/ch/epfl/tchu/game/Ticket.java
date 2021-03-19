/**
 * Les tickets que les joueurs peuvent tirer au cours du jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.stream.Collectors;

public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;

    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());

        this.trips = tryStoreTrips(trips);
        this.text = computeText(trips);
    }

    public Ticket(Station from, Station to, int points) {
        this(List.of((new Trip(from, to, points))));
    }

    /**
     * Retourne le rendu textuel de ce ticket
     *
     * @return le texte de ce ticket
     */
    public String text() {
        return this.text;
    }

    /**
     * Retourne les points que le ticket engendre selon la connexion
     *
     * @param connectivity connectivité
     * @return les points engendrés par le ticket
     */
    public int points(StationConnectivity connectivity) {
        return trips.stream().mapToInt(trip -> trip.points(connectivity)).max().getAsInt();
    }

    /**
     * Effectue et retourne le rendu du texte à afficher sur le ticket en jeu
     *
     * @param trips La liste des trajets devant figurer sur le ticket
     * @return Le texte à rendre pour notre tickets
     */
    private static String computeText(List<Trip> trips) {
        Trip firstTrip = trips.get(0);

        if (trips.size() > 1) {
            return new StringBuilder()
                    .append(String.format("%s - {", firstTrip.from().name()))
                    .append(trips
                            .stream()
                            .map(trip -> String.format("%s (%s)", trip.to().name(), trip.points()))
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(", ")))
                    .append("}")
                    .toString();
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

    /**
     * Fait une comparaison des textes des deux tickets par ordre alphabétique
     *
     * @param that objet Ticket avec lequel effectuer la comparaison
     * @return -1 si this est avant that, 1 si that est avant this, 0 si ils sont égaux
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text());
    }

    /**
     * Retourne le texte de ce ticket
     *
     * @return le texte de ce ticket
     */
    @Override
    public String toString() {
        return this.text;
    }
}
