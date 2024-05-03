package com.group01.plantique.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.group01.plantique.R;
import com.group01.plantique.model.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends BaseAdapter {
    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_review, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Review review = reviewList.get(position);
        holder.bind(review);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView profileImageView;
        private TextView userNameTextView;
        private RatingBar ratingBar;
        private TextView reviewTextView;
        private TextView timestampTextView;

        public ViewHolder(View itemView) {
            profileImageView = itemView.findViewById(R.id.profileIv);
            userNameTextView = itemView.findViewById(R.id.nameTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewTextView = itemView.findViewById(R.id.reviewTv);
            timestampTextView = itemView.findViewById(R.id.dateTv);
        }

        public void bind(Review review) {
            userNameTextView.setText(review.getUserName());
            ratingBar.setRating((float) review.getRating());
            reviewTextView.setText(review.getReview());
            timestampTextView.setText(formatTimestamp(review.getTimestamp()));
        }


        private String formatTimestamp(long timestamp) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return dateFormat.format(new Date(timestamp));
        }
    }
}