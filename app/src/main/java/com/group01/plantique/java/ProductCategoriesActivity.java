package com.group01.plantique.java;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.group01.plantique.R;
import com.group01.plantique.model.Category;
import com.squareup.picasso.Picasso;

public class ProductCategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCategories;
    private DatabaseReference databaseReference;
    private ImageButton imgbtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);
        imgbtnBack = findViewById(R.id.imgbtnBack);
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 2));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("categories");

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(databaseReference, Category.class)
                        .build();

        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
                        holder.setCategoryName(model.getCateName());
                        holder.setCategoryImage(getApplicationContext(), model.getImageurl());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ProductCategoriesActivity.this, ProductListActivity.class);
                                intent.putExtra("categoryId", model.getCateId());
                                intent.putExtra("categoryName", model.getCateName());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
                        return new CategoryViewHolder(view);
                    }
                };

        recyclerViewCategories.setAdapter(adapter);
        adapter.startListening();
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        View view;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setCategoryName(String name) {
            TextView textView = view.findViewById(R.id.textViewCategoryName);
            textView.setText(name);
        }

        public void setCategoryImage(Context context, String imageUrl) {
            ImageView imageView = view.findViewById(R.id.imageViewCategory);
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_background).into(imageView);
        }
    }
}
