package org.simpleTools;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleTools.CraftingTable.CraftingTableListener;
import org.simpleTools.DeathChest.DeathChestListener;
import org.simpleTools.EnderChest.EnderChestPlusListener;
import org.simpleTools.Entitites.ReproductionListener;
import org.simpleTools.FireworkPlus.BingoCommandListener;
import org.simpleTools.ShulkerBox.ShulkerBoxListener;

import java.util.Objects;

public final class SimpleToolsPlugin extends JavaPlugin {

    private static SimpleToolsPlugin instance;

    public static SimpleToolsPlugin getInstance() {
        return instance;
    }

    public SimpleToolsPlugin() {
        super();
        instance = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EnderChestPlusListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShulkerBoxListener(), this);
        Bukkit.getPluginManager().registerEvents(new CraftingTableListener(), this);
        Bukkit.getPluginManager().registerEvents(new BingoCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathChestListener(), this);
        Bukkit.getPluginManager().registerEvents(new ReproductionListener(), this);


        Objects.requireNonNull(this.getCommand("bingo")).setExecutor(new BingoCommandListener());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
