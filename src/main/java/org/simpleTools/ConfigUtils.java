package org.simpleTools;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigUtils {
    // 保存配置文件
    public static void savePlayerWorlds(FileConfiguration fileConfiguration, String child) {
        try {
            fileConfiguration.save(new File(SimpleToolsPlugin.getInstance().getDataFolder(), child));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public static YamlConfiguration initConfig(String fileName) {
        File playerWorldsFile = new File(SimpleToolsPlugin.getInstance().getDataFolder(), fileName);
        if (!playerWorldsFile.exists()) {
            SimpleToolsPlugin.getInstance().saveResource(fileName, false);
        }
        return YamlConfiguration.loadConfiguration(playerWorldsFile);
    }
}
