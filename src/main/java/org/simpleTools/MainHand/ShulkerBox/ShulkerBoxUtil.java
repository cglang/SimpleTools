package org.simpleTools.MainHand.ShulkerBox;

import org.bukkit.Material;

public class ShulkerBoxUtil {
    private static final Material[] SHULKERBOXES = {
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX
    };

    public static boolean IsShulkerBox(Material material) {
        boolean contains = false;
        for (Material box : SHULKERBOXES) {
            if (box == material) {
                contains = true;
                break;
            }
        }
        return contains;
    }
}
