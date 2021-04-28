package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Serializer-deserializer
 *
 * @param <T> type que le serde prend en charge
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
public interface Serde<T> {
    /**
     * Prend en arguments une fonction de sérialisation et une fonction de désérialisation, et retourne le serde
     * correspondant
     *
     * @param serializer   la fonction de sérialisation
     * @param deserializer la fonction de dé-sérialisation
     * @param <E>          le type à (dé-)sérialiser
     * @return un <code>Serde</code> capable de (dé-)sérialiser le type <code>E</code> passé en argument
     */
    static <E> Serde<E> of(Function<E, String> serializer, Function<String, E> deserializer) {
        return new Serde<>() {
            @Override
            public String serialize(E object) {
                if (object instanceof String && ((String) object).length() == 0) return "";
                if (object == null) return "";

                return serializer.apply(object);
            }

            @Override
            public E deserialize(String str) {
                if (str.length() == 0) return null;

                return deserializer.apply(str);
            }
        };
    }

    /**
     * Prend en argument la liste de toutes les valeurs d'un ensemble de valeurs énuméré et retourne le <i>Serde</i>
     * correspondant
     *
     * @param list la liste de toutes les valeurs d'un multiensemble énuméré
     * @param <E>  le type de l'énumération
     * @return le <code>Serde</code> correspondant
     */
    static <E> Serde<E> oneOf(List<E> list) {
        Preconditions.checkArgument(!list.isEmpty());
        return of(
                object -> Integer.toString(list.indexOf(object)),
                str -> list.get(Integer.parseInt(str))
        );
    }

    /**
     * Prend en argument un <i>Serde</i> et un caractère de séparation et retourne un serde capable de (dé-)
     * sérialiser des listes de valeurs (dé-)sérialisées par le <i>Serde</i> donné
     *
     * @param serde     le <code>Serde</code>
     * @param separator le charactère de séparation
     * @param <E>       le type à (dé-)sérialiser
     * @return le <code>Serde</code> correspondant
     */
    static <E> Serde<List<E>> listOf(Serde<E> serde, CharSequence separator) {
        return new Serde<>() {
            @Override
            public String serialize(List<E> object) {
                if (object.isEmpty()) return "";

                return object.stream().map(serde::serialize).collect(Collectors.joining(separator));
            }

            @Override
            public List<E> deserialize(String str) {
                if (str.length() == 0) return List.of();

                return Arrays
                        .stream(str.split(Pattern.quote(separator.toString()), -1))
                        .map(serde::deserialize).collect(Collectors.toList());
            }
        };
    }


    /**
     * Prend en argument un <i>Serde</i> et un caractère de séparation et retourne un serde capable de (dé-)
     * sérialiser des listes triées de valeurs (dé-)sérialisées par le <i>Serde</i> donné
     *
     * @param serde     le <code>Serde</code>
     * @param separator le charactère de séparation
     * @param <E>       le type à (dé-)sérialiser
     * @return le <code>Serde</code> correspondant
     */
    static <E extends Comparable<E>> Serde<SortedBag<E>> bagOf(Serde<E> serde, CharSequence separator) {
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<E> object) {
                if (object.isEmpty()) return "";

                return object.stream().map(serde::serialize).collect(Collectors.joining(separator));
            }

            @Override
            public SortedBag<E> deserialize(String str) {
                if (str.length() == 0) return SortedBag.of();

                return SortedBag.of(Arrays
                        .stream(str.split(Pattern.quote(separator.toString()), -1))
                        .map(serde::deserialize)
                        .collect(Collectors.toList())
                );
            }
        };
    }

    /**
     * Retourne <i>objet</i> transformé en une chaîne de caractères (sérialisation)
     *
     * @param object l'object à sérialiser
     * @return la sérialisation de l'argument <code>object</code>
     */
    String serialize(T object);

    /**
     * Retourne l'argument <i>string</i> transformé en un objet (dé-sérialisation)
     *
     * @param str la chaîne de caractères représentant un objet de type <code>T</code> sérialisé
     * @return l'argument <code>str</code> transformé en un objet (dé-sérialisation)
     */
    T deserialize(String str);
}
