package org.simpleTools.NetherPortalTeleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.jetbrains.annotations.Nullable;

// 下界门传送
public class NetherPortalTeleportListener implements Listener {
    private static boolean openPlayerWorld;

    public static void setOpenPlayerWorld(boolean openPlayerWorld) {
        NetherPortalTeleportListener.openPlayerWorld = openPlayerWorld;
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        var sign = getSign(player);
        if (sign == null) return;

        var mode = GetNetherPortalMode(SignUtils.getSignLine(sign, Side.FRONT, 1));

        switch (mode) {
            case Teleport:
                var to = toCoordinate(player, sign);
                if (to != null) {
                    event.setCancelled(true);
                    player.teleport(to);
                }
                break;
            case PlayerWorld:
                var toPlayerWorld = toPlayerWorld(player, sign);
                if (toPlayerWorld != null) {
                    event.setCancelled(true);
                    player.teleport(toPlayerWorld);
                }
                break;
            case Command:
                break;
            default:
        }
    }

    // 传送到指定坐标
    private @Nullable Location toCoordinate(Player player, Sign sign) {
        Location result;

        var worldAndYaw = getWorldAndYaw(SignUtils.getSignLine(sign, Side.FRONT, 3));
        var coordinate = getCoordinate(SignUtils.getSignLine(sign, Side.FRONT, 2));

        if (coordinate == null) return null;

        if (worldAndYaw == null) {
            coordinate.setWorld(player.getWorld());
            result = coordinate;
        } else {
            var world = worldAndYaw.getWorld() == null ? player.getWorld() : worldAndYaw.getWorld();
            coordinate.setWorld(world);
            result = coordinate;
            result.setYaw(worldAndYaw.getYaw());
        }

        return result;
    }

    // 传送到玩家自己的世界
    private @Nullable Location toPlayerWorld(Player player, Sign sign) {
        // 判断是否是进入玩家自己的世界
        if (openPlayerWorld) {
            var twoLine = SignUtils.getSignLine(sign, Side.FRONT, 2);
            var coordinate = getCoordinate(twoLine);
            PlayerWorld pw = new PlayerWorld();
            var playerWorld = pw.getPlayerWorld(player.getUniqueId());
            if (coordinate == null) {
                return playerWorld.getSpawnLocation();
            } else {
                return new Location(playerWorld, coordinate.getX(), coordinate.getY(), coordinate.getZ());
            }
        }
        return null;
    }

    // 获取告示牌
    private @Nullable Sign getSign(Player player) {
        var location = player.getLocation();
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();

        // 遍历玩家周围三格范围内的所有方块
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                Block block = player.getWorld().getBlockAt(x + dx, y + 1, z + dz);
                if (block.getType() == Material.OAK_WALL_SIGN) {
                    return (Sign) block.getState();
                }
            }
        }

        return null;
    }

    // 获取传送的世界和转向
    private @Nullable Location getWorldAndYaw(String text) {
        if (text.isEmpty()) return null;

        var configs = text.split(",");

        Location result = null;
        if (configs.length == 1) {
            result = new Location(Bukkit.getWorld(configs[0]), 0, 0, 0);
        } else if (configs.length == 2) {
            result = new Location(Bukkit.getWorld(configs[0]), 0, 0, 0);
            result.setYaw(Float.parseFloat(configs[1]));
        }
        return result;
    }

    // 获取传送坐标
    private @Nullable Location getCoordinate(String coordinate) {
        if (coordinate.isEmpty()) return null;

        var coordinates = coordinate.split(",");
        if (coordinates.length == 3) {
            try {
                double x = Double.parseDouble(coordinates[0]);
                double y = Double.parseDouble(coordinates[1]);
                double z = Double.parseDouble(coordinates[2]);
                return new Location(null, x, y, z);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    // 获取传送门模式
    private NetherPortalMode GetNetherPortalMode(String text) {
        if (text.equals("PlayerWorld") || text.equals("玩家的世界"))
            return NetherPortalMode.PlayerWorld;
        else if (text.equals("Command") || text.equals("命令"))
            return NetherPortalMode.Command;
        else if (text.equals("Teleport") || text.equals("传送") || text.isEmpty())
            return NetherPortalMode.Teleport;
        else
            return NetherPortalMode.Teleport;
    }
}

