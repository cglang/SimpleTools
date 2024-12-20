package org.simpleTools;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleTools.CraftingTable.CraftingTableListener;
import org.simpleTools.EnderChest.EnderChestPlusListener;
import org.simpleTools.ShulkerBox.ShulkerBoxListener;

public final class SimpleToolsPlugin extends JavaPlugin {

    private static SimpleToolsPlugin instance;

    public static SimpleToolsPlugin getInstance() {
        return instance;
    }

    private final EnderChestPlusListener mainListener = new EnderChestPlusListener();
    private final ShulkerBoxListener shulkerBoxListener = new ShulkerBoxListener();
    private final CraftingTableListener craftingTableListener = new CraftingTableListener();

    public SimpleToolsPlugin() {
        super();
        instance = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(mainListener, this);
        Bukkit.getPluginManager().registerEvents(shulkerBoxListener, this);
        Bukkit.getPluginManager().registerEvents(craftingTableListener, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
