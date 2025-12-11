package com.dheam.coordsSave.commands.subcommands;

import com.dheam.coordsSave.Main;
import com.dheam.coordsSave.commands.CoordsCommand;
import com.dheam.coordsSave.config.ConfigManager;
import com.dheam.coordsSave.config.Messages;
import org.bukkit.command.CommandSender;

public class ReloadSubcommand implements CoordsCommand.Subcommand {

    private final Main plugin;
    private final Messages messages;
    private final ConfigManager configManager;

    public ReloadSubcommand(Main plugin, Messages messages, ConfigManager configManager) {
        this.plugin = plugin;
        this.messages = messages;
        this.configManager = configManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        configManager.reload();
        messages.reload();

        plugin.getDatabaseManager().shutdown();
        plugin.getDatabaseManager().init();

        messages.send(sender, "reload_ok", null);
    }

    @Override
    public String getPermission() {
        return "savecoords.reload";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }
}
