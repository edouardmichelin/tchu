package ch.epfl.tchu.gui;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.RadioMenuItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Title
 *
 * @author Edouard Michelin (314770)
 * @author Julien Jordan (315429)
 */
final class MenuBarViewCreator {

    /**
     * Créé un item de menu ajoutant au <i>menu</i> passé en paramètre une option permettant de passer en mode
     * sombre sur toutes les <i>scenes</i> passées en paramètres
     *
     * @param scenes Les scènes sur lesquelles appliquer le mode sombre
     * @return l'item de menu
     */
    public static RadioMenuItem createDarkModeMenuItemView(Scene... scenes) {
        RadioMenuItem menuItem = new RadioMenuItem("Mode sombre");

        List<ObservableList<String>> scenesStyleSheets = Arrays
                .stream(scenes)
                .map(Scene::getStylesheets)
                .collect(Collectors.toList());

        menuItem.setOnAction(e -> scenesStyleSheets.forEach(MenuBarViewCreator::toggleDarkTheme));

        return menuItem;
    }

    private static void toggleDarkTheme(ObservableList<String> styleSheets) {
        if (styleSheets.contains("dark-theme.css"))
            styleSheets.remove("dark-theme.css");
        else
            styleSheets.add("dark-theme.css");
    }
}
