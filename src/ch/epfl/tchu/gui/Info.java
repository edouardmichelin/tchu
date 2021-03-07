package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;

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
        return "";
    }

    /**
     * Retourne le message déclarant que les joueurs, dont les noms sont ceux donnés, ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     * @param playerNames
     * @param points
     * @return le message déclarant que les joueurs, dont les noms sont ceux donnés, ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     */
    public static String draw(List<String> playerNames, int points) {
        return "";
    }


    // TOUTES LES METHODES D'INSTANCES SUIVENT MAIS J'AI LA FLEMME D'ECRIRE LES EN-TETES
}
