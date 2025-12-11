package com.dheam.coordsSave.commands.subcommands;

import com.cjcrafter.foliascheduler.ServerImplementation;
import com.dheam.coordsSave.Main;
import com.dheam.coordsSave.commands.CoordsCommand;
import com.dheam.coordsSave.config.Messages;
import com.dheam.coordsSave.data.CoordStorage;
import com.dheam.coordsSave.model.SavedCoord;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListSubcommand implements CoordsCommand.Subcommand {

    private final Main plugin;
    private final CoordStorage storage;
    private final Messages messages;

    public ListSubcommand(Main plugin, CoordStorage storage, Messages messages) {
        this.plugin = plugin;
        this.storage = storage;
        this.messages = messages;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        ServerImplementation scheduler = plugin.getScheduler();

        scheduler.async().runNow(() -> {
            List<SavedCoord> coords = storage.getCoords(uuid);

            {
                List<String> names = new java.util.ArrayList<>();
                for (SavedCoord coord : coords) {
                    names.add(coord.getName());
                }
                plugin.setCachedCoordNames(uuid, names);
            }

            if (coords.isEmpty()) {
                scheduler.entity(player).run(() ->
                        messages.send(player, "list_empty", null)
                );
                return;
            }

            scheduler.entity(player).run(() ->
                    messages.send(player, "list_header", null)
            );

            for (SavedCoord coord : coords) {
                String xText = String.valueOf((int) Math.round(coord.getX()));
                String yText = String.valueOf((int) Math.round(coord.getY()));
                String zText = String.valueOf((int) Math.round(coord.getZ()));

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%name%", coord.getName());
                placeholders.put("%world%", coord.getWorld());

                scheduler.entity(player).run(() ->
                        messages.sendClickableCoords(player, "list_format", placeholders, xText, yText, zText)
                );
            }
        });
    }

    @Override
    public String getPermission() {
        return "savecoords.use";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
