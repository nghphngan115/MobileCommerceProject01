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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.group01.plantique.R;
import com.group01.plantique.java.BlogDetailActivity;
import com.group01.plantique.java.ProductListActivity;
import com.group01.plantique.model.Category;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CategoryAdapter extends FirebaseRecyclerAdapter <Category, CategoryAdapter.CategoryViewHolder>{

    public CategoryViewHolder holder;
    private Context mContext;
    private RecyclerView recyclerView;

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options) {
        super(options);
    }

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i, @NonNull Category category) {
        if (categoryViewHolder != null) {
            categoryViewHolder.textViewCategoryName.setText(category.getCateName());
            Picasso.get()
                    .load(category.getImageurl())
                    .into(categoryViewHolder.imageViewCategory);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        return new CategoryViewHolder(view);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        TextView textViewCategoryName;
        ImageView imageViewCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategory = itemView.findViewById(R.id.imageViewCategory);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Category clickedCategory = getItem(position);

                        // Tạo Intent và chuyển sang Activity hoặc Fragment tiếp theo
                        if (mContext != null && clickedCategory != null) {
                            Intent intent = new Intent(mContext, ProductListActivity.class);
                            intent.putExtra("cateId", clickedCategory.getCateId());
                            mContext.startActivity(intent);
                        }}

                }
            });
        }
    }
}