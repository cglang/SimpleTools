package org.simpleTools.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComponentUtils {
    public static @NotNull Component getComponent(String text, NamedTextColor color) {
        return Component.newline()
                .content(text)
                .color(color);
    }

    public static List<Component> getComponents(String text, NamedTextColor color) {
        List<Component> lore = new ArrayList<>();
        var playerNameComponent = Component.newline()
                .content(text)
                .color(color)
                .decoration(TextDecoration.ITALIC, false);
        lore.add(playerNameComponent);

        return lore;
    }
}
