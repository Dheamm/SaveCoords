package com.dheam.saveCoords.commands.subcommands;

import com.cjcrafter.foliascheduler.ServerImplementation;
import com.dheam.saveCoords.Main;
import com.dheam.saveCoords.commands.CoordsCommand;
import com.dheam.saveCoords.config.Messages;
import com.dheam.saveCoords.data.CoordStorage;
import com.dheam.saveCoords.model.SavedCoord;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
        Player viewer = (Player) sender;

        UUID targetUuid = viewer.getUniqueId();
        String targetName = viewer.getName();
        boolean viewingOthers = false;

        if (args.length >= 1) {
            if (!sender.hasPermission("savecoords.view.others")) {
                messages.send(sender, "no_permission", null);
                return;
            }

            String targetArg = args[0];

            Player online = plugin.getServer().getPlayerExact(targetArg);
            if (online != null) {
                targetUuid = online.getUniqueId();
                targetName = online.getName();
            } else {
                OfflinePlayer offline = plugin.getServer().getOfflinePlayer(targetArg);

                if (!offline.hasPlayedBefore() && !offline.isOnline()) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("%player%", targetArg);
                    messages.send(sender, "player_not_found", placeholders);
                    return;
                }

                targetUuid = offline.getUniqueId();
                targetName = (offline.getName() != null) ? offline.getName() : targetArg;
            }

            viewingOthers = !targetUuid.equals(viewer.getUniqueId());
        }

        final UUID finalTargetUuid = targetUuid;
        final String finalTargetName = targetName;
        final boolean finalViewingOthers = viewingOthers;
        final Player finalViewer = viewer;

        ServerImplementation scheduler = plugin.getScheduler();

        scheduler.async().runNow(() -> {
            List<SavedCoord> coords = storage.getCoords(finalTargetUuid);

            List<String> names = new ArrayList<>(coords.size());
            for (SavedCoord coord : coords) {
                names.add(coord.getName());
            }
            plugin.setCachedCoordNames(finalTargetUuid, names);

            if (coords.isEmpty()) {
                scheduler.entity(finalViewer).run(() -> {
                    if (finalViewingOthers) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("%player%", finalTargetName);
                        messages.send(finalViewer, "list_empty_other", placeholders);
                    } else {
                        messages.send(finalViewer, "list_empty", null);
                    }
                });
                return;
            }

            scheduler.entity(finalViewer).run(() -> {
                if (finalViewingOthers) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("%player%", finalTargetName);
                    messages.send(finalViewer, "list_header_other", placeholders);
                } else {
                    messages.send(finalViewer, "list_header", null);
                }
            });

            for (SavedCoord coord : coords) {
                String xText = String.valueOf((int) Math.round(coord.getX()));
                String yText = String.valueOf((int) Math.round(coord.getY()));
                String zText = String.valueOf((int) Math.round(coord.getZ()));

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%name%", coord.getName());
                placeholders.put("%world%", coord.getWorld());

                scheduler.entity(finalViewer).run(() ->
                        messages.sendClickableCoords(
                                finalViewer,
                                "list_format",
                                placeholders,
                                xText,
                                yText,
                                zText
                        )
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