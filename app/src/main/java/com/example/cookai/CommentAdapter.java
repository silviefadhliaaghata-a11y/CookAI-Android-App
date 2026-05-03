package com.example.cookai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userComment;
        RatingBar userRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userComment = itemView.findViewById(R.id.userComment);
            userRating = itemView.findViewById(R.id.userRating);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Comment comment = commentList.get(position);

        holder.userComment.setText(comment.getComment());
        holder.userRating.setRating(comment.getRating());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}