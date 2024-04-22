package com.group01.plantique.java;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private DatabaseReference databaseReference;
    private String categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryName = intent.getStringExtra("categoryName");
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(categoryName);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("products").child(categoryId);

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(databaseReference, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                        holder.setProductName(model.getName());
                        holder.setProductPrice(model.getPrice());
                        holder.setProductImage(getApplicationContext(), model.getImageurl());
                        holder.setAddToCartClickListener(getApplicationContext(), model.getId());// Truyền ID sản phẩm vào đây
                        holder.setProductDetailClickListener(getApplicationContext(), model.getId());// Truyền ID sản phẩm vào đây
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                        return new ProductViewHolder(view);
                    }
                };

        recyclerViewProducts.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setProductName(String name) {
            TextView textView = view.findViewById(R.id.textViewProductName);
            textView.setText(name);
        }

        public void setProductPrice(int price) {
            TextView textView = view.findViewById(R.id.textViewProductPrice);
            textView.setText("$" + price);
        }

        public void setProductImage(Context context, String imageUrl) {
            ImageView imageView = view.findViewById(R.id.imageViewProduct);
            Picasso.get().load(imageUrl).into(imageView);
        }

        public void setAddToCartClickListener(final Context context, final String productId) {
            ConstraintLayout constraintLayout = view.findViewById(R.id.constraintLayoutAddToCart);
            ImageButton imageButtonCart = view.findViewById(R.id.imageButtonCart);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click event for ConstraintLayout (Add to cart)
                    addToCartAction(context, productId);
                }
            });

            imageButtonCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click event for ImageButton (Add to cart)
                    addToCartAction(context, productId);
                }
            });
        }

        private void addToCartAction(Context context, String productId) {
            Intent intent = new Intent(context, AddToCartActivity.class);
            intent.putExtra("productId", productId); // Truyền ID sản phẩm nếu cần
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public void setProductDetailClickListener(final Context context, final String productId) {
            LinearLayout layoutProductDetail = view.findViewById(R.id.layoutProductDetail);

            layoutProductDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Mở activity hiển thị thông tin chi tiết sản phẩm
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("productId", productId); // Truyền ID sản phẩm
                    // Thêm cờ FLAG_ACTIVITY_NEW_TASK nếu cần
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }
}
