package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * Représente (une partie de) l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public class PublicCardState {
    // peut-être à rendre finale, je sais pas pour le moment
    private List<Card> faceUpCards;
    private int deckSize;
    private int discardsSize;

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!(deckSize < 0));
        Preconditions.checkArgument(!(discardsSize < 0));


    }

    /**
     * Retourne le nombre total de cartes qui ne sont pas en main des joueurs, à savoir les 5 dont la face est visible, celles de la pioche et celles de la défausse
     * @return le nombre total de cartes qui ne sont pas en main des joueurs, à savoir les 5 dont la face est visible, celles de la pioche et celles de la défausse
     */
    public int totalSize() {
        return 0;
    }

    /**
     * Retourne la carte face visible à l'index donné, ou lève IndexOutOfBoundsException si cet index n'est pas compris entre 0 (inclus) et 5 (exclus)
     * @param slot
     * @return
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, 5);
        return null;
    }

    /**
     * Retourne la taille de la pioche
     * @return la taille de la pioche
     */
    public int deckSize() {
        return 0;
    }

    /**
     * Retourne vrai ssi la pioche est vide
     * @return vrai ssi la pioche est vide
     */
    public boolean isDeckEmpty() {
        return false;
    }

    /**
     * Retourne la taille de la défausse
     * @return la taille de la défausse
     */
    public int discardsSize() {
        return 0;
    }
}
