// FILE:
// app > java > com.example.cookai > MainActivity.java

package com.example.cookai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // =========================
    // DATABASE
    // =========================
    private RecipeDatabase recipeDatabase;

    // =========================
    // VIEW
    // =========================
    private SearchView searchView;
    private Spinner filterCategorySpinner;
    private RecyclerView recyclerView;
    private FloatingActionButton addRecipeFab;
    private BottomNavigationView bottomNavigation;

    // =========================
    // DATA
    // =========================
    private RecipeAdapter adapter;
    private List<Recipe> recipes;
    private List<Recipe> allRecipes;

    // =========================
    // FIREBASE
    // =========================
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================
        // LAYOUT
        // =========================
        setContentView(R.layout.activity_main);

        // =========================
        // INIT DATABASE
        // =========================
        recipeDatabase = RecipeDatabase.getInstance(this);

        // =========================
        // INIT FIREBASE
        // =========================
        db = FirebaseFirestore.getInstance();

        // =========================
        // VIEW BINDING
        // =========================
        searchView = findViewById(R.id.searchView);
        filterCategorySpinner = findViewById(R.id.filterCategorySpinner);
        recyclerView = findViewById(R.id.recipeRecyclerView);
        addRecipeFab = findViewById(R.id.addRecipeFab);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // =========================
        // RECYCLER VIEW
        // =========================
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipes = new ArrayList<>();
        allRecipes = new ArrayList<>();

        adapter = new RecipeAdapter(recipes);
        recyclerView.setAdapter(adapter);

        // =========================
        // LOAD RECIPES
        // =========================
        loadRecipes();

        // =========================
        // ADD RECIPE
        // =========================
        addRecipeFab.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddRecipeActivity.class
            );

            startActivity(intent);
        });

        // =========================
        // SEARCH
        // =========================
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(
                            String query
                    ) {
                        filterRecipes(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(
                            String newText
                    ) {
                        filterRecipes(newText);
                        return true;
                    }
                });

        // =========================
        // CATEGORY SPINNER
        // =========================
        setupCategorySpinner();

        // =========================
        // BOTTOM NAVIGATION
        // =========================
        setupBottomNavigation();
    }

    // =========================
    // AUTO REFRESH
    // =========================
    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    // =========================
    // CATEGORY SETUP
    // =========================
    private void setupCategorySpinner() {

        String[] categories = {
                "Semua",
                "Sarapan",
                "Makan Siang",
                "Makan Malam",
                "Dessert",
                "Diet",
                "Minuman"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        filterCategorySpinner.setAdapter(spinnerAdapter);

        filterCategorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        String selectedCategory =
                                parent.getItemAtPosition(position)
                                        .toString();

                        if (selectedCategory.equals("Semua")) {

                            loadRecipes();

                        } else {

                            filterRecipesByCategory(
                                    selectedCategory
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                });
    }

    // =========================
    // BOTTOM NAVIGATION SETUP
    // =========================
    // DI MainActivity.java
// GANTI METHOD setupBottomNavigation() DENGAN INI:

    private void setupBottomNavigation() {

        bottomNavigation.setSelectedItemId(
                R.id.nav_home
        );

        bottomNavigation.setOnItemSelectedListener(
                item -> {

                    int id = item.getItemId();

                    if (id == R.id.nav_home) {

                        return true;

                    } else if (id == R.id.nav_favorite) {

                        Intent intent = new Intent(
                                MainActivity.this,
                                FavoriteActivity.class
                        );

                        startActivity(intent);

                        // animasi smooth
                        overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                        );

                        return true;

                    } else if (id == R.id.nav_ai) {

                        Intent intent = new Intent(
                                MainActivity.this,
                                AiRecipeActivity.class
                        );

                        startActivity(intent);

                        overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                        );

                        return true;

                    } else if (id == R.id.nav_profile) {

                        Toast.makeText(
                                MainActivity.this,
                                "Profile coming soon",
                                Toast.LENGTH_SHORT
                        ).show();

                        return true;
                    }

                    return false;
                });
    }

    // =========================
    // LOAD SQLITE RECIPES
    // =========================
    private void loadRecipes() {

        recipes.clear();
        allRecipes.clear();

        List<RecipeEntity> localRecipes =
                recipeDatabase.recipeDao().getAllRecipes();

        for (RecipeEntity entity : localRecipes) {

            Recipe recipe = new Recipe(
                    entity.title,
                    entity.description,
                    entity.ingredients,
                    entity.steps,
                    entity.imagePath,
                    entity.category
            );

            recipes.add(recipe);
            allRecipes.add(recipe);
        }

        adapter.notifyDataSetChanged();

        if (recipes.isEmpty()) {

            Toast.makeText(
                    this,
                    "Belum ada resep, yuk tambah!",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================
    // SEARCH FILTER
    // =========================
    private void filterRecipes(String query) {

        recipes.clear();

        for (Recipe recipe : allRecipes) {

            if (recipe.getTitle() != null
                    && recipe.getTitle()
                    .toLowerCase()
                    .contains(
                            query.toLowerCase()
                    )) {

                recipes.add(recipe);
            }
        }

        adapter.notifyDataSetChanged();
    }

    // =========================
    // FIREBASE CATEGORY FILTER
    // =========================
    private void filterRecipesByCategory(
            String selectedCategory
    ) {

        db.collection("recipes")
                .whereEqualTo(
                        "category",
                        selectedCategory
                )
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        recipes.clear();

                        for (QueryDocumentSnapshot document :
                                task.getResult()) {

                            String title =
                                    document.getString("title");

                            String description =
                                    document.getString("description");

                            String ingredients =
                                    document.getString("ingredients");

                            String steps =
                                    document.getString("steps");

                            String imageUrl =
                                    document.getString("imageUrl");

                            String recipeCategory =
                                    document.getString("category");

                            recipes.add(
                                    new Recipe(
                                            title,
                                            description,
                                            ingredients,
                                            steps,
                                            imageUrl,
                                            recipeCategory
                                    )
                            );
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}