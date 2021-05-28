/**
 * Les tickets que les joueurs peuvent tirer au cours du jeu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Représente un billet de tCHu
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;

    /**
     * Construit un billet constitué de la liste de trajets donnée, ou lève IllegalArgumentException si celle-ci est
     * vide, ou si toutes les gares de départ des trajets n'ont pas le même nom
     *
     * @param trips liste de trajets
     * @throws IllegalArgumentException si celle-ci est vide
     * @throws IllegalArgumentException ou si toutes les gares de départ des trajets n'ont pas le même nom
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());
        List<Trip> tripsCopy = List.copyOf(trips);
        this.trips = tryStoreTrips(tripsCopy);
        this.text = computeText(tripsCopy);
    }

    /**
     * Construit un billet constitué d'un unique trajet
     *
     * @param from   gare de départ du trajet
     * @param to     gare d'arrivée du trajet
     * @param points nombre de points attribués au trajet
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of((new Trip(from, to, points))));
    }

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

        Preconditions.checkArgument(validator);

        return trips;
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
     * Fait une comparaison des textes des deux tickets par ordre alphabétique
     *
     * @param that objet <code>Ticket</code> avec lequel effectuer la comparaison
     * @return <code>-1</code> ssi <code>this</code> est avant <code>that</code>, <code>1</code> ssi <code>that</code>
     * est avant <code>this</code>, <code>0</code> ssi ils sont égaux
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
