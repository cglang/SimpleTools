package org.simpleTools.MainHand.ShulkerBox;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.simpleTools.SimpleToolsPlugin;

public class ShulkerBoxPlusInventory implements Listener {
    // 玩家
    private final Player player;
    // 真实的潜影盒Stack
    private final ItemStack actualShulkerBoxItemStack;
    /// 虚拟的潜影盒
    private final Inventory virtualShulkerInventory;

    public ShulkerBoxPlusInventory(Player player, ItemStack actualShulkerBoxItemStack) {
        Bukkit.getPluginManager().registerEvents(this, SimpleToolsPlugin.getInstance());
        this.player = player;
        this.actualShulkerBoxItemStack = actualShulkerBoxItemStack;
        this.virtualShulkerInventory = createVirtualInventory(actualShulkerBoxItemStack);
    }

    /// 创建虚拟的潜影盒
    private Inventory createVirtualInventory(ItemStack actualShulkerBoxItemStack) {
        if (actualShulkerBoxItemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta) {
            if (blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                return shulkerBox.getInventory();
            }
        }

        return virtualShulkerInventory;
    }

    /// 让玩家显示虚拟的潜影盒
    public void open() {
        player.openInventory(virtualShulkerInventory);
        // 播放潜影盒打开的声音
        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 0.5f, 1.0f);
    }

    /// 将虚拟潜影盒的物品保存到真实潜影盒中
    private void saveContents() {
        ItemMeta meta = actualShulkerBoxItemStack.getItemMeta();

        if (meta instanceof BlockStateMeta blockStateMeta) {
            if (blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                Inventory shulkerInventory = shulkerBox.getInventory();
                for (int i = 0; i < virtualShulkerInventory.getSize(); i++) {
                    shulkerInventory.setItem(i, virtualShulkerInventory.getItem(i));
                }

                // 更新BlockStateMeta，保存到潜影盒
                blockStateMeta.setBlockState(shulkerBox);
                actualShulkerBoxItemStack.setItemMeta(blockStateMeta);
            }
        }
    }

    /// 关闭潜影盒时
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            saveContents();
            // 播放潜影盒打开的声音
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 0.5f, 1.0f);
            HandlerList.unregisterAll(this);
        }
    }

    /// 潜影盒被玩家丢弃
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onShulkerDrop(PlayerDropItemEvent event) {
        if (!event.getPlayer().equals(player)) return;
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (!ShulkerBoxUtil.IsShulkerBox(droppedItem.getType())) return;

        if (droppedItem.hashCode() == actualShulkerBoxItemStack.hashCode()) {
            event.setCancelled(true);
        }
    }

    /// 潜影盒被玩家移动了位置
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onShulkerMove(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        if (!ShulkerBoxUtil.IsShulkerBox(clickedItem.getType())) return;

        if (event.getCurrentItem().hashCode() == actualShulkerBoxItemStack.hashCode()) {
            event.setCancelled(true);
        }
    }
}
