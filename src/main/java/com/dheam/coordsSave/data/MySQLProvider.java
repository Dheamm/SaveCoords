package com.dheam.coordsSave.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLProvider implements DatabaseManager.DatabaseProvider {

    private final String url;
    private final String user;
    private final String password;

    public MySQLProvider(String host, int port, String database, String user, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&characterEncoding=utf8";
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void initTables() throws SQLException {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS savecoords (" +
                            "player_uuid VARCHAR(36) NOT NULL," +
                            "name VARCHAR(100) NOT NULL," +
                            "world VARCHAR(100) NOT NULL," +
                            "x DOUBLE NOT NULL," +
                            "y DOUBLE NOT NULL," +
                            "z DOUBLE NOT NULL," +
                            "created_at BIGINT NOT NULL," +
                            "PRIMARY KEY (player_uuid, name)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );
        }
    }

    @Override
    public void close() {
        // Conexiones por operación
    }
}
