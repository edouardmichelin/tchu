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
    /**
     * Représente les deux niveaux auxquels une route peut se trouver
     */
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
     * Construit une route avec l'identité, les gares, la longueur, le niveau et la couleur donnés ; lève
     * IllegalArgumentException si les deux gares sont égales (au sens de la méthode equals) ou si la longueur n'est
     * pas comprise dans les limites acceptables (fournies par l'interface Constants), ou NullPointerException si
     * l'identité, l'une des deux gares ou le niveau sont nuls. Notez bien que la couleur peut par contre être nulle,
     * ce qui signifie que la route est de couleur neutre.
     *
     * @param id       identifiant unique de la route
     * @param station1 station de départ de la route
     * @param station2 station d'arrivée de la route
     * @param length   taille de la route
     * @param level    niveau sur lequel se situe la route (route ou tunnel)
     * @param color    couleur de la route
     * @throws IllegalArgumentException si les deux gares sont égales (au sens de la méthode equals)
     * @throws NullPointerException     si l'identité, l'une des deux gares ou le niveau sont nuls
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(station1.id() != station2.id());
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
     *
     * @return l'identifiant de la route
     */
    public String id() {
        return this.id;
    }

    /**
     * Retourne la station de départ du chemin
     *
     * @return la station de départ du chemin
     */
    public Station station1() {
        return this.station1;
    }

    /**
     * Retourne la station d'arrivée du chemin
     *
     * @return la station d'arrivée du chemin
     */
    public Station station2() {
        return this.station2;
    }

    /**
     * Retourne la taille de la route
     *
     * @return la taille de la route
     */
    public int length() {
        return this.length;
    }

    /**
     * Retourne le niveau de la route
     *
     * @return le niveau de la route
     */
    public Level level() {
        return this.level;
    }

    /**
     * Retourne la couleur de la route
     *
     * @return la couleur de la route
     */
    public Color color() {
        return this.color;
    }

    /**
     * Retourne une liste contenant la station de départ et la station d'arrivée
     *
     * @return une liste contenant la station de départ et la station d'arrivée
     */
    public List<Station> stations() {
        return List.of(this.station1, this.station2);
    }

    /**
     * Retourne la gare de la route qui n'est pas celle donnée, ou lève IllegalArgumentException si la gare donnée
     * n'est ni la première ni la seconde gare de la route
     *
     * @param station station de référence
     * @return la gare de la route qui n'est pas celle donnée
     * @throws IllegalArgumentException si la gare donnée n'est ni la première ni la seconde gare de la route
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(this.station1) || station.equals(this.station2));
        return station.equals(this.station1) ? this.station2 : this.station1;
    }

    /**
     * Retourne les possibilités de mains de carte pour réclamer la route
     *
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
     * Retourne le nombre de cartes additionnelles à jouer pour s'emparer de la route (en tunnel), sachant que le
     * joueur a initialement posé les cartes claimCards et que les trois cartes tirées du sommet de la pioche sont
     * drawnCards ; lève l'exception IllegalArgumentException si la route à laquelle on l'applique n'est pas un
     * tunnel, ou si drawnCards ne contient pas exactement 3 cartes
     *
     * @param claimCards main du joueur pour construire le tunnel
     * @param drawnCards pioche de la pile
     * @return le nombre de cartes additionnelles à jouer pour s'emparer de la route (en tunnel), sachant que le
     * joueur a initialement posé les cartes claimCards et que les trois cartes tirées du sommet de la pioche sont
     * drawnCards
     * @throws IllegalArgumentException si la route à laquelle on l'applique n'est pas un tunnel
     * @throws IllegalArgumentException si drawnCards ne contient pas exactement 3 cartes
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
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
     *
     * @return le nombre de points gagné à la construction de la route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(this.length);
    }

    private static List<SortedBag<Card>> possibleClaimCardsForUndergroundRoute(int length, Color color) {
        List<SortedBag<Card>> result = new ArrayList<>();
        if (color == null) {
            for (int locomotiveAmount = 0; locomotiveAmount < length; locomotiveAmount++)
                result.addAll(Route.possibleClaimCardsForNeutralRoute(length, locomotiveAmount));
        } else {
            for (int locAmount = 0; locAmount < length; locAmount++)
                result.add((new SortedBag.Builder<Card>())
                        .add(locAmount, Card.LOCOMOTIVE)
                        .add(length - locAmount, Card.of(color))
                        .build());
        }

        result.add(SortedBag.of(length, Card.LOCOMOTIVE));

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
