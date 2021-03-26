package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;


/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class GameStateTest {

    // region Stations
    final Station BAD = new Station(0, "Baden");
    final Station BAL = new Station(1, "Bâle");
    final Station BEL = new Station(2, "Bellinzone");
    final Station BER = new Station(3, "Berne");
    final Station BRI = new Station(4, "Brigue");
    final Station BRU = new Station(5, "Brusio");
    final Station COI = new Station(6, "Coire");
    final Station DAV = new Station(7, "Davos");
    final Station DEL = new Station(8, "Delémont");
    final Station FRI = new Station(9, "Fribourg");
    final Station GEN = new Station(10, "Genève");
    final Station INT = new Station(11, "Interlaken");
    final Station KRE = new Station(12, "Kreuzlingen");
    final Station LAU = new Station(13, "Lausanne");
    final Station LCF = new Station(14, "La Chaux-de-Fonds");
    final Station LOC = new Station(15, "Locarno");
    final Station LUC = new Station(16, "Lucerne");
    final Station LUG = new Station(17, "Lugano");
    final Station MAR = new Station(18, "Martigny");
    final Station NEU = new Station(19, "Neuchâtel");
    final Station OLT = new Station(20, "Olten");
    final Station PFA = new Station(21, "Pfäffikon");
    final Station SAR = new Station(22, "Sargans");
    final Station SCE = new Station(23, "Schaffhouse");
    final Station SCZ = new Station(24, "Schwyz");
    final Station SIO = new Station(25, "Sion");
    final Station SOL = new Station(26, "Soleure");
    final Station STG = new Station(27, "Saint-Gall");
    final Station VAD = new Station(28, "Vaduz");
    final Station WAS = new Station(29, "Wassen");
    final Station WIN = new Station(30, "Winterthour");
    final Station YVE = new Station(31, "Yverdon");
    final Station ZOU = new Station(32, "Zoug");
    final Station ZUR = new Station(33, "Zürich");

    // endregion

    // region Tickets
    final Ticket BAL_BER = new Ticket(BAL, BER, 5);
    final Ticket BAL_BRI = new Ticket(BAL, BRI, 10);
    final Ticket BAL_STG = new Ticket(BAL, STG, 8);
    final Ticket BER_COI = new Ticket(BER, COI, 10);
    final Ticket BER_LUG = new Ticket(BER, LUG, 12);
    final Ticket BER_SCZ = new Ticket(BER, SCZ, 5);
    final Ticket BER_ZUR = new Ticket(BER, ZUR, 6);
    final Ticket FRI_LUC = new Ticket(FRI, LUC, 5);
    final Ticket GEN_BAL = new Ticket(GEN, BAL, 13);
    final Ticket GEN_BER = new Ticket(GEN, BER, 8);
    final Ticket GEN_SIO = new Ticket(GEN, SIO, 10);
    final Ticket GEN_ZUR = new Ticket(GEN, ZUR, 14);
    final Ticket INT_WIN = new Ticket(INT, WIN, 7);
    final Ticket KRE_ZUR = new Ticket(KRE, ZUR, 3);
    final Ticket LAU_INT = new Ticket(LAU, INT, 7);
    final Ticket LAU_LUC = new Ticket(LAU, LUC, 8);
    final Ticket LAU_STG = new Ticket(LAU, STG, 13);
    final Ticket LCF_BER = new Ticket(LCF, BER, 3);
    final Ticket LCF_LUC = new Ticket(LCF, LUC, 7);
    final Ticket LCF_ZUR = new Ticket(LCF, ZUR, 8);
    final Ticket LUC_VAD = new Ticket(LUC, VAD, 6);
    final Ticket LUC_ZUR = new Ticket(LUC, ZUR, 2);
    final Ticket LUG_COI = new Ticket(LUG, COI, 10);
    final Ticket NEU_WIN = new Ticket(NEU, WIN, 9);
    final Ticket OLT_SCE = new Ticket(OLT, SCE, 5);
    final Ticket SCE_MAR = new Ticket(SCE, MAR, 15);
    final Ticket SCE_STG = new Ticket(SCE, STG, 4);
    final Ticket SCE_ZOU = new Ticket(SCE, ZOU, 3);
    final Ticket STG_BRU = new Ticket(STG, BRU, 9);
    final Ticket WIN_SCZ = new Ticket(WIN, SCZ, 3);
    final Ticket ZUR_BAL = new Ticket(ZUR, BAL, 4);
    final Ticket ZUR_BRU = new Ticket(ZUR, BRU, 11);
    final Ticket ZUR_LUG = new Ticket(ZUR, LUG, 9);
    final Ticket ZUR_VAD = new Ticket(ZUR, VAD, 6);

    // endregion

    @Test
    void gameStateInitialWorks() {
        var tickets = SortedBag.of(List.of(BAL_BER, BAL_BRI, BAL_STG, BER_COI, BER_LUG, BER_SCZ, BER_ZUR, FRI_LUC,
                GEN_BAL, GEN_BER, GEN_SIO, GEN_ZUR, INT_WIN, KRE_ZUR, LAU_INT, LAU_LUC, LAU_STG, LCF_BER));

        var rng = new Random();

        var gs = GameState.initial(tickets, rng);

        System.out.println("yolo");
    }
}
