package com.example.cookai;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDao {

    @Insert
    void insertRecipe(RecipeEntity recipe);

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    List<RecipeEntity> getAllRecipes();

    @Query("DELETE FROM recipes")
    void deleteAllRecipes();
}