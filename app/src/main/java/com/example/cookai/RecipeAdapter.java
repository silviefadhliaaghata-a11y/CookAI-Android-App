package com.example.cookai;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    // =========================
    // DATA
    // =========================
    private List<Recipe> recipeList;

    // =========================
    // CONSTRUCTOR
    // =========================
    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    // =========================
    // VIEWHOLDER
    // =========================
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView desc;
        TextView category;
        ImageView recipeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.recipeTitle);
            desc = itemView.findViewById(R.id.recipeDescription);
            category = itemView.findViewById(R.id.recipeCategory);
            recipeImage = itemView.findViewById(R.id.recipeImage);
        }
    }

    // =========================
    // CREATE VIEW
    // =========================
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_recipe,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    // =========================
    // BIND DATA
    // =========================
    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Recipe recipe = recipeList.get(position);

        // =========================
        // SET TEXT
        // =========================
        holder.title.setText(recipe.getTitle());
        holder.desc.setText(recipe.getDescription());

        if (recipe.getCategory() != null
                && !recipe.getCategory().isEmpty()) {

            holder.category.setText("🍽 " + recipe.getCategory());

        } else {

            holder.category.setText("");
        }

        // =========================
        // LOAD IMAGE
        // =========================
        if (recipe.getImageUrl() != null
                && !recipe.getImageUrl().isEmpty()) {

            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(recipe.getImageUrl()))
                    .into(holder.recipeImage);

        } else {

            holder.recipeImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }

        // =========================
        // ITEM CLICK
        // =========================
        holder.itemView.setOnClickListener(v -> {

            // animasi klik
            holder.itemView.setAlpha(0.7f);
            holder.itemView.animate()
                    .alpha(1f)
                    .setDuration(200);

            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    RecipeDetailActivity.class
            );

            // =========================
            // SEND DATA
            // =========================
            intent.putExtra("title", recipe.getTitle());
            intent.putExtra("description", recipe.getDescription());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("steps", recipe.getSteps());
            intent.putExtra("imageUrl", recipe.getImageUrl());
            intent.putExtra("category", recipe.getCategory());

            holder.itemView.getContext()
                    .startActivity(intent);
        });
    }

    // =========================
    // ITEM COUNT
    // =========================
    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}