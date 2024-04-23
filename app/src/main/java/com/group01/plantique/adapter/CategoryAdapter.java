package com.group01.plantique.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.model.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;

    public CategoryAdapter() {
        this.categoryList = new ArrayList<>();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList.clear();
        this.categoryList.addAll(categoryList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewCategoryName;
        private ImageView imageViewCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategory = itemView.findViewById(R.id.imageViewCategory);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
        }

        public void bind(Category category) {
            textViewCategoryName.setText(category.getCateName());
            // Load image using Picasso or any other image loading library
            Picasso.get().load(category.getImageurl()).into(imageViewCategory);
        }
    }
}


