package org.simpleTools.NetherPortal.World;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.simpleTools.SimpleToolsPlugin;

import java.util.UUID;

public class PlayerWorldPass {
    public ItemStack savePassInfo(ItemStack itemStack, ItemMeta itemMeta, String playerName, UUID playerId) {
        // 创建一个自定义的 NamespacedKey 用于存储创建者信息
        NamespacedKey toKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "to");
        NamespacedKey playerKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "player");

        // 在 PersistentDataContainer 中存储创建者和允许的玩家信息
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(toKey, PersistentDataType.STRING, "world_" + playerId);
        dataContainer.set(playerKey, PersistentDataType.STRING, getPassName(itemStack));

        itemStack.setItemMeta(PlayerWorldUtils.getPassInfo(itemMeta, getPassName(itemStack), playerName));

        return itemStack;
    }

    /**
     * 获取通行证物品名称
     * 通行证为更改过名称的纸张 所以这个方法只能用于纸张 其他物品不保证能用
     */
    private static String getPassName(ItemStack itemStack) {
        if (itemStack.displayName() instanceof TranslatableComponent translationArgument) {
            if (translationArgument.arguments().getFirst().value() instanceof TextComponent textComponent) {
                if (textComponent.children().getFirst() instanceof TextComponent contentComponent)
                    return contentComponent.content();
            }
        }
        return itemStack.getType().name();
    }


    public boolean checkPass(Inventory inventory, String worldName, String currentPlayerName) {
        for (ItemStack item : inventory) {
            if (item != null && item.getType() == Material.PAPER) {
                NamespacedKey creatorKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "to");
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
}
