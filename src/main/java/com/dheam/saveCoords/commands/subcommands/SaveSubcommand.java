package com.dheam.saveCoords.commands.subcommands;

import com.cjcrafter.foliascheduler.ServerImplementation;
import com.dheam.saveCoords.Main;
import com.dheam.saveCoords.commands.CoordsCommand;
import com.dheam.saveCoords.config.ConfigManager;
import com.dheam.saveCoords.config.Messages;
import com.dheam.saveCoords.data.CoordStorage;
import com.dheam.saveCoords.model.SavedCoord;
import com.dheam.saveCoords.utils.NameGenerator;
import com.dheam.saveCoords.utils.PermissionUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveSubcommand implements CoordsCommand.Subcommand {

    private final Main plugin;
    private final CoordStorage storage;
    private final Messages messages;
    private final ConfigManager configManager;
    private final NameGenerator nameGenerator;

    public SaveSubcommand(Main plugin,
                          CoordStorage storage,
                          Messages messages,
                          ConfigManager configManager,
                          NameGenerator nameGenerator) {
        this.plugin = plugin;
        this.storage = storage;
        this.messages = messages;
        this.configManager = configManager;
        this.nameGenerator = nameGenerator;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        ServerImplementation scheduler = plugin.getScheduler();

        int limit = PermissionUtil.getMaxCoords(player, configManager);

        Location baseLoc = player.getLocation();
        double x;
        double y;
        double z;

        int index = 0;

        if (args.length >= 3 && isNumber(args[0]) && isNumber(args[1]) && isNumber(args[2])) {
            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);
            index = 3;
        } else {
            x = baseLoc.getBlockX();
            y = baseLoc.getBlockY();
            z = baseLoc.getBlockZ();
        }

        String world = baseLoc.getWorld().getName();

        String manualName = null;
        if (args.length > index) {
            StringBuilder sb = new StringBuilder();
            for (int i = index; i < args.length; i++) {
                if (i > index) sb.append(" ");
                sb.append(args[i]);
            }
            manualName = sb.toString();
        }

        double finalX = x;
        double finalY = y;
        double finalZ = z;
        String finalWorld = world;
        String finalManualName = manualName;
        int finalLimit = limit;
        String playerName = player.getName();

        scheduler.async().runNow(() -> {
            int used = storage.countCoords(uuid);

            if (finalLimit != -1 && used >= finalLimit) {
                int remaining = Math.max(0, finalLimit - used);

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%limit%", String.valueOf(finalLimit));
                placeholders.put("%used%", String.valueOf(used));
                placeholders.put("%remaining%", String.valueOf(remaining));

                scheduler.entity(player).run(() ->
                        messages.send(player, "limit_reached", placeholders)
                );
                return;
            }

            String name = finalManualName;
            if (name == null || name.trim().isEmpty()) {
                name = nameGenerator.generateName(uuid, used + 1);
            }

            long now = System.currentTimeMillis();
            SavedCoord coord = new SavedCoord(uuid, name, finalWorld, finalX, finalY, finalZ, now);

            boolean success = storage.saveCoord(coord);

            int newUsed = success ? used + 1 : used;
            int remaining = (finalLimit == -1) ? -1 : Math.max(0, finalLimit - newUsed);

            if (success) {
                plugin.addCoordNameToCache(uuid, name);
            }

            String xText = String.valueOf((int) Math.round(finalX));
            String yText = String.valueOf((int) Math.round(finalY));
            String zText = String.valueOf((int) Math.round(finalZ));

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", playerName);
            placeholders.put("%name%", name);
            placeholders.put("%world%", finalWorld);
            placeholders.put("%limit%", String.valueOf(finalLimit));
            placeholders.put("%used%", String.valueOf(newUsed));
            placeholders.put("%remaining%", String.valueOf(remaining));

            if (!success) {
                scheduler.entity(player).run(() ->
                        messages.sendClickableCoords(player, "error_saving", placeholders, xText, yText, zText)
                );
            } else {
                scheduler.entity(player).run(() ->
                        messages.sendClickableCoords(player, "saved", placeholders, xText, yText, zText)
                );
            }
        });
    }

    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
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
