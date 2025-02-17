package org.simpleTools.NetherPortalTeleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerWorld {
    public World createPlayerWorld(UUID playerId) {
        String worldName = "world_" + playerId.toString(); // 以玩家ID命名世界
        WorldCreator creator = new WorldCreator(worldName);

        creator.seed(WorldUtils.uuidToSeed(playerId));  // 设置种子
        creator.environment(World.Environment.NORMAL);  // 你可以根据需要设置不同的环境（如：正常、末地、地狱）
        return creator.createWorld();
    }

    public World getPlayerWorld(UUID playerId) {
        String worldName = "world_" + playerId.toString();
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = createPlayerWorld(playerId);  // 如果世界不存在，则创建一个新世界
        }
        return world;
    }

    public void teleportToPlayerWorld(Player player) {
        var playerId = player.getUniqueId();
        World world = getPlayerWorld(playerId);
        Location spawnLocation = world.getSpawnLocation();  // 获取世界的出生点
        player.teleport(spawnLocation);  // 传送玩家
    }
}
