package com.recipeplatform.model;

/**
 * A shared recipe. Maps to one row of the "recipes" table, plus two values
 * that are calculated when it is loaded: the author's name and the rating summary.
 */
public class Recipe {
    private int id;
    private String title;
    private String ingredients;   // one ingredient per line
    private String instructions;
    private String status;        // PENDING, APPROVED or REJECTED
    private int userId;           // id of the author
    private String authorName;    // joined from the users table
    private String createdAt;
    private double avgRating;     // average of all star ratings (0 when none)
    private int ratingCount;      // how many ratings the recipe has

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
}
