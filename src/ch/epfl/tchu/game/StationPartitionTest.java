package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class StationPartitionTest {

    private static final Station FRI = new Station(9, "Fribourg");
    private static final Station GEN = new Station(10, "Genève");
    private static final Station LAU = new Station(13, "Lausanne");
    private static final Station LCF = new Station(14, "La Chaux-de-Fonds");
    private static final Station LUC = new Station(16, "Lucerne");
    private static final Station LUG = new Station(17, "Lugano");
    private static final Station MAR = new Station(18, "Martigny");
    private static final Station NEU = new Station(19, "Neuchâtel");
    private static final Station YVE = new Station(31, "Yverdon");
    private static final Station ZOU = new Station(32, "Zoug");
    private static final Station ZUR = new Station(33, "Zürich");

    @Test
    void stationPartitionShouldWork() {
        var size = 34;

        var connectivity = new StationPartition.Builder(size)
                .connect(GEN, LAU)
                .connect(LAU, YVE)
                .connect(MAR, GEN)
                .connect(LAU, NEU)
                .connect(LAU, LCF)
                .connect(ZUR, ZOU)
                .connect(ZUR, LUG)
                .build();

        System.out.println(connectivity);

        var t1 = connectivity.connected(GEN, LAU);
        var t2 = connectivity.connected(LAU, YVE);
        var t3 = connectivity.connected(ZUR, LUG);
        var t4 = connectivity.connected(GEN, MAR);
        var f1 = connectivity.connected(LAU, ZUR);
        var f2 = connectivity.connected(LAU, ZOU);
        var f3 = connectivity.connected(LAU, FRI);

        assertTrue(t1);
        assertTrue(t2);
        assertTrue(t3);
        assertTrue(t4);
        assertFalse(f1);
        assertFalse(f2);
        assertFalse(f3);
    }

    @Test
    void stationPartitionBuilderFailsOnNegativeInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            var test = new StationPartition.Builder(-1);
        });
    }


}