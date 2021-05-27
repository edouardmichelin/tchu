package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * L'état complet d'un joueur
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Créé l'état complet d'un joueur
     *
     * @param tickets les tickets en possession du joueur
     * @param cards   les cartes en main du joueur
     * @param routes  les routes dont le joueur s'est emparé
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Construit l'état initial du joueur en fonction des cartes données, lève IllegalArgumentException
     * si le nombre de cartes donné n'est pas égal à 4
     *
     * @param initialCards Les cartes initiale du joueur
     * @return l'état initial du joueur
     * @throws IllegalArgumentException si le nombre de cartes donné n'est pas égal à 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Retourne les billets du joueur,
     *
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return this.tickets;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés
     *
     * @param newTickets Les tickets à ajouter à l'état du joueur
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag<Ticket> union = this.tickets.union(newTickets);
        return new PlayerState(union, this.cards, this.routes());
    }

    /**
     * Retourne les cartes wagon/locomotive du joueur
     *
     * @return les cartes wagon/locomotive du joueur
     */
    public SortedBag<Card> cards() {
        return this.cards;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     *
     * @param card la carte à ajouter
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     */
    public PlayerState withAddedCard(Card card) {
        return withAddedCards(SortedBag.of(card));
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     *
     * @param additionalCards l'ensemble de cartes à ajouter
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        SortedBag<Card> union = this.cards.union(additionalCards);
        return new PlayerState(this.tickets, union, this.routes());
    }

    /**
     * Retourne vrai ssi le joueur peut s'emparer de la route donnée
     *
     * @param route la route dont on veut savoir si le joueur peut s'en emparer
     * @return retourne vrai ssi le joueur peut s'emparer de la route donnée
     */
    public boolean canClaimRoute(Route route) {
        return this.carCount() >= route.length() && !this.possibleClaimCards(route).isEmpty();
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour prendre possession de la
     * route donnée, ou lève IllegalArgumentException si le joueur n'a pas assez de wagons pour s'emparer de la route
     *
     * @param route Route concernée du contexte
     * @return retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour prendre
     * possession de la route donnée
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());
        List<SortedBag<Card>> playerPossibilities = new ArrayList<>();

        for (SortedBag<Card> possibility : route.possibleClaimCards()) {
            if (this.cards().contains(possibility)) playerPossibilities.add(possibility);
        }
        return playerPossibilities;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel,
     * trié par ordre croissant du nombre de cartes locomotives,
     *
     * @param additionalCardsCount le nombre de cartes additionnel à défausser pour s'emparer du tunnel
     * @param initialCards         les cartes initialement posées pour s'eparer du tunnel
     * @param drawnCards           la pioche de 3 cartes qui définissent le nombre de cartes additionnelles à défausser
     * @return la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un
     * tunnel, trié par ordre croissant du nombre de cartes locomotives,
     */
    public List<SortedBag<Card>> possibleAdditionalCards(
            int additionalCardsCount,
            SortedBag<Card> initialCards,
            SortedBag<Card> drawnCards
    ) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty());
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        Card initialType = Card.LOCOMOTIVE;
        for (Card card : initialCards) {
            if (!card.equals(Card.LOCOMOTIVE)) {
                initialType = card;
                break;
            }
        }

        Card caughtType = initialType;

        SortedBag<Card> usableCards = SortedBag.of(this.cards().difference(initialCards).stream().filter(
                card -> card == caughtType || card == Card.LOCOMOTIVE).collect(Collectors.toList()
        ));

        List<SortedBag<Card>> usableCardsSet;
        if (usableCards.size() >= additionalCardsCount) {
            usableCardsSet = new ArrayList<>(usableCards.subsetsOfSize(additionalCardsCount));
            usableCardsSet.sort(Comparator.comparingInt(hand -> hand.countOf(Card.LOCOMOTIVE)));
        } else {
            usableCardsSet = new ArrayList<>();
        }
        return usableCardsSet;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au
     * moyen des cartes données
     *
     * @param route      route dont le joueur s'est emparé
     * @param claimCards cartes défaussées pour s'emparer de la route
     * @return un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au
     * moyen des cartes données
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routes = new ArrayList<>(this.routes());
        routes.add(route);
        return new PlayerState(this.tickets, this.cards().difference(claimCards), routes);
    }

    /**
     * Retourne le nombre de points — éventuellement négatif — obtenus par le joueur grâce à ses billets
     *
     * @return le nombre de points — éventuellement négatif — obtenus par le joueur grâce à ses billets
     */
    public int ticketPoints() {
        int maxId = 0;
        for (Route route : this.routes()) {
            if (route.station1().id() > maxId || route.station2().id() > maxId) {
                if (route.station2().id() > route.station1().id()) {
                    maxId = route.station2().id();
                } else {
                    maxId = route.station1().id();
                }
            }
        }

        StationPartition.Builder spb = new StationPartition.Builder(maxId + 1);
        for (Route route : this.routes()) {
            spb.connect(route.station1(), route.station2());
        }
        StationPartition stationPartition = spb.build();

        int netPoints = 0;
        for (Ticket ticket : this.tickets) {
            netPoints += ticket.points(stationPartition);
        }

        return netPoints;
    }

    /**
     * Retourne la totalité des points obtenus par le joueur à la fin de la partie
     *
     * @return retourne la totalité des points obtenus par le joueur à la fin de la partie
     */
    public int finalPoints() {
        return this.ticketPoints() + this.claimPoints();
    }
}
