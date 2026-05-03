// FILE:
// app > java > com.example.cookai > AiRecipeActivity.java

package com.example.cookai;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AiRecipeActivity extends AppCompatActivity {

    private EditText ingredientsInput;
    private Button generateButton;
    private Button saveRecipeButton;
    private TextView resultText;
    private ProgressBar loadingBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_recipe);

        // =========================
        // HUBUNGKAN VIEW
        // =========================
        ingredientsInput = findViewById(R.id.ingredientsInput);
        generateButton = findViewById(R.id.generateButton);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);
        resultText = findViewById(R.id.resultText);
        loadingBar = findViewById(R.id.loadingBar);

        // =========================
        // FIREBASE
        // =========================
        db = FirebaseFirestore.getInstance();

        // =========================
        // GENERATE BUTTON
        // =========================
        generateButton.setOnClickListener(v -> {

            String ingredients = ingredientsInput.getText().toString().trim();

            if (ingredients.isEmpty()) {
                resultText.setText("Masukkan bahan terlebih dahulu.");
                return;
            }

            generateRecipe(ingredients);
        });

        // =========================
        // SAVE BUTTON
        // =========================
        saveRecipeButton.setOnClickListener(v -> saveRecipeToFirestore());
    }

    // =========================
    // AI GRATIS LOKAL
    // =========================
    private void generateRecipe(String ingredients) {

        loadingBar.setVisibility(View.VISIBLE);
        resultText.setText("");

        new Handler().postDelayed(() -> {

            loadingBar.setVisibility(View.GONE);

            String recipeName = "Kreasi " + ingredients;

            String recipe =
                    "🍽 Nama Resep:\n" +
                            recipeName + "\n\n" +

                            "🧂 Bahan:\n" +
                            "- " + ingredients + "\n" +
                            "- Garam\n" +
                            "- Minyak\n" +
                            "- Bawang putih\n" +
                            "- Merica\n\n" +

                            "👨‍🍳 Langkah Memasak:\n" +
                            "1. Siapkan semua bahan.\n" +
                            "2. Panaskan minyak di wajan.\n" +
                            "3. Tumis bawang putih hingga harum.\n" +
                            "4. Masukkan " + ingredients + ".\n" +
                            "5. Tambahkan garam dan merica.\n" +
                            "6. Aduk hingga matang.\n" +
                            "7. Sajikan selagi hangat.\n\n" +

                            "💡 Tips AI:\n" +
                            "Tambahkan saus atau sayuran agar lebih lezat.";

            resultText.setText(recipe);

        }, 1500);
    }

    // =========================
    // SIMPAN RESEP KE FIRESTORE
    // =========================
    private void saveRecipeToFirestore() {

        String ingredients = ingredientsInput.getText().toString().trim();
        String generatedRecipe = resultText.getText().toString().trim();

        if (ingredients.isEmpty() || generatedRecipe.isEmpty()) {
            Toast.makeText(this,
                    "Generate resep terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> recipe = new HashMap<>();

        recipe.put("title", "AI Recipe: " + ingredients);
        recipe.put("description", "Resep AI otomatis");
        recipe.put("ingredients", ingredients);
        recipe.put("steps", generatedRecipe);
        recipe.put("timestamp", System.currentTimeMillis());

        db.collection("recipes")
                .add(recipe)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this,
                                "Resep berhasil disimpan",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Gagal menyimpan resep",
                                Toast.LENGTH_SHORT).show());
    }
}