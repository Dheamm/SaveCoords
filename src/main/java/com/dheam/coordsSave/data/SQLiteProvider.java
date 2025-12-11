package com.dheam.coordsSave.data;

import com.dheam.coordsSave.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteProvider implements DatabaseManager.DatabaseProvider {

    private final String url;

    public SQLiteProvider(Main plugin) {
        File dbFile = new File(plugin.getDataFolder(), "savecoords.db");
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        this.url = "jdbc:sqlite:" + dbFile.getPath();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public void initTables() throws SQLException {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS savecoords (" +
                            "player_uuid TEXT NOT NULL," +
                            "name TEXT NOT NULL," +
                            "world TEXT NOT NULL," +
                            "x DOUBLE NOT NULL," +
                            "y DOUBLE NOT NULL," +
                            "z DOUBLE NOT NULL," +
                            "created_at LONG NOT NULL," +
                            "PRIMARY KEY (player_uuid, name)" +
                            ");"
            );
        }
    }

    @Override
    public void close() {
        // No action needed for SQLite
    }
}
