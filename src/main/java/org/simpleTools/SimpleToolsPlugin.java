package org.simpleTools;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleTools.MainHand.WorkbenchListener;
import org.simpleTools.DeathChest.DeathChestListener;
import org.simpleTools.MainHand.EnderChest.EnderChestPlusListener;
import org.simpleTools.Entitites.ReproductionListener;
import org.simpleTools.FireworkPlus.BingoCommandListener;
import org.simpleTools.NetherPortal.NetherPortal;
import org.simpleTools.NetherPortal.NetherPortalListener;
import org.simpleTools.NetherPortal.World.PlayerWorldListener;
import org.simpleTools.MainHand.ShulkerBox.ShulkerBoxListener;

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
        saveDefaultConfig();
        if (getConfig().getBoolean("openPhantomBoxWithHand"))
            Bukkit.getPluginManager().registerEvents(new EnderChestPlusListener(), this);

        if (getConfig().getBoolean("openShulkerBoxWithHand"))
            Bukkit.getPluginManager().registerEvents(new ShulkerBoxListener(), this);

        if (getConfig().getBoolean("openCraftingTableWithHand"))
            Bukkit.getPluginManager().registerEvents(new WorkbenchListener(), this);

        if (getConfig().getBoolean("launchFirework")) {
            Bukkit.getPluginManager().registerEvents(new BingoCommandListener(), this);
            Objects.requireNonNull(this.getCommand("bingo")).setExecutor(new BingoCommandListener());
        }

        if (getConfig().getBoolean("createChestOnDeath"))
            Bukkit.getPluginManager().registerEvents(new DeathChestListener(), this);

        if (getConfig().getBoolean("breedFrogEggs"))
            Bukkit.getPluginManager().registerEvents(new ReproductionListener(), this);

        if (getConfig().getBoolean("teleportToNetherPortal"))
            Bukkit.getPluginManager().registerEvents(new NetherPortalListener(), this);

        if (getConfig().getBoolean("openPlayerWorld")) {
            NetherPortal.setOpenPlayerWorld(true);
            Bukkit.getPluginManager().registerEvents(new PlayerWorldListener(), this);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
