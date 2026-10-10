package com.recipeplatform.dao;

import com.recipeplatform.model.Recipe;
import java.sql.SQLException;
import java.util.List;

/** Database operations for recipes. */
public interface RecipeDao {

    /** Approved recipes, newest first. A blank search returns all of them. */
    List<Recipe> findApproved(String search) throws SQLException;

    /** Every recipe written by one user, whatever its status. */
    List<Recipe> findByUser(int userId) throws SQLException;

    /** One recipe by id, or null if it does not exist. */
    Recipe findById(int id) throws SQLException;

    /** Saves a new recipe with status PENDING and returns its id. */
    int create(String title, String ingredients, String instructions, int userId) throws SQLException;

    /** Number of approved recipes. */
    int countApproved() throws SQLException;
}
