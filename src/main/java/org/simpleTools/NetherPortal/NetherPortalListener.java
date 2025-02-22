package org.simpleTools.NetherPortal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

// 下界门传送
public class NetherPortalListener implements Listener {
    private final NetherPortal netherPortal;

    public NetherPortalListener() {
        netherPortal = new NetherPortal();
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        var sign = netherPortal.getSign(player);
        if (sign != null) {
            var teleportModel = netherPortal.getTeleportModel(sign, player);
            var cancel = netherPortal.teleport(player, teleportModel);

            event.setCancelled(cancel);
        }
    }
}

