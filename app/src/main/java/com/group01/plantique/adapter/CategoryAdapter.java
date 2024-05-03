package com.group01.plantique.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.group01.plantique.R;
import com.group01.plantique.model.Category;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends FirebaseRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options, Context context, OnCategoryClickListener listener) {
        super(options);
        this.mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
        holder.setCategoryName(model.getCateName());
        Picasso.get()
                .load(model.getImageurl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.getImageView());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(model);
            }
        });
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCateName;
        private ImageView imgCat;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCateName = itemView.findViewById(R.id.textViewCategoryName);
            imgCat = itemView.findViewById(R.id.imageViewCategory);
        }

        public void setCategoryName(String name) {
            txtCateName.setText(name);
        }

        public ImageView getImageView() {
            return imgCat;
        }
    }
}
