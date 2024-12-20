package org.simpleTools.ShulkerBox;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ShulkerBoxListener implements Listener {

    /// 玩家触发事件执行
    @EventHandler
    public void onPlayerInteractWithShulkerBoxInHand(PlayerInteractEvent event) {
        // 是否右击空气
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        // 获取当前玩家
        Player player = event.getPlayer();

        // 判断是否是主手
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        // 手中的物品
        ItemStack actualShulkerBoxItemStack = player.getInventory().getItemInMainHand();

        // Material枚举中不同颜色的潜影盒有不同的值 所以用此方法
        if (!actualShulkerBoxItemStack.getType().name().endsWith("SHULKER_BOX"))
            return;

        ShulkerBoxPlusInventory.open(player, actualShulkerBoxItemStack);
    }
}
