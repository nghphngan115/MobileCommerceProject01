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
import com.group01.plantique.model.HighlightBlogItem;

import java.util.ArrayList;

public class HighlightedBlogAdapter extends RecyclerView.Adapter<HighlightedBlogAdapter.BlogViewHolder> {

    Context context;
    ArrayList<HighlightBlogItem> blogList;

    public HighlightedBlogAdapter(Context context, ArrayList<HighlightBlogItem> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlighted_blog, parent, false);
        return new BlogViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        HighlightBlogItem blogItem = blogList.get(position);
        holder.setBlogDetails(blogItem);


        holder.itemView.findViewById(R.id.llBlog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BlogDetailActivity.class);
                intent.putExtra("blogId", blogItem.getBlogId());
                intent.putExtra("blogTitle", blogItem.getBlogTitle());
                intent.putExtra("blogImage", blogItem.getBlogImage());
                intent.putExtra("blogContent", blogItem.getBlogContent());
                intent.putExtra("blogDate", blogItem.getBlogDate());
                intent.putExtra("blogAuthor", blogItem.getBlogAuthor());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (blogList != null) {
            return blogList.size();
        }else {
            return 0;
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgThumbs;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgThumbs = itemView.findViewById(R.id.imgThumbs);

        }

        public void setBlogDetails(HighlightBlogItem blogItem) {
            if (blogItem != null) {
                TextView txtTitle = itemView.findViewById(R.id.txtTitle);
                txtTitle.setText(blogItem.getBlogTitle());

                ImageView imgThumbs = itemView.findViewById(R.id.imgThumbs);
                String blogImage = blogItem.getBlogImage();
                if (blogImage != null && !blogImage.isEmpty()) {
                    Glide.with(itemView.getContext()).load(blogItem.getBlogImage()).into(imgThumbs);
                }



            }
        }
    }}

