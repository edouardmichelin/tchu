package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

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
            return String.format(
                    "%s%s",
                    StringsFr.class.getDeclaredField(cardName).get(StringsFr.class),
                    StringsFr.plural(count)
            );
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

    /**
     * Retourne le message déclarant que le joueur jouera en premier
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, this.playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a gardé le nombre de billets donné
     * @param count le nombre de ticket
     * @return le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, this.playerName, count, StringsFr.plural(count));
    }

    /**
     * Retourne le message déclarant que le joueur peut jouer
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, this.playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré le nombre donné de billets
     * @return le message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, this.playerName, count, StringsFr.plural(count));
    }

    /**
     * Retourne le message déclarant que le joueur a tiré une carte «à l'aveugle», c-à-d du sommet de la pioche
     * @return le message déclarant que le joueur a tiré une carte «à l'aveugle», c-à-d du sommet de la pioche
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, this.playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré la carte disposée face visible donnée
     * @param card la carte concernée
     * @return le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, this.playerName, Info.cardName(card, 1));
    }

    /**
     * Retourne le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     * @param route la route prise
     * @param cards les cartes données
     * @return le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, this.playerName, Info.getRouteName(route), cards);
    }

    /**
     * Retourne le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     * @param route le tunnel en question
     * @param initialCards les cartes servant à prendre possession du tunnel
     * @return le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, this.playerName, route, initialCards);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré les trois cartes additionnelles données, et qu'elles impliquent un coût additionel du nombre de cartes donné
     * @param drawnCards les cartes tirées
     * @param additionalCost le coût additionnel
     * @return le message déclarant que le joueur a tiré les trois cartes additionnelles données, et qu'elles impliquent un coût additionel du nombre de cartes donné
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String additionalCostString = additionalCost == 0 ?
                StringsFr.NO_ADDITIONAL_COST :
                String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));

        return String.format(StringsFr.ADDITIONAL_CARDS_ARE + additionalCostString, drawnCards);
    }

    /**
     * Retourne le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     * @param route la route en question
     * @return le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, this.playerName, Info.getRouteName(route));
    }

    /**
     * Retourne le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons, et que le dernier tour commence donc
     * @param carCount le nombre de wagon
     * @return le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons, et que le dernier tour commence donc
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, this.playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * Retourne le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné, qui est le plus long, ou l'un des plus longs
     * @param longestTrail le chemin en question
     * @return le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné, qui est le plus long, ou l'un des plus longs
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        String formattedLongestTrail = String.format(
                "%s%s%s",
                longestTrail.station1(), StringsFr.EN_DASH_SEPARATOR, longestTrail.station2());

        return String.format(StringsFr.GETS_BONUS, this.playerName, formattedLongestTrail);
    }

    /**
     * Retourne le message déclarant que le joueur remporte la partie avec le nombre de points donnés, son adversaire n'en ayant obtenu que loserPoints.
     * @param points le nombre de points du vainqueur
     * @param loserPoints le nombre de points du perdant
     * @return le message déclarant que le joueur remporte la partie avec le nombre de points donnés, son adversaire n'en ayant obtenu que loserPoints.
     */
    public String won(int points, int loserPoints) {
        return String.format(
                StringsFr.WINS,
                this.playerName,
                points,
                StringsFr.plural(points),
                loserPoints,
                StringsFr.plural(loserPoints));
    }

    private static String getRouteName(Route route) {
        return String.format("%s%s%s", route.station1(), StringsFr.EN_DASH_SEPARATOR, route.station2());
    }
}
