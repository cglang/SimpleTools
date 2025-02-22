package org.simpleTools.NetherPortal.World;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerWorldListener implements Listener {
    private final PlayerWorld playerWorld;
    private final PlayerWorldPass playerWorldPass;

    public PlayerWorldListener() {
        playerWorld = new PlayerWorld();
        playerWorldPass = new PlayerWorldPass();
    }

    /**
     * 玩家登陆时触发
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        var loc = playerWorld.loadPlayerWorld(player);

        if (loc != null)
            player.teleport(loc);
    }

    /**
     * 玩家退出时触发
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var loc = player.getLocation();
        var playerId = player.getUniqueId();
        var worldName = player.getWorld().getName();

        playerWorld.savePlayerWorld(playerId, worldName, loc.getX(), loc.getY(), loc.getZ());
    }

    // 铁砧命名时触发 对纸进行命名时进行签名
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult(); // 获取铁砧的输出物品
        if (result == null || result.getType() != Material.PAPER) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        if (!(event.getViewers().getFirst() instanceof Player player)) return;

        result = playerWorldPass.savePassInfo(result, meta, player.getName(), player.getUniqueId());
        event.setResult(result); // 更新铁砧输出物品
    }
}
