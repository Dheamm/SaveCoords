package com.dheam.saveCoords.commands.subcommands;

import com.cjcrafter.foliascheduler.ServerImplementation;
import com.dheam.saveCoords.Main;
import com.dheam.saveCoords.commands.CoordsCommand;
import com.dheam.saveCoords.config.Messages;
import com.dheam.saveCoords.data.CoordStorage;
import com.dheam.saveCoords.model.SavedCoord;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GetSubcommand implements CoordsCommand.Subcommand {

    private final Main plugin;
    private final CoordStorage storage;
    private final Messages messages;

    public GetSubcommand(Main plugin, CoordStorage storage, Messages messages) {
        this.plugin = plugin;
        this.storage = storage;
        this.messages = messages;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            messages.send(sender, "usage_get", null);
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String name = String.join(" ", args);
        String playerName = player.getName();

        ServerImplementation scheduler = plugin.getScheduler();

        scheduler.async().runNow(() -> {
            SavedCoord coord = storage.getCoord(uuid, name);
            if (coord == null) {
                scheduler.entity(player).run(() ->
                        messages.send(player, "not_found", null)
                );
                return;
            }

            String xText = String.valueOf((int) Math.round(coord.getX()));
            String yText = String.valueOf((int) Math.round(coord.getY()));
            String zText = String.valueOf((int) Math.round(coord.getZ()));

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", playerName);
            placeholders.put("%name%", coord.getName());
            placeholders.put("%world%", coord.getWorld());

            scheduler.entity(player).run(() ->
                    messages.sendClickableCoords(player, "get_format", placeholders, xText, yText, zText)
            );
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
