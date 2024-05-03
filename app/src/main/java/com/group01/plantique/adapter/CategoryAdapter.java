package com.group01.plantique.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.group01.plantique.R;
import com.group01.plantique.java.HomeScreenActivity;
import com.group01.plantique.java.ProductCategoriesActivity;
import com.group01.plantique.java.ProductDetailActivity;
import com.group01.plantique.java.ProductListActivity;
import com.group01.plantique.model.Category;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CategoryAdapter extends FirebaseRecyclerAdapter <Category, CategoryAdapter.CategoryViewHolder>{

    public CategoryViewHolder holder;
    private Context mContext;
    Category category;

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options,  Context context) {
        super(options);
        mContext = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i, @NonNull Category category) {
        categoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductListActivity.class);
                intent.putExtra("categoryId", category.getCateId());
                intent.putExtra("categoryName", category.getCateName());
                mContext.startActivity(intent);
            }
        });

        if (categoryViewHolder != null) {
            categoryViewHolder.txtCateName.setText(category.getCateName());
            Picasso.get()
                    .load(category.getImageurl())
                    .into(categoryViewHolder.imgCat);
        }

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category,parent,false);
        return new CategoryViewHolder(view);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        TextView txtCateName;
        ImageView imgCat;
        ConstraintLayout clCate;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCateName = itemView.findViewById(R.id.txtCateName);
            imgCat = itemView.findViewById(R.id.imgCat);
            clCate= itemView.findViewById(R.id.clCate);


        }

    }
}