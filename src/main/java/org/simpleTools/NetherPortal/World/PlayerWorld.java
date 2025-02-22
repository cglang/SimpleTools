package org.simpleTools.NetherPortal.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;
import org.simpleTools.ConfigUtils;

import java.util.UUID;

public class PlayerWorld {

    private static final String playerWorldsFileName = "playerWorlds.yml";
    private final FileConfiguration playerWorldConfig;
    private final PlayerWorldPass playerWorldPass;


    public PlayerWorld() {
        playerWorldConfig = ConfigUtils.initConfig(playerWorldsFileName);
        playerWorldPass = new PlayerWorldPass();
    }

    /**
     * 创建玩家的世界
     */
    public World createPlayerWorld(UUID playerId) {
        String worldName = "world_" + playerId.toString(); // 以玩家ID命名世界
        WorldCreator creator = new WorldCreator(worldName);

        creator.seed(PlayerWorldUtils.uuidToSeed(playerId));  // 设置种子
        creator.environment(World.Environment.NORMAL);  // 你可以根据需要设置不同的环境（如：正常、末地、地狱）
        return creator.createWorld();
    }

    /**
     * 获取玩家的世界 没有则创建
     */
    public @Nullable World getPlayerWorld(UUID playerId, boolean create) {
        String worldName = "world_" + playerId.toString();
        World world = Bukkit.getWorld(worldName);
        if (world == null && create) {
            world = createPlayerWorld(playerId);  // 如果世界不存在，则创建一个新世界
        }
        return world;
    }

    public @Nullable Location loadPlayerWorld(Player player) {
        var world = getPlayerWorld(player.getUniqueId(), true);
        if (world == null) return null;

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

    /**
     * 保存玩家的世界和坐标信息
     */
    public void savePlayerWorld(UUID playerId, String worldName, double x, double y, double z) {
        // 保存世界信息和坐标
        playerWorldConfig.set(playerId + ".world", worldName);
        playerWorldConfig.set(playerId + ".x", x);
        playerWorldConfig.set(playerId + ".y", y);
        playerWorldConfig.set(playerId + ".z", z);

        ConfigUtils.savePlayerWorlds(playerWorldConfig, playerWorldsFileName);
    }

    public boolean toPlayerWorldCheck(Player player, World world) {
        // 自己的世界 允许进入
        if (world.getName().equals("world_" + player.getUniqueId())) return true;

        // 进入者是OP 允许进入
        if (player.hasPermission("minecraft.command.op")) return true;

        // 获取玩家背包中的所有物品
        Inventory inventory = player.getInventory();
        var decide = playerWorldPass.checkPass(inventory, world.getName(), player.getName());
        if (decide) return true;

        // 获取玩家末影箱中的所有物品
        Inventory enderChest = player.getEnderChest();
        decide = playerWorldPass.checkPass(enderChest, world.getName(), player.getName());
        return decide;
    }
}
