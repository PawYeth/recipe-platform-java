package com.recipeplatform.model;

/** A registered person. Maps to one row of the "users" table. */
public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;      // "USER" or "ADMIN"
    private String createdAt;

    public User() {
    }

    public User(int id, String name, String email, String passwordHash, String role, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** True when this person has administrator rights. */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
