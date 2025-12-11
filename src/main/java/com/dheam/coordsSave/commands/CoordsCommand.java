package com.dheam.coordsSave.commands;

import com.dheam.coordsSave.Main;
import com.dheam.coordsSave.commands.subcommands.DeleteSubcommand;
import com.dheam.coordsSave.commands.subcommands.GetSubcommand;
import com.dheam.coordsSave.commands.subcommands.ListSubcommand;
import com.dheam.coordsSave.commands.subcommands.ReloadSubcommand;
import com.dheam.coordsSave.commands.subcommands.SaveSubcommand;
import com.dheam.coordsSave.config.ConfigManager;
import com.dheam.coordsSave.config.Messages;
import com.dheam.coordsSave.data.CoordStorage;
import com.dheam.coordsSave.utils.NameGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CoordsCommand implements CommandExecutor {

    private final Main plugin;
    private final Messages messages;
    private final Map<String, Subcommand> subcommands = new HashMap<>();

    public CoordsCommand(Main plugin,
                         CoordStorage storage,
                         Messages messages,
                         ConfigManager configManager,
                         NameGenerator nameGenerator) {
        this.plugin = plugin;
        this.messages = messages;

        registerSubcommand("save", new SaveSubcommand(plugin, storage, messages, configManager, nameGenerator));
        registerSubcommand("get", new GetSubcommand(plugin, storage, messages));
        registerSubcommand("list", new ListSubcommand(plugin, storage, messages));
        registerSubcommand("delete", new DeleteSubcommand(plugin, storage, messages));
        registerSubcommand("reload", new ReloadSubcommand(plugin, messages, configManager));
    }

    private void registerSubcommand(String name, Subcommand subcommand) {
        subcommands.put(name.toLowerCase(), subcommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.send(sender, "usage_main", null);
            return true;
        }

        String sub = args[0].toLowerCase();
        Subcommand handler = subcommands.get(sub);

        if (handler == null) {
            messages.send(sender, "unknown_subcommand", null);
            return true;
        }

        if (handler.isPlayerOnly() && !(sender instanceof Player)) {
            messages.send(sender, "only_players", null);
            return true;
        }

        if (!handler.hasPermission(sender)) {
            messages.send(sender, "no_permission", null);
            return true;
        }

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        handler.execute(sender, subArgs);
        return true;
    }

    public interface Subcommand {
        void execute(CommandSender sender, String[] args);

        default boolean hasPermission(CommandSender sender) {
            String perm = getPermission();
            return perm == null || perm.isEmpty() || sender.hasPermission(perm);
        }

        String getPermission();

        boolean isPlayerOnly();
    }
}
