package org.simpleTools.ChainDestruction;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.simpleTools.SimpleToolsPlugin;
import org.simpleTools.Utils.ComponentUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ChopperListener implements Listener {
    private final Set<Block> pluginBrokenBlocks = new HashSet<>();

    public ChopperListener() {
        createEnchantmentRecipe();
    }

    // 铁砧更改触发
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        Inventory anvilInventory = event.getInventory();
        // 获取第一格和第二格的物品
        ItemStack firstItem = anvilInventory.getItem(0); // 第一格物品
        if (firstItem == null || !isAxe(firstItem.getType()))
            return;

        ItemStack secondItem = anvilInventory.getItem(1); // 第二格物品
        if (secondItem == null || secondItem.getType() != Material.ENCHANTED_BOOK
                || !isChainMiningEnchantment(secondItem))
            return;

        var result = firstItem.clone();
        setChainMiningEnchantment(result);
        event.setResult(result);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block startBlock = event.getBlock();
        if (pluginBrokenBlocks.contains(startBlock)) {
            pluginBrokenBlocks.remove(startBlock);
            return;
        }

        var player = event.getPlayer();
        var blockType = startBlock.getType();
        var mainHandItem = player.getInventory().getItemInMainHand();

        if (isLog(blockType) && isAxe(mainHandItem.getType())
                && player.isSneaking() && isChainMiningEnchantment(mainHandItem)) {
            event.setCancelled(true);

            var blocks = findNaturallyBlocks(startBlock);
            if (player.getGameMode() == GameMode.CREATIVE) {
                for (var block : blocks) {
                    block.setType(Material.AIR);
                }
            } else {
                for (var block : blocks) {
                    block.breakNaturally(true);
                }
                var leave = mainHandItem.getEnchantmentLevel(Enchantment.UNBREAKING);
                var count = (1 - (0.6 + (0.4 / (leave + 1)))) * blocks.size();
                setDamage((int) count, player, mainHandItem);
            }
        }
    }

    // 是否是木头
    private boolean isLog(Material material) {
        return material.name().endsWith("_LOG");
    }

    // 是否是树叶
    private boolean isLeaves(Material material) {
        return material.name().endsWith("_LEAVES");
    }

    // 是否是斧子
    private boolean isAxe(Material material) {
        return switch (material) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE -> true;
            default -> false;
        };
    }

    // 是否有连锁采集附魔
    private boolean isChainMiningEnchantment(ItemStack stack) {
        PersistentDataContainer dataContainer = stack.getItemMeta().getPersistentDataContainer();
        NamespacedKey enchantmentKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "chain_mining_enchantment");
        return dataContainer.has(enchantmentKey);
    }

    /**
     * 扣除耐久
     */
    private void setDamage(int count, Player player, ItemStack axe) {
        Damageable meta = (Damageable) axe.getItemMeta();
        meta.setDamage(meta.getDamage() + count);
        if (meta.getDamage() >= axe.getType().getMaxDurability()) {
            player.getInventory().setItemInMainHand(null);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        } else {
            axe.setItemMeta(meta);
        }
    }

    /**
     * 想寻找要破坏的方块
     */
    private Set<Block> findNaturallyBlocks(Block startBlock) {
        Material logType = startBlock.getType();

        int count = 0;
        Queue<Block> queue = new LinkedList<>();
        queue.add(startBlock);

        Set<Block> visited = new HashSet<>();
        visited.add(startBlock);


        int logFindDeepin = 1;  // 木头搜索深度
        int leaveFindDeepin = 2; // 树叶搜索深度
        while (!queue.isEmpty() && count < 500) {
            Block current = queue.poll();
            count++;

            for (int dx = -logFindDeepin; dx <= logFindDeepin; dx++) {
                for (int dy = -logFindDeepin; dy <= logFindDeepin; dy++) {
                    for (int dz = -logFindDeepin; dz <= logFindDeepin; dz++) {
                        Block relative = current.getRelative(dx, dy, dz);
                        if (!visited.contains(relative) && relative.getType() == logType) {
                            visited.add(relative);
                            queue.add(relative);
                        }

                        if (!visited.contains(relative) && isLeaves(relative.getType())) {
                            for (int ex = -leaveFindDeepin; ex <= leaveFindDeepin; ex++) {
                                for (int ey = -leaveFindDeepin; ey <= leaveFindDeepin; ey++) {
                                    for (int ez = -leaveFindDeepin; ez <= leaveFindDeepin; ez++) {
                                        Block ls_relative = current.getRelative(ex, ey, ez);
                                        if (!visited.contains(ls_relative) && ls_relative.getType() == logType) {
                                            visited.add(ls_relative);
                                            queue.add(ls_relative);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return visited;
    }


    /**
     * 创建连锁采集附魔书合成配方
     */
    private void createEnchantmentRecipe() {
        // 创建附魔书物品
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);

        setChainMiningEnchantment(enchantedBook);

        // 创建合成配方
        NamespacedKey recipeKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "chain_mining_recipe");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, enchantedBook);
        recipe.shape("AAA", "AAA", "AAA");
        recipe.setIngredient('A', Material.DIAMOND_AXE);

        Bukkit.addRecipe(recipe);
    }

    private void setChainMiningEnchantment(ItemStack stack) {
        var meta = stack.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey enchantmentKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "chain_mining_enchantment");
        dataContainer.set(enchantmentKey, PersistentDataType.BOOLEAN, true);

        var lores = meta.lore();
        if (lores == null) {
            meta.lore(ComponentUtils.getComponents("连锁采集", NamedTextColor.GRAY));
        } else {
            lores.add(ComponentUtils.getComponent("连锁采集", NamedTextColor.GRAY));
        }

        meta.addEnchant(Enchantment.UNBREAKING, 3, true);

        stack.setItemMeta(meta);
    }
}
