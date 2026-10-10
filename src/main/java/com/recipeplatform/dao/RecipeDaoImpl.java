package com.recipeplatform.dao;

import com.recipeplatform.model.Recipe;
import com.recipeplatform.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** JDBC implementation of RecipeDao. */
public class RecipeDaoImpl implements RecipeDao {

    /** Shared SELECT: recipe columns + author name + rating summary calculated with sub-queries. */
    private static final String BASE_SELECT =
            "SELECT r.id AS id, r.title AS title, r.ingredients AS ingredients, "
                    + "r.instructions AS instructions, r.status AS status, r.user_id AS user_id, "
                    + "r.created_at AS created_at, u.name AS author, "
                    + "COALESCE((SELECT AVG(stars) FROM ratings WHERE recipe_id = r.id), 0) AS avg_rating, "
                    + "(SELECT COUNT(*) FROM ratings WHERE recipe_id = r.id) AS rating_count "
                    + "FROM recipes r JOIN users u ON u.id = r.user_id";

    private static final String NEWEST_FIRST = " ORDER BY r.created_at DESC, r.id DESC";

    @Override
    public List<Recipe> findApproved(String search) throws SQLException {
        boolean hasSearch = search != null && !search.isBlank();
        String sql = BASE_SELECT + " WHERE r.status = 'APPROVED'"
                + (hasSearch ? " AND (LOWER(r.title) LIKE ? OR LOWER(r.ingredients) LIKE ?)" : "")
                + NEWEST_FIRST;
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (hasSearch) {
                String like = "%" + search.trim().toLowerCase() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    @Override
    public List<Recipe> findByUser(int userId) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.user_id = ?" + NEWEST_FIRST;
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    @Override
    public Recipe findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public int create(String title, String ingredients, String instructions, int userId) throws SQLException {
        String sql = "INSERT INTO recipes (title, ingredients, instructions, status, user_id) "
                + "VALUES (?, ?, ?, 'PENDING', ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, ingredients);
            ps.setString(3, instructions);
            ps.setInt(4, userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    @Override
    public int countApproved() throws SQLException {
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM recipes WHERE status = 'APPROVED'")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private List<Recipe> mapAll(ResultSet rs) throws SQLException {
        List<Recipe> list = new ArrayList<>();
        while (rs.next()) {
            list.add(map(rs));
        }
        return list;
    }

    /** Converts the current row of a result set into a Recipe object. */
    private Recipe map(ResultSet rs) throws SQLException {
        Recipe r = new Recipe();
        r.setId(rs.getInt("id"));
        r.setTitle(rs.getString("title"));
        r.setIngredients(rs.getString("ingredients"));
        r.setInstructions(rs.getString("instructions"));
        r.setStatus(rs.getString("status"));
        r.setUserId(rs.getInt("user_id"));
        r.setCreatedAt(rs.getString("created_at"));
        r.setAuthorName(rs.getString("author"));
        r.setAvgRating(rs.getDouble("avg_rating"));
        r.setRatingCount(rs.getInt("rating_count"));
        return r;
    }
}
