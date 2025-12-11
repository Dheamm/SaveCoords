package com.dheam.coordsSave.config;

import com.dheam.coordsSave.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration config;

    private int maxCoordsDefault;
    private Map<String, Integer> maxCoordsPermissions = new HashMap<>();

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        plugin.reloadConfig();
        load();
    }

    private void load() {
        this.config = plugin.getConfig();

        this.maxCoordsDefault = config.getInt("max-coords-default", 10);

        maxCoordsPermissions.clear();
        ConfigurationSection section = config.getConfigurationSection("max-coords-permission");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                maxCoordsPermissions.put(key, section.getInt(key));
            }
        }
    }

    public int getMaxCoordsDefault() {
        return maxCoordsDefault;
    }

    public Map<String, Integer> getMaxCoordsPermissions() {
        return maxCoordsPermissions;
    }

    public String getDatabaseType() {
        return config.getString("database.type", "sqlite").toLowerCase();
    }

    public String getMySqlHost() {
        return config.getString("database.mysql.host", "localhost");
    }

    public int getMySqlPort() {
        return config.getInt("database.mysql.port", 3306);
    }

    public String getMySqlDatabase() {
        return config.getString("database.mysql.database", "savecoords");
    }

    public String getMySqlUser() {
        return config.getString("database.mysql.user", "root");
    }

    public String getMySqlPassword() {
        return config.getString("database.mysql.password", "");
    }
}
