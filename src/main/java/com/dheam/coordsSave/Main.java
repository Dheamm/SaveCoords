package com.dheam.coordsSave;

import com.cjcrafter.foliascheduler.FoliaCompatibility;
import com.cjcrafter.foliascheduler.ServerImplementation;
import com.dheam.coordsSave.commands.CoordsCommand;
import com.dheam.coordsSave.commands.TabCompleterHandler;
import com.dheam.coordsSave.config.ConfigManager;
import com.dheam.coordsSave.config.Messages;
import com.dheam.coordsSave.data.CoordStorage;
import com.dheam.coordsSave.data.DatabaseManager;
import com.dheam.coordsSave.utils.NameGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    private ConfigManager configManager;
    private Messages messages;
    private DatabaseManager databaseManager;
    private CoordStorage coordStorage;
    private NameGenerator nameGenerator;
    private ServerImplementation scheduler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        this.messages = new Messages(this);

        this.scheduler = new FoliaCompatibility(this).getServerImplementation();

        this.databaseManager = new DatabaseManager(this, configManager);
        this.databaseManager.init();
        this.coordStorage = new CoordStorage(databaseManager);
        this.nameGenerator = new NameGenerator(coordStorage);

        registerCommands();

        getLogger().info("SaveCoords habilitado.");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }

        if (scheduler != null) {
            scheduler.cancelTasks();
        }

        getLogger().info("SaveCoords deshabilitado.");
    }

    private void registerCommands() {
        var coordsCmd = getCommand("coords");
        if (coordsCmd == null) {
            getLogger().severe("No se pudo registrar el comando /coords. Revisa plugin.yml");
            return;
        }

        CoordsCommand executor = new CoordsCommand(this, coordStorage, messages, configManager, nameGenerator);
        coordsCmd.setExecutor(executor);
        coordsCmd.setTabCompleter(new TabCompleterHandler(this, coordStorage));
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Messages getMessages() {
        return messages;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public CoordStorage getCoordStorage() {
        return coordStorage;
    }

    public NameGenerator getNameGenerator() {
        return nameGenerator;
    }

    public ServerImplementation getScheduler() {
        return scheduler;
    }
}
