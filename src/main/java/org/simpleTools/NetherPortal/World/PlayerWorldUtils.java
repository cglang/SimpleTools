package org.simpleTools.NetherPortal.World;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.simpleTools.Utils.ComponentUtils;

import java.util.UUID;

public class PlayerWorldUtils {
    public static long uuidToSeed(UUID playerId) {
        // 将UUID转化为一个long类型的种子
        long mostSigBits = playerId.getMostSignificantBits();
        long leastSigBits = playerId.getLeastSignificantBits();

        return mostSigBits ^ leastSigBits;  // 使用异或操作生成种子
    }



    public static ItemMeta getPassInfo(ItemMeta itemMeta, String passName, String playerName) {
        var text = "[" + passName + "]的[" + playerName + "]世界通行证";
        itemMeta.lore(ComponentUtils.getComponents(text, NamedTextColor.GREEN));
        itemMeta.displayName(ComponentUtils.getComponent("通行证", NamedTextColor.LIGHT_PURPLE));
        return itemMeta;
    }

}
