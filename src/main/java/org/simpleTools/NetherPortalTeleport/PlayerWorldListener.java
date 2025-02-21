package org.simpleTools.NetherPortalTeleport;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.simpleTools.ConfigUtils;
import org.simpleTools.SimpleToolsPlugin;

public class PlayerWorldListener implements Listener {
    private static final String playerWorldsFileName = "playerWorlds.yml";
    private final FileConfiguration playerWorldConfig;

    public PlayerWorldListener() {
        playerWorldConfig = ConfigUtils.initConfig(playerWorldsFileName);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        var loc = loadPlayerWorld(player);

        if (loc != null)
            player.teleport(loc);
    }

    private @Nullable Location loadPlayerWorld(Player player) {
        PlayerWorld pw = new PlayerWorld();
        World world = pw.getPlayerWorld(player.getUniqueId());

        var playerQuitWorldName = playerWorldConfig.getString(player.getUniqueId() + ".world");
        if (playerQuitWorldName == null) return null;

        var playerQuitWorld = Bukkit.getWorld(playerQuitWorldName);
        if (playerQuitWorld == null) return null;

        if (playerQuitWorld.getName().equals(world.getName())) {
            double x = playerWorldConfig.getDouble(player.getUniqueId() + ".x");
            double y = playerWorldConfig.getDouble(player.getUniqueId() + ".y");
            double z = playerWorldConfig.getDouble(player.getUniqueId() + ".z");

            return new Location(world, x, y, z);
        }

        return null;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerWorld(player);
    }

    // 保存玩家的世界和坐标信息
    private void savePlayerWorld(Player player) {
        String worldName = player.getWorld().getName();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();

        // 保存世界信息和坐标
        playerWorldConfig.set(player.getUniqueId() + ".world", worldName);
        playerWorldConfig.set(player.getUniqueId() + ".x", x);
        playerWorldConfig.set(player.getUniqueId() + ".y", y);
        playerWorldConfig.set(player.getUniqueId() + ".z", z);

        ConfigUtils.savePlayerWorlds(playerWorldConfig, playerWorldsFileName);
    }

    // 铁砧命名时触发 对纸进行命名时进行签名
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getViewers().getFirst() instanceof Player player)) return;

        ItemStack result = event.getResult(); // 获取铁砧的输出物品
        if (result == null || result.getType() != Material.PAPER) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        // 创建一个自定义的 NamespacedKey 用于存储创建者信息
        NamespacedKey key = new NamespacedKey(SimpleToolsPlugin.getInstance(), "creator");
        NamespacedKey playerKey = new NamespacedKey(SimpleToolsPlugin.getInstance(), "player");
        // 获取玩家的名字
        String playerName = player.getName();
        // 在 PersistentDataContainer 中存储创建者和允许的玩家信息
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(key, PersistentDataType.STRING, playerName);
        dataContainer.set(playerKey, PersistentDataType.STRING, getPassName(result));

        result.setItemMeta(PlayerWorldUtils.getPassInfo(meta, getPassName(result), playerName));
        event.setResult(result); // 更新铁砧输出物品
    }

    /**
     * 获取通行证物品名称
     * 通行证为更改过名称的纸张 所以这个方法只能用于纸张 其他物品不保证能用
     */
    private String getPassName(ItemStack itemStack) {
        if (itemStack.displayName() instanceof TranslatableComponent translationArgument) {
            if (translationArgument.arguments().getFirst().value() instanceof TextComponent textComponent) {
                if (textComponent.children().getFirst() instanceof TextComponent contentComponent)
                    return contentComponent.content();
            }
        }
        return itemStack.getType().name();
    }
}
