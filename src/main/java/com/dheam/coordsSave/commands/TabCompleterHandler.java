package com.dheam.coordsSave.commands;

import com.dheam.coordsSave.Main;
import com.dheam.coordsSave.data.CoordStorage;
import com.dheam.coordsSave.model.SavedCoord;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TabCompleterHandler implements TabCompleter {

    private final Main plugin;
    private final CoordStorage storage;

    public TabCompleterHandler(Main plugin, CoordStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return result;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            if ("save".startsWith(prefix)) result.add("save");
            if ("list".startsWith(prefix)) result.add("list");
            if ("get".startsWith(prefix)) result.add("get");
            if ("delete".startsWith(prefix)) result.add("delete");
            if (player.hasPermission("savecoords.reload") && "reload".startsWith(prefix)) result.add("reload");
            return result;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("save")) {
            Location loc = player.getLocation();
            if (args.length == 2) {
                result.add(String.valueOf(loc.getBlockX()));
            } else if (args.length == 3) {
                result.add(String.valueOf(loc.getBlockY()));
            } else if (args.length == 4) {
                result.add(String.valueOf(loc.getBlockZ()));
            }
            return result;
        }

        if (sub.equals("get") || sub.equals("delete")) {
            if (args.length == 2) {
                result.addAll(plugin.getCachedCoordNames(player.getUniqueId()));
            }
        }

        return result;
    }
}
