package com.dheam.coordsSave.data;

import com.dheam.coordsSave.Main;
import com.dheam.coordsSave.config.ConfigManager;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    public interface DatabaseProvider {
        Connection getConnection() throws SQLException;

        void initTables() throws SQLException;

        void close();
    }

    private final Main plugin;
    private final ConfigManager configManager;

    private DatabaseProvider provider;

    public DatabaseManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void init() {
        String type = configManager.getDatabaseType();

        if (provider != null) {
            provider.close();
        }

        if ("mysql".equalsIgnoreCase(type)) {
            provider = new MySQLProvider(
                    configManager.getMySqlHost(),
                    configManager.getMySqlPort(),
                    configManager.getMySqlDatabase(),
                    configManager.getMySqlUser(),
                    configManager.getMySqlPassword()
            );
            plugin.getLogger().info("Usando MySQL como base de datos.");
        } else {
            provider = new SQLiteProvider(plugin);
            plugin.getLogger().info("Usando SQLite como base de datos.");
        }

        try {
            provider.initTables();
        } catch (SQLException e) {
            plugin.getLogger().severe("No se pudo inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DatabaseProvider getProvider() {
        return provider;
    }

    public void shutdown() {
        if (provider != null) {
            provider.close();
        }
    }
}
