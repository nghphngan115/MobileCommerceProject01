package com.group01.plantique.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group01.plantique.R;
import com.group01.plantique.java.BlogDetailActivity;
import com.group01.plantique.model.BlogItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private Context mContext;
    private ArrayList<BlogItem> blogItems;
    private OnItemClickListener mListener;

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public BlogAdapter(Context context, ArrayList<BlogItem> blogItems) {
        mContext = context;
        this.blogItems = blogItems;
    }

    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.blog_show, parent, false);
        return new BlogViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogItem currentItem = blogItems.get(position);
        holder.titleTextView.setText(currentItem.getBlogTitle());
        Picasso.get().load(currentItem.getBlogImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return blogItems.size();
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;

        public BlogViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgBlog);
            titleTextView = itemView.findViewById(R.id.txtTitle1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
