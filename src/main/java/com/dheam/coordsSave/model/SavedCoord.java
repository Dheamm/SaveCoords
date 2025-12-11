package com.dheam.coordsSave.model;

import java.util.UUID;

public class SavedCoord {

    private final UUID playerUuid;
    private final String name;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final long createdAt;

    public SavedCoord(UUID playerUuid, String name, String world, double x, double y, double z, long createdAt) {
        this.playerUuid = playerUuid;
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createdAt = createdAt;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
