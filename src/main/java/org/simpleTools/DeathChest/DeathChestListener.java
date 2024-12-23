package org.simpleTools.DeathChest;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;

public class DeathChestListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        // 生成第一个箱子
        Block block1 = deathLocation.getBlock();
        block1.setType(Material.CHEST);
        Chest chest1 = (Chest) block1.getState();

        if (event.getDrops().size() > 27) {
            Block block2 = deathLocation.clone().add(1, 0, 0).getBlock();
            block2.setType(Material.CHEST);
            Chest chest2 = (Chest) block2.getState();

            org.bukkit.block.data.type.Chest chestData1 = (org.bukkit.block.data.type.Chest) chest1.getBlockData();
            org.bukkit.block.data.type.Chest chestData2 = (org.bukkit.block.data.type.Chest) chest2.getBlockData();
            chestData1.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
            chestData2.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
            chest1.setBlockData(chestData1);
            chest2.setBlockData(chestData2);
            chest1.update();
            chest2.update();
        }

        Inventory chestInventory = chest1.getInventory();
        // 将玩家死亡时的物品转移到大箱子中
        chestInventory.addItem(event.getDrops().toArray(new org.bukkit.inventory.ItemStack[0]));
        event.getDrops().clear();

        // 发送死亡坐标到死亡玩家的聊天框
        String deathMessage = "你死在了: X: %d Y: %d Z: %d".formatted(deathLocation.getBlockX(), deathLocation.getBlockY(), deathLocation.getBlockZ());
        player.sendMessage(deathMessage);
    }
}
