package org.simpleTools.CraftingTable;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class WithCraftingTablePlus implements Listener {
    public static void open(Player player) {
        player.openWorkbench(null, true);
    }
}
