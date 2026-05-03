package com.example.cookai;

public class Comment {

    private String recipeTitle;
    private String userId;
    private String comment;
    private float rating;

    public Comment() {
    }

    public Comment(String recipeTitle, String userId,
                   String comment, float rating) {
        this.recipeTitle = recipeTitle;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public String getUserId() {
        return userId;
    }

    public String getComment() {
        return comment;
    }

    public float getRating() {
        return rating;
    }
}