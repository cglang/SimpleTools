package org.simpleTools.NetherPortalTeleport;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.simpleTools.SimpleToolsPlugin;
import org.simpleTools.Utils.ComponentUtils;

import java.util.UUID;

public class PlayerWorldUtils {
    public static long uuidToSeed(UUID playerId) {
        // 将UUID转化为一个long类型的种子
        long mostSigBits = playerId.getMostSignificantBits();
        long leastSigBits = playerId.getLeastSignificantBits();

        return mostSigBits ^ leastSigBits;  // 使用异或操作生成种子
    }

    public static boolean checkPass(Inventory inventory, String worldName, String currentPlayerName) {
        for (ItemStack item : inventory) {
            if (item != null && item.getType() == Material.PAPER) {
                NamespacedKey creatorKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "creator");
                NamespacedKey playerKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "player");
                PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
                if (dataContainer.has(creatorKey, PersistentDataType.STRING) &&
                        dataContainer.has(playerKey, PersistentDataType.STRING)) {
                    String creatorName = dataContainer.get(creatorKey, PersistentDataType.STRING);
                    String playerName = dataContainer.get(playerKey, PersistentDataType.STRING);
                    if (worldName.equals(creatorName) && currentPlayerName.equals(playerName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ItemMeta getPassInfo(ItemMeta itemMeta, String passName, String playerName) {
        var text = "[" + passName + "]的[" + playerName + "]世界通行证";
        itemMeta.lore(ComponentUtils.getComponents(text, NamedTextColor.GREEN));
        itemMeta.displayName(ComponentUtils.getComponent("通行证", NamedTextColor.LIGHT_PURPLE));
        return itemMeta;
    }

}
