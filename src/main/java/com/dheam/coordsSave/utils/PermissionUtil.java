package com.dheam.coordsSave.utils;

import com.dheam.coordsSave.config.ConfigManager;
import org.bukkit.entity.Player;

import java.util.Map;

public class PermissionUtil {

    public static int getMaxCoords(Player player, ConfigManager config) {
        int max = config.getMaxCoordsDefault();

        for (Map.Entry<String, Integer> entry : config.getMaxCoordsPermissions().entrySet()) {
            String perm = entry.getKey();
            int value = entry.getValue();

            if (player.hasPermission(perm)) {
                if (value == -1) {
                    // Unlimited
                    return -1;
                }
                if (max == -1) {
                    // Already unlimited
                    continue;
                }
                if (value > max) {
                    max = value;
                }
            }
        }

        return max;
    }
}
