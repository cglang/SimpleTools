package org.simpleTools.NetherPortalTeleport;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
                event.setCancelled(true);

                var to = toCoordinate(player, sign);
                if (to != null) {
                    player.teleport(to);
                }
                break;
            case PlayerWorld:
                event.setCancelled(true);

                // 进入世界权限检查
                var threeLine = SignUtils.getSignLine(sign, Side.FRONT, 3);
                var worldByPlayerName = threeLine.split(",")[0];
                boolean decide = toPlayerWorldCheck(player, worldByPlayerName);
                if (decide) {
                    var toPlayerWorld = toPlayerWorld(player, sign);
                    if (toPlayerWorld != null) {
                        player.teleport(toPlayerWorld);
                    }
                } else {
                    player.sendMessage("你不能进入这个世界");
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
        if (!openPlayerWorld) return null;

        PlayerWorld pw = new PlayerWorld();
        World playerWorld;

        var threeLine = SignUtils.getSignLine(sign, Side.FRONT, 3);
        var worldByPlayerName = threeLine.split(",")[0];

        var uuid = getPlayerUUID(worldByPlayerName);
        if (uuid.toString().equals(player.getUniqueId().toString())) { // 进入自己的世界
            playerWorld = pw.getPlayerWorld(player.getUniqueId());
        } else { // 进入别人的世界
            playerWorld = pw.getPlayerWorld(uuid);
        }

        var twoLine = SignUtils.getSignLine(sign, Side.FRONT, 2);
        var coordinate = getCoordinate(twoLine);
        if (coordinate == null) {
            return playerWorld.getSpawnLocation();
        } else {
            return new Location(playerWorld, coordinate.getX(), coordinate.getY(), coordinate.getZ());
        }
    }

    private boolean toPlayerWorldCheck(Player player, String worldName) {

        // 没有指定世界名 允许进入
        if (worldName.isEmpty()) return true;

        // 自己的世界 允许进入
        if (worldName.equals(player.getName())) return true;

        // 进入者是OP 允许进入
        if (player.hasPermission("minecraft.command.op")) return true;

        // 获取玩家背包中的所有物品
        Inventory inventory = player.getInventory();
        var decide = PlayerWorldUtils.checkPass(inventory, worldName, player.getName());
        if (decide) return true;

        // 获取玩家末影箱中的所有物品
        Inventory enderChest = player.getEnderChest();
        decide = PlayerWorldUtils.checkPass(enderChest, worldName, player.getName());
        return decide;
    }

    public static UUID getPlayerUUID(String playerName) {
        // 尝试通过玩家用户名获取玩家对象
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            // 如果玩家在线，返回玩家的UUID
            return player.getUniqueId();
        } else {
            // 如果玩家不在线，可以考虑使用离线玩家的方法
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            return offlinePlayer.getUniqueId();
        }
    }

    // 获取告示牌
    private @Nullable Sign getSign(Player player) {
        var location = player.getLocation();

        Block portalBlock = getStartObsidianBlock(location, player.getWorld());
        if (portalBlock == null) return null;

        var signBlock = getSignBlock(portalBlock);
        if (signBlock == null) return null;

        return (Sign) signBlock.getState();
    }

    // 获取第一个开始位置的黑曜石方块
    private @Nullable Block getStartObsidianBlock(Location location, World world) {
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        // 遍历玩家周围三格范围内的所有方块
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    Block block = world.getBlockAt(x + dx, y + 1, z + dz);
                    if (block.getType() == Material.OBSIDIAN) {
                        return block;
                    }
                }
            }
        }

        return null;
    }

    // 获取告示牌所在的方块
    private @Nullable Block getSignBlock(Block start) {
        Set<Block> portalBlocks = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Block block = queue.poll();
            if (portalBlocks.contains(block)) continue;

            Material type = block.getType();
            if (type == Material.OBSIDIAN) {
                portalBlocks.add(block);
                for (BlockFace face : BlockFace.values()) {
                    queue.add(block.getRelative(face));
                }
            }

            if (block.getType() == Material.OAK_WALL_SIGN)
                return block;
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
        else if (text.equals("Teleport") || text.equals("传送") || text.isEmpty())
            return NetherPortalMode.Teleport;
        else
            return NetherPortalMode.Teleport;
    }
}

