package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Les routes
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Route {
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Construit une route
     * @param id identifiant unique de la route
     * @param station1 station de départ de la route
     * @param station2 station d'arrivée de la route
     * @param length taille de la route
     * @param level niveau sur lequel se situe la route (route ou tunnel)
     * @param color couleur de la route
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);
        Preconditions.checkArgument(!id.isBlank());

        this.id = id;
        this.station1 = station1;
        this.station2 = Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.color = color;
        this.length = length;
    }

    /**
     * Retourne l'identifiant de la route
     * @return l'identifiant de la route
     */
    public String id() { return this.id; }

    /**
     * Retourne la station de départ du chemin
     * @return la station de départ du chemin
     */
    public Station station1() { return this.station1; }

    /**
     * Retourne la station d'arrivée du chemin
     * @return la station d'arrivée du chemin
     */
    public Station station2() { return this.station2; }

    /**
     * Retourne la taille de la route
     * @return
     */
    public int length() { return this.length; }

    /**
     * Retourne le niveau de la route
     * @return le niveau de la route
     */
    public Level level() { return this.level; }

    /**
     * Retourne la couleur de la route
     * @return la couleur de la route
     */
    public Color color() { return this.color; }

    /**
     * Retourne une liste contenant la station de départ et la station d'arrivée
     * @return une liste contenant la station de départ et la station d'arrivée
     */
    public List<Station> stations() { return List.of(this.station1, this.station2); }

    /**
     * Retourne la station opposée à la station de référence passée en paramètre
     * @param station station de référence
     * @return la station opposée à la station de référence passée en paramètre
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(this.station1) || station.equals(this.station2));
        return station.equals(this.station1) ? this.station2 : this.station1;
    }

    /**
     * Retourne les possibilités de mains de carte pour réclamer la route
     * @return les possibilités de mains de carte pour réclamer la route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        if (this.level.equals(Level.UNDERGROUND))
            return Route.possibleClaimCardsForUndergroundRoute(this.length, this.color);

        if (this.color == null)
            return Route.possibleClaimCardsForNeutralRoute(this.length);

        return List.of((new SortedBag.Builder<Card>()).add(this.length, Card.of(this.color)).build());
    }

    /**
     * Retourne le nombre de carte supplémentaire à payer pour construire le tunnel
     * @param claimCards main du joueur pour construire le tunnel
     * @param drawnCards pioche de la pile
     * @return le nombre de carte supplémentaire à payer pour construire le tunnel
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards,SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(this.level.equals(Level.UNDERGROUND));
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        int count = 0;

        for (Card drawnCard : drawnCards)
            if (drawnCard.equals(Card.LOCOMOTIVE))
                count++;
            else
                for (Card claimCard : claimCards)
                    if (drawnCard.equals(claimCard)) {
                        count++;
                        break;
                    }

        return count;
    }

    /**
     * Retourne le nombre de points gagné à la construction de la route
     * @return le nombre de points gagné à la construction de la route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(this.length);
    }

    private static List<SortedBag<Card>> possibleClaimCardsForUndergroundRoute(int length, Color color) {
        List<SortedBag<Card>> result = new ArrayList<>();
        if (color == null) {
            for (int locAmount = 0; locAmount < length; locAmount++)
                result.addAll(Route.possibleClaimCardsForNeutralRoute(length, locAmount));
        } else {
            for (int locAmount = 0; locAmount < length; locAmount++)
                result.add((new SortedBag.Builder<Card>())
                        .add(locAmount, Card.LOCOMOTIVE)
                        .add(length - locAmount, Card.of(color))
                        .build());
        }

        result.add(SortedBag.of(3, Card.LOCOMOTIVE));

        return result;
    }

    private static List<SortedBag<Card>> possibleClaimCardsForNeutralRoute(int length) {
        return Route.possibleClaimCardsForNeutralRoute(length, 0);
    }

    private static List<SortedBag<Card>> possibleClaimCardsForNeutralRoute(int length, int locomotiveAmount) {
        return Card
                .CARS
                .stream()
                .map(card -> new SortedBag.Builder<Card>()
                        .add(locomotiveAmount, Card.LOCOMOTIVE)
                        .add(length - locomotiveAmount, card)
                        .build())
                .collect(Collectors.toList());
    }
}
