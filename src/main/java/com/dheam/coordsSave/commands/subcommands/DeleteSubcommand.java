package com.dheam.coordsSave.commands.subcommands;

import com.cjcrafter.foliascheduler.ServerImplementation;
import com.dheam.coordsSave.Main;
import com.dheam.coordsSave.commands.CoordsCommand;
import com.dheam.coordsSave.config.Messages;
import com.dheam.coordsSave.data.CoordStorage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeleteSubcommand implements CoordsCommand.Subcommand {

    private final Main plugin;
    private final CoordStorage storage;
    private final Messages messages;

    public DeleteSubcommand(Main plugin, CoordStorage storage, Messages messages) {
        this.plugin = plugin;
        this.storage = storage;
        this.messages = messages;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            messages.send(sender, "usage_delete", null);
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String name = String.join(" ", args);

        ServerImplementation scheduler = plugin.getScheduler();

        scheduler.async().runNow(() -> {
            boolean deleted = storage.deleteCoord(uuid, name);

            if (!deleted) {
                scheduler.entity(player).run(() ->
                        messages.send(player, "not_found", null)
                );
                return;
            }

            plugin.removeCoordNameFromCache(uuid, name);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%name%", name);

            scheduler.entity(player).run(() ->
                    messages.send(player, "deleted", placeholders)
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
