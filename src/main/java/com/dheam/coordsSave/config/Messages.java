package com.dheam.coordsSave.config;

import com.dheam.coordsSave.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Messages {

    private final Main plugin;
    private File file;
    private FileConfiguration config;

    public Messages(Main plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
    }

    public String getRaw(String path) {
        return config.getString(path, "&cMissing message: " + path);
    }

    public String get(String path, Map<String, String> placeholders) {
        String msg = getRaw(path);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace(entry.getKey(), entry.getValue());
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        String msg = get(path, placeholders);
        if (msg == null || msg.isEmpty()) return;

        for (String line : msg.split("\n")) {
            sender.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
        }
    }

    public void sendClickableCoords(CommandSender sender,
                                    String path,
                                    Map<String, String> placeholders,
                                    String xText,
                                    String yText,
                                    String zText) {

        if (!(sender instanceof Player)) {
            Map<String, String> plain = new HashMap<>();
            if (placeholders != null) plain.putAll(placeholders);
            plain.put("%x%", xText);
            plain.put("%y%", yText);
            plain.put("%z%", zText);
            send(sender, path, plain);
            return;
        }

        Player player = (Player) sender;

        String raw = getRaw(path);
        if (raw == null || raw.isEmpty()) return;

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                if (entry.getKey().equals("%x%") || entry.getKey().equals("%y%") || entry.getKey().equals("%z%"))
                    continue;
                raw = raw.replace(entry.getKey(), entry.getValue());
            }
        }

        int idxX = raw.indexOf("%x%");
        int idxY = raw.indexOf("%y%");
        int idxZ = raw.indexOf("%z%");

        boolean patternFound = idxX != -1 && idxY != -1 && idxZ != -1 && idxX < idxY && idxY < idxZ;

        if (!patternFound) {
            Map<String, String> plain = new HashMap<>();
            if (placeholders != null) plain.putAll(placeholders);
            plain.put("%x%", xText);
            plain.put("%y%", yText);
            plain.put("%z%", zText);
            send(sender, path, plain);
            return;
        }

        String before = raw.substring(0, idxX);
        String after = raw.substring(idxZ + 3);

        TextComponent base = new TextComponent("");

        BaseComponent[] beforeComps =
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', before));
        addComponents(base, beforeComps);

        String coordsText = xText + " " + yText + " " + zText;

        TextComponent coordsComp = new TextComponent(coordsText);
        coordsComp.setColor(ChatColor.YELLOW);

        coordsComp.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                coordsText
        ));

        coordsComp.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(ChatColor.GOLD + "Click para copiar coords").create()
        ));

        base.addExtra(coordsComp);

        BaseComponent[] afterComps =
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', after));
        addComponents(base, afterComps);

        player.spigot().sendMessage(base);
    }

    private void addComponents(TextComponent parent, BaseComponent[] components) {
        for (BaseComponent comp : components) {
            parent.addExtra(comp);
        }
    }
}
