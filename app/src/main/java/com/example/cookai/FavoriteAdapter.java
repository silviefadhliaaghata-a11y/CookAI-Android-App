package com.example.cookai;

import android.content.Context;
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

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> favoriteRecipes;

    public FavoriteAdapter(
            Context context,
            List<Recipe> favoriteRecipes
    ) {
        this.context = context;
        this.favoriteRecipes = favoriteRecipes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        TextView category;
        ImageView recipeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.recipeTitle);
            description = itemView.findViewById(R.id.recipeDescription);
            category = itemView.findViewById(R.id.recipeCategory);
            recipeImage = itemView.findViewById(R.id.recipeImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_recipe,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Recipe recipe = favoriteRecipes.get(position);

        // =========================
        // TEXT
        // =========================
        holder.title.setText(recipe.getTitle());
        holder.description.setText(recipe.getDescription());

        if (recipe.getCategory() != null
                && !recipe.getCategory().isEmpty()) {

            holder.category.setText(
                    "🍽 " + recipe.getCategory()
            );

        } else {

            holder.category.setText("");
        }

        // =========================
        // IMAGE
        // =========================
        if (recipe.getImageUrl() != null
                && !recipe.getImageUrl().isEmpty()) {

            Glide.with(context)
                    .load(Uri.parse(recipe.getImageUrl()))
                    .into(holder.recipeImage);

        } else {

            holder.recipeImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }

        // =========================
        // CLICK
        // =========================
        holder.itemView.setOnClickListener(v -> {

            holder.itemView.setAlpha(0.7f);
            holder.itemView.animate()
                    .alpha(1f)
                    .setDuration(200);

            Intent intent = new Intent(
                    context,
                    RecipeDetailActivity.class
            );

            intent.putExtra(
                    "title",
                    recipe.getTitle()
            );

            intent.putExtra(
                    "description",
                    recipe.getDescription()
            );

            intent.putExtra(
                    "ingredients",
                    recipe.getIngredients()
            );

            intent.putExtra(
                    "steps",
                    recipe.getSteps()
            );

            intent.putExtra(
                    "imageUrl",
                    recipe.getImageUrl()
            );

            intent.putExtra(
                    "category",
                    recipe.getCategory()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteRecipes.size();
    }
}