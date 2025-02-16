package org.simpleTools.NetherPortalTeleport;

import net.kyori.adventure.text.TextComponent;
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
    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        var to = getToLocation(player);
        if (to != null) {
            event.setCancelled(true);
            player.teleport(to);
        }
    }

    private @Nullable Location getToLocation(Player player) {
        Location result;

        var sign = getSign(player);
        if (sign == null) return null;
        var signSide = sign.getSide(Side.FRONT);

        var worldAndYawTextComponent = (TextComponent) signSide.line(3);
        var worldAndYaw = getWorldAndYaw(worldAndYawTextComponent.content());

        var coordinateTextComponent = (TextComponent) signSide.line(2);
        var coordinate = getCoordinate(coordinateTextComponent.content());

        if (coordinate == null) return null;

        if (worldAndYaw == null) {
            result = new Location(player.getWorld(), coordinate.getX(), coordinate.getY(), coordinate.getZ());
        } else {
            var world = worldAndYaw.getWorld() == null ? player.getWorld() : worldAndYaw.getWorld();
            result = new Location(world, coordinate.getX(), coordinate.getY(), coordinate.getZ());
            result.setYaw(worldAndYaw.getYaw());
        }

        return result;
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
        if (configs.length == 1) {
            return new Location(Bukkit.getWorld(configs[0]), 0, 0, 0);
        } else if (configs.length == 2) {
            var loc = new Location(Bukkit.getWorld(configs[0]), 0, 0, 0);
            loc.setYaw(Float.parseFloat(configs[1]));
            return loc;
        }
        return null;
    }

    // 获取传送坐标
    private @Nullable Location getCoordinate(String coordinate) {
        if (coordinate.isEmpty()) return null;

        var coordinates = coordinate.split(",");
        if (coordinates.length == 3) {
            try {
                double x = Integer.parseInt(coordinates[0]);
                double y = Integer.parseInt(coordinates[1]);
                double z = Integer.parseInt(coordinates[2]);
                return new Location(null, x, y, z);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
