package org.simpleTools.NetherPortal;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleTools.NetherPortal.Teleport.TeleportMode;
import org.simpleTools.NetherPortal.Teleport.TeleportModel;
import org.simpleTools.NetherPortal.World.PlayerWorld;

import java.util.*;

public class NetherPortal {
    private static boolean openPlayerWorld;

    private final PlayerWorld playerWorld;

    public NetherPortal() {
        playerWorld = new PlayerWorld();
    }

    public static void setOpenPlayerWorld(boolean openPlayerWorld) {
        NetherPortal.openPlayerWorld = openPlayerWorld;
    }

    // 获取告示牌
    public @Nullable Sign getSign(Player player) {
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
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
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

    /**
     * 获取传送信息
     */
    public TeleportModel getTeleportModel(Sign sign, Player player) {
        var mode = getNetherPortalMode(SignUtils.getSignLine(sign, Side.FRONT, 1));

        var coordinate = getCoordinate(SignUtils.getSignLine(sign, Side.FRONT, 2));

        World world;
        if (mode == TeleportMode.PlayerWorld) {
            world = getPlayerWorld(SignUtils.getSignLine(sign, Side.FRONT, 3), player);
        } else {
            world = getWorld(SignUtils.getSignLine(sign, Side.FRONT, 3), player);
        }

        var yaw = getYaw(SignUtils.getSignLine(sign, Side.FRONT, 3));

        return new TeleportModel(mode, world, coordinate, yaw);
    }

    // 获取传送门模式
    private TeleportMode getNetherPortalMode(String text) {
        if (text.equals("PlayerWorld") || text.equals("玩家的世界"))
            return TeleportMode.PlayerWorld;
        else if (text.equals("Teleport") || text.equals("传送"))
            return TeleportMode.Teleport;
        else
            return TeleportMode.Original;
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

    // 获取传送的世界
    private @NotNull World getWorld(String worldAndYaw, Player player) {
        if (worldAndYaw.isEmpty())
            return player.getWorld();

        var configs = worldAndYaw.split(",");

        World world = null;
        if (configs.length == 1 || configs.length == 2) {
            world = Bukkit.getWorld(configs[0]);
        }

        return world == null ? player.getWorld() : world;
    }

    // 获取玩家的世界
    private @Nullable World getPlayerWorld(String worldAndYaw, Player player) {
        var worldPlayerName = worldAndYaw.split(",")[0];
        var uuid = getPlayerUniqueId(worldPlayerName);

        PlayerWorld pw = new PlayerWorld();
        World playerWorld;
        if (uuid.toString().equals(player.getUniqueId().toString())) { // 进入自己的世界
            playerWorld = pw.getPlayerWorld(player.getUniqueId(), true);
        } else { // 进入别人的世界
            // 进入者是OP 进入的世界没有创建则新创建一个
            var isCreate = player.hasPermission("minecraft.command.op");
            playerWorld = pw.getPlayerWorld(uuid, isCreate);
        }

        return playerWorld;
    }

    // 获取朝向
    private float getYaw(String worldAndYaw) {
        if (worldAndYaw.isEmpty()) return 0;

        var configs = worldAndYaw.split(",");

        if (configs.length == 2) {
            return Float.parseFloat(configs[1]);
        }
        return 0;
    }

    // 根据玩家名获取玩家Id
    private UUID getPlayerUniqueId(String playerName) {
        // 尝试通过玩家用户名获取玩家对象
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            // 如果玩家在线，返回玩家的Id
            return player.getUniqueId();
        } else {
            // 如果玩家不在线，可以考虑使用离线玩家的方法
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            return offlinePlayer.getUniqueId();
        }
    }

    /**
     * 传送玩家
     */
    public boolean teleport(Player player, TeleportModel teleportModel) {
        boolean result = false;

        switch (teleportModel.getMode()) {
            case Teleport:
                if (teleportModel.getLocation() != null) {
                    player.teleport(teleportModel.getLocation());
                    result = true;
                }
                break;
            case PlayerWorld:
                if (!openPlayerWorld) break;

                if (teleportModel.getLocation() != null) {
                    boolean decide = playerWorld.toPlayerWorldCheck(player, teleportModel.getLocation().getWorld());
                    if (decide) {
                        player.teleport(teleportModel.getLocation());
                    } else {
                        player.sendMessage("你不能进入这个世界");
                    }
                } else {
                    player.sendMessage("这个玩家的世界尚未创建，无法进入。");
                }
                result = true;
                break;
            case Command:
                break;
            default:
        }

        return result;
    }
}