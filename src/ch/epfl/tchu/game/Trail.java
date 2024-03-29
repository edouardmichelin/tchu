package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.StringsFr;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Représente un chemin
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public final class Trail {
    private final int length;
    private final List<Route> routes;

    private Trail(List<Route> routes, int length) {
        this.routes = routes;
        this.length = length;
    }

    private Trail(List<Route> routes) {
        this(routes, routes == null ? 0 : routes.stream().mapToInt(Route::length).sum());
    }

    /**
     * Retourne le chemin le plus long que le joueur possède
     *
     * @param routes la liste de routes du joueur
     * @return le chemin le plus long que le joueur possède
     */
    public static Trail longest(List<Route> routes) {
        Trail currentLongestTrail;
        final Trail[] finalCurrentLongestTrail = {new Trail(null)};

        List<Trail> singleRouteTrails = routes
                .stream()
                .flatMap(route -> {
                    Trail trail = new Trail(List.of(route));
                    if (trail.length > finalCurrentLongestTrail[0].length)
                        finalCurrentLongestTrail[0] = trail;

                    return Stream.of(new Trail(List.of(route)), new Trail(List.of(Trail.reverseRoute(route))));
                })
                .collect(Collectors.toList());

        currentLongestTrail = finalCurrentLongestTrail[0];

        List<Trail> trails = new ArrayList<>(singleRouteTrails);

        while (!trails.isEmpty()) {
            List<Trail> nextTrails = new ArrayList<>();

            for (Trail trail : trails) {
                List<Route> connections = Trail.findAllRouteConnections(singleRouteTrails, trail);

                for (Route connection : connections) {
                    Trail candidate = trail.copyAndExtend(connection);
                    nextTrails.add(candidate);

                    if (candidate.length > currentLongestTrail.length)
                        currentLongestTrail = candidate;
                }
            }

            trails = nextTrails;
        }

        return currentLongestTrail;
    }

    private static Route reverseRoute(Route route) {
        return new Route(
                route.id(),
                route.station2(),
                route.station1(),
                route.length(),
                route.level(),
                route.color()
        );
    }

    private static List<Route> findAllRouteConnections(List<Trail> trails, Trail currentTrail) {
        List<Trail> filteredTrails = new ArrayList<>(trails);
        List<Route> result = new ArrayList<>();

        filteredTrails.forEach(trail -> {
            if (
                    currentTrail
                            .routes
                            .stream()
                            .map(Route::id)
                            .anyMatch(
                                    id -> trail
                                            .routes
                                            .stream()
                                            .map(Route::id)
                                            .anyMatch(id::equals))
            ) return;

            if (currentTrail.station2().id() == trail.station1().id())
                result.addAll(trail.routes);
        });

        return result;
    }

    /**
     * Retourne la première station du chemin
     *
     * @return la première station du chemin ssi la longueur est plus grande que 0, <code>null</code> sinon
     */
    public Station station1() {
        return this.length > 0 ? this.routes.get(0).station1() : null;
    }

    /**
     * Retourne la dernière station du chemin
     *
     * @return la dernière station du chemin ssi la longueur est plus grande que 0, <code>null</code> sinon
     */
    public Station station2() {
        return this.length > 0 ? this.routes.get(this.routes.size() - 1).station2() : null;
    }

    /**
     * Retourne la longueur totale des routes du chemin
     *
     * @return la longueur totale des routes du chemin
     */
    public int length() {
        return this.length;
    }

    /**
     * Retourne le nom des stations se trouvant le long du chemin, ainsi que la longueur totale du chemin, le tout
     * formatté avec style
     *
     * @return le nom des stations se trouvant le long du chemin, ainsi que la longueur totale du chemin, le tout
     * formatté avec style
     */
    @Override
    public String toString() {
        if (this.length <= 0) return "";

        var sj = new StringJoiner(
                StringsFr.EN_DASH_SEPARATOR,
                this.station1() + StringsFr.EN_DASH_SEPARATOR,
                String.format(" (%d)", this.length)
        );

        this.routes.forEach(route -> sj.add(route.station2().name()));

        return sj.toString();
    }

    private Trail copyAndExtend(Route route) {
        List<Route> newRoutes = new ArrayList<>(this.routes);
        newRoutes.add(route);

        return new Trail(newRoutes, this.length + route.length());
    }
}
