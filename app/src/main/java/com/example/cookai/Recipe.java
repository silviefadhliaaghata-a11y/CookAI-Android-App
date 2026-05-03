package com.example.cookai;

public class Recipe {

    // =========================
    // DATA
    // =========================
    private String title;
    private String description;
    private String ingredients;
    private String steps;
    private String imageUrl;
    private String category;

    // =========================
    // EMPTY CONSTRUCTOR
    // =========================
    public Recipe() {
    }

    // =========================
    // WITHOUT CATEGORY
    // =========================
    public Recipe(String title,
                  String description,
                  String ingredients,
                  String steps,
                  String imageUrl) {

        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
    }

    // =========================
    // FULL CONSTRUCTOR
    // =========================
    public Recipe(String title,
                  String description,
                  String ingredients,
                  String steps,
                  String imageUrl,
                  String category) {

        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // =========================
    // GETTERS
    // =========================
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }
}