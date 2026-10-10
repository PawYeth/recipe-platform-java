package com.recipeplatform.dao;

import com.recipeplatform.model.User;
import java.sql.SQLException;

/** Database operations for users. Servlets depend on this interface, not on JDBC. */
public interface UserDao {

    /** Returns the user with this email, or null if there is none. */
    User findByEmail(String email) throws SQLException;

    /** Returns the user with this id, or null if there is none. */
    User findById(int id) throws SQLException;

    /** Inserts a new user and returns the generated id. */
    int create(String name, String email, String passwordHash, String role) throws SQLException;

    /** Number of registered users. */
    int count() throws SQLException;
}
