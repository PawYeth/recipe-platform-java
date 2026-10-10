package com.recipeplatform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connectivity (JDBC). Every DAO gets its connections from here,
 * so the database location is configured in exactly one place.
 */
public final class DBUtil {

    /** SQLite keeps the whole database in the file recipe.db in the project folder. */
    private static final String URL = "jdbc:sqlite:recipe.db";

    static {
        try {
            // Loads the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SQLite JDBC driver not found on the classpath", e);
        }
    }

    private DBUtil() {
    }

    /** Opens a new connection. Callers close it with try-with-resources. */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        // SQLite ignores foreign keys unless this is switched on for each connection
        try (Statement st = connection.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }
}
