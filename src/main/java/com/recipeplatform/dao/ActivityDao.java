package com.recipeplatform.dao;

import com.recipeplatform.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Writes entries to the user_activity table (SIGNUP, LOGIN, RECIPE_SUBMITTED). */
public class ActivityDao {

    /** Logging must never break the main action, so errors are only printed. */
    public void log(int userId, String action, String details) {
        String sql = "INSERT INTO user_activity (user_id, action, details) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not write activity log: " + e.getMessage());
        }
    }
}
