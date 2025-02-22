package org.simpleTools.MainHand;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class WorkbenchListener implements Listener {
    @EventHandler
    public void onPlayerInteractWithCraftingTableInHand(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR || event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();

        ItemStack mainHandItemStack = player.getInventory().getItemInMainHand();

        if (mainHandItemStack.getType() != Material.CRAFTING_TABLE) return;

        player.openWorkbench(null, true);
    }
}
