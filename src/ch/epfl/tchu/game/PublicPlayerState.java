package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * État du joueur visible aux joueurs de la partie
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;

    private final int carCount;
    private final int claimPoints;

    /**
     * Construit un l'état du joueur qui est visible aux joueurs de la partie
     *
     * @param ticketCount le nombre de tickets du joueurs
     * @param cardCount   le nombre de cartes du joueur
     * @param routes      la liste de routes dont le joueur s'est emparé
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(!(ticketCount < 0 || cardCount < 0));

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        this.carCount = Constants.INITIAL_CAR_COUNT - getUsedCars(routes);
        this.claimPoints = getClaimPoints(routes);
    }

    /**
     * Retourne le nombre de billets que possède le joueur
     *
     * @return le nombre de billets que possède le joueur
     */
    public int ticketCount() {
        return this.ticketCount;
    }

    /**
     * Retourne le nombre de cartes que possède le joueur
     *
     * @return le nombre de cartes que possède le joueur
     */
    public int cardCount() {
        return this.cardCount;
    }

    /**
     * Retourne les routes dont le joueur s'est emparé
     *
     * @return les routes dont le joueur s'est emparé
     */
    public List<Route> routes() {
        return this.routes;
    }

    /**
     * Retourne le nombre de wagons que possède le joueur
     *
     * @return le nombre de wagons que possède le joueur
     */
    public int carCount() {
        return this.carCount;
    }

    /**
     * Retourne le nombre de points de construction obtenus par le joueur
     *
     * @return le nombre de points de construction obtenus par le joueur
     */
    public int claimPoints() {
        return this.claimPoints;
    }

    private static int getUsedCars(List<Route> routes) {
        int usedCars = 0;
        for (Route route : routes) {
            usedCars += route.length();
        }
        return usedCars;
    }

    private static int getClaimPoints(List<Route> routes) {
        int claimPoints = 0;
        for (Route route : routes) {
            claimPoints += route.claimPoints();
        }
        return claimPoints;
    }
}
