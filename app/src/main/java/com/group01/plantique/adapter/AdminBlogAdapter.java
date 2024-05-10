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

import com.group01.plantique.R;
import com.group01.plantique.java.AdminEditBlogActivity;
import com.group01.plantique.model.BlogItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminBlogAdapter extends RecyclerView.Adapter<AdminBlogAdapter.BlogViewHolder> {
    private Context context;
    private ArrayList<BlogItem> blogItems;
    private LayoutInflater inflater;

    public AdminBlogAdapter(Context context, ArrayList<BlogItem> blogItems) {
        this.context = context;
        this.blogItems = blogItems;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.admin_blog_item, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogItem currentBlog = blogItems.get(position);
        // Display only the first 20 characters of the blog content
        String titlePreview = currentBlog.getBlogTitle().length() > 20 ?
                currentBlog.getBlogTitle().substring(0, 20) + "..." :
                currentBlog.getBlogTitle();
        holder.txtBlogTitle.setText(titlePreview);
        Picasso.get().load(currentBlog.getBlogImage()).into(holder.imgBlog);

        // Display only the first 20 characters of the blog content
        String contentPreview = currentBlog.getBlogContent().length() > 20 ?
                currentBlog.getBlogContent().substring(0, 20) + "..." :
                currentBlog.getBlogContent();
        holder.txtBlogContent.setText(contentPreview);

        // Set click listener to navigate to AdminEditBlogActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AdminEditBlogActivity when an item is clicked
                Intent intent = new Intent(context, AdminEditBlogActivity.class);
                intent.putExtra("blogId", currentBlog.getBlogId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return blogItems.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBlog;
        TextView txtBlogTitle, txtBlogContent;

        public BlogViewHolder(View itemView) {
            super(itemView);
            imgBlog = itemView.findViewById(R.id.imgBlog);
            txtBlogTitle = itemView.findViewById(R.id.txtBlogTitle);
            txtBlogContent = itemView.findViewById(R.id.txtBlogContent);
        }
    }
}
