package org.stepanchuk.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

class DBConnection {

    public static final String HIKARI_CONFIG_FILE = "/db.properties";
    private static DBConnection instance;
    private static HikariDataSource ds;

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    DBConnection() {
        ds = new HikariDataSource(new HikariConfig(HIKARI_CONFIG_FILE));
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}