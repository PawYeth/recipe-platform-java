package com.recipeplatform.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Runs once when the web application starts: creates the tables from schema.sql
 * and, on an empty database, inserts demo accounts and sample recipes.
 */
public class DbInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try (Connection c = DBUtil.getConnection()) {
            createTables(c);
            seedIfEmpty(c);
            System.out.println("Database ready (recipe.db).");
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Could not initialise the database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // nothing to clean up: connections are closed after every query
    }

    /** Reads schema.sql from the classpath and runs each statement. */
    private void createTables(Connection c) throws SQLException, IOException {
        try (InputStream in = DbInitListener.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new IOException("schema.sql was not found in src/main/resources");
            }
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement st = c.createStatement()) {
                for (String statement : script.split(";")) {
                    if (!statement.isBlank()) {
                        st.execute(statement);
                    }
                }
            }
        }
    }

    /** Adds demo data only when there are no users yet. */
    private void seedIfEmpty(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        int admin = insertUser(c, "Admin", "admin@recipe.com", "ADMIN");
        int user1 = insertUser(c, "Chef Mario", "user1@recipe.com", "USER");
        int user2 = insertUser(c, "Baker Julia", "user2@recipe.com", "USER");

        int pizza = insertRecipe(c, "Classic Margherita Pizza",
                "Pizza dough\nTomato sauce\nFresh mozzarella\nBasil leaves\nOlive oil\nSalt",
                "Preheat the oven to 250C.\nStretch the dough and spread the sauce.\n"
                        + "Add the mozzarella and bake for 10 minutes.\nTop with basil and olive oil.",
                "APPROVED", user1);
        int pasta = insertRecipe(c, "Creamy Mushroom Pasta",
                "Penne pasta\nMushrooms\nGarlic\nCream\nParmesan\nBlack pepper",
                "Boil the pasta until tender.\nFry the mushrooms and garlic in butter.\n"
                        + "Add the cream and parmesan, then toss with the pasta.",
                "APPROVED", user2);
        int paneer = insertRecipe(c, "Paneer Butter Masala",
                "Paneer cubes\nTomato puree\nButter\nCream\nGaram masala\nKasuri methi",
                "Simmer the tomato puree with butter and spices.\nAdd the cream and paneer.\n"
                        + "Cook for 5 minutes and finish with kasuri methi.",
                "APPROVED", user1);
        insertRecipe(c, "Masala Chai",
                "Water\nMilk\nTea leaves\nGinger\nCardamom\nSugar",
                "Boil the water with ginger and cardamom.\nAdd the tea leaves and milk.\nSimmer, sweeten and strain.",
                "APPROVED", user2);
        insertRecipe(c, "Chocolate Lava Cake",
                "Dark chocolate\nButter\nEggs\nSugar\nFlour",
                "Melt the chocolate with butter.\nWhisk in the eggs, sugar and flour.\nBake for 12 minutes.",
                "PENDING", user1);

        insertRating(c, pizza, user2, 5);
        insertRating(c, pizza, admin, 4);
        insertRating(c, pasta, user1, 4);
        insertRating(c, paneer, user2, 5);
        insertRating(c, paneer, admin, 5);

        try (Statement st = c.createStatement()) {
            st.execute("INSERT OR IGNORE INTO settings (setting_key, setting_value) VALUES ('allow_registrations', 'true')");
        }
        System.out.println("Demo data added: admin@recipe.com, user1@recipe.com, user2@recipe.com (password123)");
    }

    private int insertUser(Connection c, String name, String email, String role) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash("password123"));
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private int insertRecipe(Connection c, String title, String ingredients, String instructions,
                             String status, int userId) throws SQLException {
        String sql = "INSERT INTO recipes (title, ingredients, instructions, status, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, ingredients);
            ps.setString(3, instructions);
            ps.setString(4, status);
            ps.setInt(5, userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private void insertRating(Connection c, int recipeId, int userId, int stars) throws SQLException {
        String sql = "INSERT INTO ratings (recipe_id, user_id, stars) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, recipeId);
            ps.setInt(2, userId);
            ps.setInt(3, stars);
            ps.executeUpdate();
        }
    }
}
