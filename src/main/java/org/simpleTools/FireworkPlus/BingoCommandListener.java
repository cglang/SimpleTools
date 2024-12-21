package org.simpleTools.FireworkPlus;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.simpleTools.SimpleToolsPlugin;

import java.util.Random;

public class BingoCommandListener implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 检查命令参数数量
        if (args.length != 1) {
            sender.sendMessage("使用方法: /bingo <玩家名称>");
            return false;
        }

        // 获取目标玩家的名称
        String targetPlayerName = args[0];

        // 查找目标玩家
        Player targetPlayer = sender.getServer().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage("玩家 " + targetPlayerName + " 不在线.");
            return false;
        }

        // 获取目标玩家的位置和朝向
        Location targetLocation = targetPlayer.getLocation();
        Vector direction = targetLocation.getDirection(); // 获取目标玩家的朝向

        // 保存原始Y坐标
        double originalY = targetLocation.getY();
        Location fireworkLocation = targetLocation.add(direction.multiply(RandomNumber(15, 30)));
        fireworkLocation.setY(originalY + 2);
        fireworkLocation.setZ(fireworkLocation.getZ() + RandomNumber(-5, 5));
        fireworkLocation.setX(fireworkLocation.getX() + RandomNumber(-5, 5));

        // 释放烟花
        launchFirework(fireworkLocation);
        return true;
    }

    private int RandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min)) + min;
    }

    // 定义颜色数组
    public static final Color[] COLORS = {
            Color.RED, Color.YELLOW, Color.BLUE,
            Color.WHITE, Color.LIME,
            Color.AQUA, Color.FUCHSIA, Color.ORANGE
    };

    // 方法：从颜色数组中随机选取一个颜色
    public static Color getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(COLORS.length);
        return COLORS[index];
    }

    public static final Type[] TYPES = {
            Type.BALL_LARGE, Type.STAR, Type.BURST
    };

    public static Type getRandomType() {
        Random random = new Random();
        int index = random.nextInt(TYPES.length);
        return TYPES[index];
    }

    // 释放烟花的方法
    private void launchFirework(Location location) {
        // 创建烟花实体
        Firework firework = location.getWorld().spawn(location, Firework.class);

        // 设置烟花效果
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        // 创建烟花效果
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(getRandomColor())           // 设置颜色
                .withFade(getRandomColor())         // 设置渐变颜色
                .with(getRandomType())                // 设置烟花类型
                .trail(true)                    // 启用拖尾效果
                .flicker(true)                  // 启用闪烁效果
                .build();
        fireworkMeta.addEffect(effect);

        firework.setFireworkMeta(fireworkMeta);
        // 计划烟花在10 tick后爆炸
        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(SimpleToolsPlugin.getInstance(), 20); // 20 tick = 1 秒
    }
}
