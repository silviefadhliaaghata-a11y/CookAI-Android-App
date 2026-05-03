// FILE:
// app > java > com.example.cookai > AddRecipeActivity.java

package com.example.cookai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity {

    private RecipeDatabase recipeDatabase;
    // =========================
    // CONSTANT
    // =========================
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;

    // =========================
    // VIEW
    // =========================
    private EditText titleInput;
    private EditText descriptionInput;
    private EditText ingredientsInput;
    private EditText stepsInput;

    private Button saveRecipeButton;
    private Button selectImageButton;

    private ImageView recipeImageView;
    private Spinner categorySpinner;

    // =========================
    // FIREBASE
    // =========================
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    // =========================
    // DATA
    // =========================
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        recipeDatabase = RecipeDatabase.getInstance(this);

        // =========================
        // FIREBASE INIT
        // =========================
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance("gs://cookai-be201.appspot.com");

        // =========================
        // HUBUNGKAN VIEW
        // =========================
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        ingredientsInput = findViewById(R.id.ingredientsInput);
        stepsInput = findViewById(R.id.stepsInput);

        saveRecipeButton = findViewById(R.id.saveRecipeButton);
        selectImageButton = findViewById(R.id.selectImageButton);

        recipeImageView = findViewById(R.id.recipeImageView);
        categorySpinner = findViewById(R.id.categorySpinner);

        // =========================
        // CATEGORY SPINNER
        // =========================
        ArrayAdapter<CharSequence> adapterCategory =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.recipe_categories,
                        android.R.layout.simple_spinner_item
                );

        adapterCategory.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        categorySpinner.setAdapter(adapterCategory);

        // =========================
        // BUTTON LISTENER
        // =========================
        selectImageButton.setOnClickListener(v -> openGallery());

        saveRecipeButton.setOnClickListener(v -> saveRecipe());
    }

    // =========================
    // OPEN GALLERY
    // =========================
    private void openGallery() {

        String permission = Build.VERSION.SDK_INT >= 33
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(
                this,
                permission
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    STORAGE_PERMISSION_CODE
            );

            return;
        }

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // =========================
    // PERMISSION RESULT
    // =========================
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openGallery();

            } else {

                Toast.makeText(
                        this,
                        "Izin galeri ditolak",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // =========================
    // IMAGE PICK RESULT
    // =========================
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            imageUri = data.getData();
            recipeImageView.setImageURI(imageUri);
        }
    }

    // =========================
    // SAVE RECIPE
    // =========================
    private void saveRecipe() {

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String ingredients = ingredientsInput.getText().toString().trim();
        String steps = stepsInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (title.isEmpty()
                || description.isEmpty()
                || ingredients.isEmpty()
                || steps.isEmpty()) {

            Toast.makeText(
                    this,
                    "Semua field wajib diisi",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String imagePath = imageUri != null
                ? imageUri.toString()
                : "";

        RecipeEntity recipe = new RecipeEntity(
                title,
                description,
                ingredients,
                steps,
                category,
                imagePath
        );

        recipeDatabase.recipeDao().insertRecipe(recipe);

        Toast.makeText(
                this,
                "Resep berhasil disimpan",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }


    // =========================
    // UPLOAD IMAGE
    // =========================
    private void uploadImageAndSaveRecipe(
            String title,
            String description,
            String ingredients,
            String steps,
            String category
    ) {

        StorageReference storageRef =
                storage.getReference()
                        .child(
                                "recipe_images/"
                                        + UUID.randomUUID().toString()
                        );

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        saveRecipeToFirestore(
                                                title,
                                                description,
                                                ingredients,
                                                steps,
                                                category,
                                                uri.toString()
                                        )))
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Upload gambar gagal : " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show());
    }

    // =========================
    // SAVE TO FIRESTORE
    // =========================
    private void saveRecipeToFirestore(
            String title,
            String description,
            String ingredients,
            String steps,
            String category,
            String imageUrl
    ) {

        HashMap<String, Object> recipe = new HashMap<>();

        recipe.put("title", title);
        recipe.put("description", description);
        recipe.put("ingredients", ingredients);
        recipe.put("steps", steps);
        recipe.put("category", category);
        recipe.put("imageUrl", imageUrl);
        recipe.put("timestamp", System.currentTimeMillis());

        if (auth.getCurrentUser() != null) {
            recipe.put(
                    "authorId",
                    auth.getCurrentUser().getUid()
            );
        }

        db.collection("recipes")
                .add(recipe)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "✅ Resep berhasil ditambahkan!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Gagal menambahkan resep",
                                Toast.LENGTH_SHORT
                        ).show());
    }
}