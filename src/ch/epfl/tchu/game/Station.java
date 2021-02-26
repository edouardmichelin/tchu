package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

public final class Station {
    private static List<Integer> ids = new ArrayList<>();

    private final int id;
    private final String name;

    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        // Preconditions.checkArgument(!ids.contains(id));

        ids.add(id);

        this.id = id;
        this.name = name;
    }

    public int id() { return this.id; }
    public String name() { return this.name; }

    @Override
    public String toString() { return this.name; }
}
