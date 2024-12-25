package org.simpleTools.Entitites;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.simpleTools.SimpleToolsPlugin;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReproductionListener implements Listener {
    private final Map<UUID, String> frogBreedingPlayers = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // 检查玩家是否与青蛙交互
        if (event.getRightClicked().getType() == EntityType.FROG) {
            Player player = event.getPlayer();
            frogBreedingPlayers.put(event.getRightClicked().getUniqueId(), player.getName());
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        // 检查是否是青蛙生成卵
        if (event.getEntity().getType() == EntityType.FROG && event.getTo() == Material.FROGSPAWN) {
            UUID frogUUID = event.getEntity().getUniqueId();
            if (frogBreedingPlayers.containsKey(frogUUID)) {
                String playerName = frogBreedingPlayers.get(frogUUID);
                // 向所有玩家广播消息
                String broadcastMessage = MessageFormat.format("万恶不赦的{0}又开始繁殖他的青蛙了！", playerName);
                SimpleToolsPlugin.getInstance().getServer().sendMessage(Component.text(broadcastMessage));
                frogBreedingPlayers.remove(frogUUID);
            }
        }
    }
}
