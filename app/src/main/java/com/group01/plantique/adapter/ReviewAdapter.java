package com.group01.plantique.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.model.ModelReview;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<ModelReview> reviewList;
    private Context context;

    public ReviewAdapter(Context context, List<ModelReview> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review, parent, false);
        return new ReviewViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ModelReview review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private RatingBar ratingBar;
        private TextView reviewTextView;
        private TextView timestampTextView;
        private TextView userIdTextView;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewTextView = itemView.findViewById(R.id.reviewEt);
            userIdTextView = itemView.findViewById(R.id.nameTv);
            timestampTextView = itemView.findViewById(R.id.dateTv);

        }

        public void bind(ModelReview review) {
            userIdTextView.setText(review.getUserId());
            ratingBar.setRating((float) review.getRatings());
            reviewTextView.setText(review.getReview());
            timestampTextView.setText(String.valueOf(review.getTimestamp()));
        }
    }
}