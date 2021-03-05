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

    public String id() { return this.id; }
    public Station station1() { return this.station1; }
    public Station station2() { return this.station2; }
    public int length() { return this.length; }
    public Level level() { return this.level; }
    public Color color() { return this.color; }

    public List<Station> stations() { return List.of(this.station1, this.station2); }

    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(this.station1) || station.equals(this.station2));
        return station.equals(this.station1) ? this.station2 : this.station1;
    }

    public List<SortedBag<Card>> possibleClaimCards() {
        if (this.level.equals(Level.UNDERGROUND))
            return Route.possibleClaimCardsForUndergroundRoute(this.length, this.color);

        if (this.color == null)
            return Route.possibleClaimCardsForNeutralRoute(this.length);

        return List.of((new SortedBag.Builder<Card>()).add(this.length, Card.of(this.color)).build());
    }

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
