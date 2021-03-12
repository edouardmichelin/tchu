package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;
import java.util.StringJoiner;

/**
 * Permet de générer les textes décrivant le déroulement de la partie
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Info {
    private final String playerName;

    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Retourne le nom (français) de la carte donnée, au singulier ssi la valeur absolue du second argument vaut 1
     * @param card la carte
     * @param count le nombre d'occurence de la carte
     * @return le nom (français) de la carte donnée, au singulier ssi la valeur absolue du second argument vaut 1
     */
    public static String cardName(Card card, int count) {
        String cardName = String.format("%s_CARD", card.color().toString());
        try {
            return String.format("%s%s", (String) StringsFr.class.getDeclaredField(cardName).get(StringsFr.class), StringsFr.plural(count));
        } catch (Exception ignored) { }

        return "";
    }

    /**
     * Retourne le message déclarant que les joueurs, dont les noms sont ceux donnés, ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     * @param playerNames names of the player
     * @param points amount of points
     * @return le message déclarant que les joueurs, dont les noms sont ceux donnés, ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     */
    public static String draw(List<String> playerNames, int points) {
        int lastIndex = playerNames.size() - 1;
        String commas = String.join(", ", playerNames.subList(0, lastIndex));

        return String.format(StringsFr.DRAW, String.format("%s et %s", commas, playerNames.get(lastIndex)), points);
    }

    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, this.playerName);
    }

    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, this.playerName, count, StringsFr.plural(count));
    }

    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, this.playerName);
    }

    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, this.playerName, count, StringsFr.plural(count));
    }

    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, this.playerName);
    }

    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, this.playerName, Info.cardName(card, 1));
    }

    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, this.playerName, route, cards);
    }

    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, this.playerName, route, initialCards);
    }

    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String additionalCostString = additionalCost == 0 ?
                StringsFr.NO_ADDITIONAL_COST :
                String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));

        return String.format(StringsFr.ADDITIONAL_CARDS_ARE + additionalCostString, drawnCards);
    }

    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, this.playerName, route);
    }

    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, this.playerName, carCount, StringsFr.plural(carCount));
    }

    public String getsLongestTrailBonus(Trail longestTrail) {
        String formattedLongestTrail = String.format(
                "%s%s%s",
                longestTrail.station1(), StringsFr.EN_DASH_SEPARATOR, longestTrail.station2());

        return String.format(StringsFr.GETS_BONUS, this.playerName, formattedLongestTrail);
    }

    public String won(int points, int loserPoints) {
        return String.format(
                StringsFr.WINS,
                this.playerName,
                points,
                StringsFr.plural(points),
                loserPoints,
                StringsFr.plural(loserPoints));
    }
}
