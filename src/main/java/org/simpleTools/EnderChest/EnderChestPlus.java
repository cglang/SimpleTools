package org.simpleTools.EnderChest;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.simpleTools.SimpleToolsPlugin;

public class EnderChestPlus implements Listener {

    private final Player player;
    private final ItemStack itemStack;

    private Inventory visualEnderChestInventory;

    public EnderChestPlus(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public void open() {
        // 获取玩家的末影箱
        Inventory enderChestInventory = player.getEnderChest();
        Component component = Component.translatable(itemStack);
        visualEnderChestInventory = createCustomEnderChest(enderChestInventory, component);
        // 打开玩家的末影箱
        player.openInventory(visualEnderChestInventory);
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.5f, 1.0f);
        Bukkit.getPluginManager().registerEvents(this, SimpleToolsPlugin.getInstance());
    }

    // 创建一个自定义的 Ender Chest
    private Inventory createCustomEnderChest(Inventory originalEnderChest, Component title) {
        Inventory customInventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, title);

        for (int i = 0; i < originalEnderChest.getSize(); i++) {
            customInventory.setItem(i, originalEnderChest.getItem(i));
        }

        return customInventory;
    }

    /// 关闭潜影盒时
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        saveContents();
        // 播放潜影盒打开的声音
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 0.5f, 1.0f);
        HandlerList.unregisterAll(this);
    }

    /// 将虚拟潜影盒的物品保存到真实潜影盒中
    private void saveContents() {
        Inventory enderChestInventory = player.getEnderChest();

        for (int i = 0; i < visualEnderChestInventory.getSize(); i++) {
            enderChestInventory.setItem(i, visualEnderChestInventory.getItem(i));
        }
    }
}
