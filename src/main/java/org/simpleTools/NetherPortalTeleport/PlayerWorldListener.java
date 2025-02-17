package org.simpleTools.NetherPortalTeleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.FileConfiguration;

import org.simpleTools.ConfigUtils;

public class PlayerWorldListener implements Listener {
    private static final String playerWorldsFileName = "playerWorlds.yml";
    private final FileConfiguration playerWorldConfig;

    public PlayerWorldListener() {
        playerWorldConfig = ConfigUtils.initConfig(playerWorldsFileName);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayerWorld(player);
    }

    private void loadPlayerWorld(Player player) {
        PlayerWorld pw = new PlayerWorld();
        World world = pw.getPlayerWorld(player.getUniqueId());

        var playerQuitWorldName = playerWorldConfig.getString(player.getUniqueId() + ".world");
        if (playerQuitWorldName == null) return;

        var playerQuitWorld = Bukkit.getWorld(playerQuitWorldName);
        if (playerQuitWorld == null) return;

        if (playerQuitWorld.getName().equals(world.getName())) {
            double x = playerWorldConfig.getDouble(player.getUniqueId() + ".x");
            double y = playerWorldConfig.getDouble(player.getUniqueId() + ".y");
            double z = playerWorldConfig.getDouble(player.getUniqueId() + ".z");

            player.teleport(new Location(world, x, y, z));
        }
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
}
