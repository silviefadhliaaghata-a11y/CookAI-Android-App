// FILE:
// app > java > com.example.cookai > RecipeDetailActivity.java

package com.example.cookai;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    // =========================
    // FIREBASE
    // =========================
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // =========================
    // REVIEW
    // =========================
    private RatingBar ratingBar;
    private EditText commentInput;
    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // =========================
        // FIREBASE INIT
        // =========================
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // =========================
        // VIEW
        // =========================
        ImageView detailImage = findViewById(R.id.detailImage);

        TextView title = findViewById(R.id.detailTitle);
        TextView category = findViewById(R.id.detailCategory);
        TextView description = findViewById(R.id.detailDescription);
        TextView ingredients = findViewById(R.id.detailIngredients);
        TextView steps = findViewById(R.id.detailSteps);

        Button bookmarkButton = findViewById(R.id.bookmarkButton);
        Button removeBookmarkButton =
                findViewById(R.id.removeBookmarkButton);

        ratingBar = findViewById(R.id.ratingBar);
        commentInput = findViewById(R.id.commentInput);

        Button submitCommentButton =
                findViewById(R.id.submitCommentButton);

        commentRecyclerView =
                findViewById(R.id.commentRecyclerView);

        commentRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        // =========================
        // INTENT DATA
        // =========================
        String recipeTitle =
                getIntent().getStringExtra("title");

        String recipeDescription =
                getIntent().getStringExtra("description");

        String recipeIngredients =
                getIntent().getStringExtra("ingredients");

        String recipeSteps =
                getIntent().getStringExtra("steps");

        String imageUrl =
                getIntent().getStringExtra("imageUrl");

        String recipeCategory =
                getIntent().getStringExtra("category");

        // =========================
        // SET TEXT
        // =========================
        title.setText(recipeTitle);
        description.setText(recipeDescription);
        ingredients.setText(recipeIngredients);
        steps.setText(recipeSteps);

        if (recipeCategory != null) {
            category.setText("🍽 " + recipeCategory);
        } else {
            category.setText("");
        }

        // =========================
        // LOAD IMAGE
        // =========================
        if (imageUrl != null && !imageUrl.isEmpty()) {

            Glide.with(this)
                    .load(Uri.parse(imageUrl))
                    .into(detailImage);

        } else {

            detailImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }

        // =========================
        // BUTTONS
        // =========================
        bookmarkButton.setOnClickListener(
                v -> saveToFavorites()
        );

        removeBookmarkButton.setOnClickListener(
                v -> removeFromFavorites()
        );

        submitCommentButton.setOnClickListener(
                v -> submitComment()
        );

        // =========================
        // LOAD COMMENTS
        // =========================
        loadComments();
    }

    // =========================
    // SAVE FAVORITE
    // =========================
    private void saveToFavorites() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "User belum login",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        HashMap<String, Object> favorite =
                new HashMap<>();

        favorite.put(
                "userId",
                auth.getCurrentUser().getUid()
        );

        favorite.put(
                "title",
                getIntent().getStringExtra("title")
        );

        favorite.put(
                "description",
                getIntent().getStringExtra("description")
        );

        favorite.put(
                "ingredients",
                getIntent().getStringExtra("ingredients")
        );

        favorite.put(
                "steps",
                getIntent().getStringExtra("steps")
        );

        favorite.put(
                "imageUrl",
                getIntent().getStringExtra("imageUrl")
        );

        favorite.put(
                "category",
                getIntent().getStringExtra("category")
        );

        favorite.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("favorites")
                .add(favorite)
                .addOnSuccessListener(documentReference ->

                        Toast.makeText(
                                this,
                                "Resep disimpan ke favorit",
                                Toast.LENGTH_SHORT
                        ).show()

                )
                .addOnFailureListener(e ->

                        Toast.makeText(
                                this,
                                "Gagal menyimpan favorit",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // =========================
    // REMOVE FAVORITE
    // =========================
    private void removeFromFavorites() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "User belum login",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String recipeTitle =
                getIntent().getStringExtra("title");

        db.collection("favorites")
                .whereEqualTo(
                        "userId",
                        auth.getCurrentUser().getUid()
                )
                .whereEqualTo(
                        "title",
                        recipeTitle
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            for (com.google.firebase.firestore.DocumentSnapshot document :
                                    queryDocumentSnapshots.getDocuments()) {

                                db.collection("favorites")
                                        .document(document.getId())
                                        .delete();
                            }

                            Toast.makeText(
                                    this,
                                    "Resep dihapus dari favorit",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .addOnFailureListener(e ->

                        Toast.makeText(
                                this,
                                "Gagal menghapus favorit",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // =========================
    // SUBMIT COMMENT
    // =========================
    private void submitComment() {

        String commentText =
                commentInput.getText()
                        .toString()
                        .trim();

        float ratingValue =
                ratingBar.getRating();

        if (commentText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Tulis komentar terlebih dahulu",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        HashMap<String, Object> comment =
                new HashMap<>();

        comment.put(
                "recipeTitle",
                getIntent().getStringExtra("title")
        );

        comment.put(
                "userId",
                auth.getCurrentUser() != null
                        ? auth.getCurrentUser().getUid()
                        : "anonymous"
        );

        comment.put("comment", commentText);
        comment.put("rating", ratingValue);
        comment.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    this,
                                    "Review berhasil dikirim",
                                    Toast.LENGTH_SHORT
                            ).show();

                            commentInput.setText("");
                            ratingBar.setRating(0);

                            loadComments();
                        }
                )
                .addOnFailureListener(e ->

                        Toast.makeText(
                                this,
                                "Gagal mengirim review",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // =========================
    // LOAD COMMENTS
    // =========================
    private void loadComments() {

        String recipeTitle =
                getIntent().getStringExtra("title");

        db.collection("comments")
                .whereEqualTo(
                        "recipeTitle",
                        recipeTitle
                )
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        commentList.clear();

                        for (QueryDocumentSnapshot document :
                                task.getResult()) {

                            String userId =
                                    document.getString("userId");

                            String commentText =
                                    document.getString("comment");

                            Double ratingDouble =
                                    document.getDouble("rating");

                            float rating =
                                    ratingDouble != null
                                            ? ratingDouble.floatValue()
                                            : 0;

                            commentList.add(
                                    new Comment(
                                            recipeTitle,
                                            userId,
                                            commentText,
                                            rating
                                    )
                            );
                        }

                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }
}