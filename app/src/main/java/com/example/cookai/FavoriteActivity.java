// FILE:
// app > java > com.example.cookai > FavoriteActivity.java

package com.example.cookai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    // =========================
    // VIEW
    // =========================
    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private TextView emptyFavoriteText;
    private BottomNavigationView bottomNavigation;

    // =========================
    // DATA
    // =========================
    private List<Recipe> favoriteRecipes;

    // =========================
    // FIREBASE
    // =========================
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================
        // LAYOUT
        // =========================
        setContentView(R.layout.activity_favorite);

        // =========================
        // VIEW BINDING
        // =========================
        recyclerView = findViewById(R.id.favoriteRecyclerView);
        emptyFavoriteText = findViewById(R.id.emptyFavoriteText);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // =========================
        // RECYCLER VIEW
        // =========================
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // =========================
        // DATA
        // =========================
        favoriteRecipes = new ArrayList<>();

        // =========================
        // ADAPTER
        // =========================
        adapter = new FavoriteAdapter(
                this,
                favoriteRecipes
        );

        recyclerView.setAdapter(adapter);

        // =========================
        // FIREBASE
        // =========================
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // =========================
        // LOAD FAVORITES
        // =========================
        loadFavorites();

        // =========================
        // NAVIGATION
        // =========================
        setupBottomNavigation();
    }

    // =========================
    // AUTO REFRESH
    // =========================
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    // =========================
    // BOTTOM NAVIGATION
    // =========================
    private void setupBottomNavigation() {

        bottomNavigation.setSelectedItemId(
                R.id.nav_favorite
        );

        bottomNavigation.setOnItemSelectedListener(
                item -> {

                    int id = item.getItemId();

                    if (id == R.id.nav_home) {

                        startActivity(
                                new Intent(
                                        FavoriteActivity.this,
                                        MainActivity.class
                                )
                        );

                        finish();
                        return true;

                    } else if (id == R.id.nav_favorite) {

                        return true;

                    } else if (id == R.id.nav_ai) {

                        startActivity(
                                new Intent(
                                        FavoriteActivity.this,
                                        AiRecipeActivity.class
                                )
                        );

                        return true;

                    } else if (id == R.id.nav_profile) {

                        Toast.makeText(
                                this,
                                "Profile coming soon",
                                Toast.LENGTH_SHORT
                        ).show();

                        return true;
                    }

                    return false;
                });
    }

    // =========================
    // LOAD FAVORITES
    // =========================
    private void loadFavorites() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "User belum login",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("favorites")
                .whereEqualTo(
                        "userId",
                        auth.getCurrentUser().getUid()
                )
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        favoriteRecipes.clear();

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

                            String category =
                                    document.getString("category");

                            favoriteRecipes.add(
                                    new Recipe(
                                            title,
                                            description,
                                            ingredients,
                                            steps,
                                            imageUrl,
                                            category
                                    )
                            );
                        }

                        // =========================
                        // EMPTY STATE
                        // =========================
                        if (favoriteRecipes.isEmpty()) {

                            emptyFavoriteText.setVisibility(
                                    View.VISIBLE
                            );

                        } else {

                            emptyFavoriteText.setVisibility(
                                    View.GONE
                            );
                        }

                        adapter.notifyDataSetChanged();

                    } else {

                        Toast.makeText(
                                FavoriteActivity.this,
                                "Gagal memuat favorit",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}