package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private Color c;

    Card(Color color) {
        this.c = color;
    }

    public final static List<Card> ALL = List.of(Card.values());
    public final static int COUNT = ALL.size();

    public final static List<Card> CARS = Arrays
            .stream(Card.values())
            .filter(card -> card.c != null)
            .collect(Collectors.toUnmodifiableList());

    public static Card of(Color color) {
        return Arrays.stream(Card.values())
                .filter(card -> card.c.equals(color))
                .findFirst()
                .orElse(null);
    }

    public Color color() { return this.c; }
}
