package com.example.cookai;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class RecipeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public String ingredients;
    public String steps;
    public String category;
    public String imagePath;

    public RecipeEntity(String title,
                        String description,
                        String ingredients,
                        String steps,
                        String category,
                        String imagePath) {

        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.category = category;
        this.imagePath = imagePath;
    }
}