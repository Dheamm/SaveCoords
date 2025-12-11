package com.dheam.coordsSave.data;

import com.dheam.coordsSave.model.SavedCoord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoordStorage {

    private final DatabaseManager manager;

    public CoordStorage(DatabaseManager manager) {
        this.manager = manager;
    }

    public boolean saveCoord(SavedCoord coord) {
        String insertSql = "INSERT INTO savecoords (player_uuid, name, world, x, y, z, created_at) VALUES (?,?,?,?,?,?,?)";
        String updateSql = "UPDATE savecoords SET world=?, x=?, y=?, z=?, created_at=? WHERE player_uuid=? AND name=?";

        try (Connection conn = manager.getProvider().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, coord.getPlayerUuid().toString());
                ps.setString(2, coord.getName());
                ps.setString(3, coord.getWorld());
                ps.setDouble(4, coord.getX());
                ps.setDouble(5, coord.getY());
                ps.setDouble(6, coord.getZ());
                ps.setLong(7, coord.getCreatedAt());
                ps.executeUpdate();
                return true;
            } catch (SQLException ex) {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, coord.getWorld());
                    ps.setDouble(2, coord.getX());
                    ps.setDouble(3, coord.getY());
                    ps.setDouble(4, coord.getZ());
                    ps.setLong(5, coord.getCreatedAt());
                    ps.setString(6, coord.getPlayerUuid().toString());
                    ps.setString(7, coord.getName());
                    ps.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public SavedCoord getCoord(UUID uuid, String name) {
        String sql = "SELECT world, x, y, z, created_at FROM savecoords WHERE player_uuid=? AND name=?";

        try (Connection conn = manager.getProvider().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String world = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    long createdAt = rs.getLong("created_at");
                    return new SavedCoord(uuid, name, world, x, y, z, createdAt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<SavedCoord> getCoords(UUID uuid) {
        List<SavedCoord> list = new ArrayList<>();
        String sql = "SELECT name, world, x, y, z, created_at FROM savecoords WHERE player_uuid=? ORDER BY created_at ASC";

        try (Connection conn = manager.getProvider().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String world = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    long createdAt = rs.getLong("created_at");

                    list.add(new SavedCoord(uuid, name, world, x, y, z, createdAt));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countCoords(UUID uuid) {
        String sql = "SELECT COUNT(*) FROM savecoords WHERE player_uuid=?";
        try (Connection conn = manager.getProvider().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean deleteCoord(UUID uuid, String name) {
        String sql = "DELETE FROM savecoords WHERE player_uuid=? AND name=?";
        try (Connection conn = manager.getProvider().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsName(UUID uuid, String name) {
        String sql = "SELECT 1 FROM savecoords WHERE player_uuid=? AND name=? LIMIT 1";
        try (Connection conn = manager.getProvider().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
