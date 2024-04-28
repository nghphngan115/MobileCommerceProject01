package com.group01.plantique.java;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.CartUtility;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private DatabaseReference productsRef;
    private String categoryId;
    private TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        textViewTitle = findViewById(R.id.textViewTitle);

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories").child(categoryId);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String categoryName = snapshot.child("cateName").getValue(String.class);
                    textViewTitle.setText(categoryName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductListActivity", "Failed to fetch category data: " + error.getMessage());
            }
        });

        productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        Query query = productsRef.orderByChild("categoryId").equalTo(categoryId);

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                        holder.setProductDetails(model);
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

        public void setProductDetails(Product product) {
            if (product != null) {
            TextView textViewName = view.findViewById(R.id.textViewProductName);
            textViewName.setText(product.getProductName());

            TextView textViewPrice = view.findViewById(R.id.textViewProductPrice);
            TextView txtDiscountPrice = view.findViewById(R.id.textViewProductDiscount);

            // Convert int discountPrice to String
            String discountPrice = String.valueOf(product.getDiscount_price());

            if (!discountPrice.isEmpty() && !discountPrice.equals("0")) {
                // If discount_price is not empty and not zero, strike through textViewProductPrice and show discountPrice
                textViewPrice.setPaintFlags(textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textViewPrice.setText("$" + product.getPrice());
                txtDiscountPrice.setVisibility(View.VISIBLE);
                txtDiscountPrice.setText("$" + discountPrice);
            } else {
                // If discount_price is empty, zero, or null, show regular price and hide discount_price
                textViewPrice.setPaintFlags(0); // Remove strike through if present
                textViewPrice.setText("$" + product.getPrice());
                txtDiscountPrice.setVisibility(View.GONE);
            }

            ImageView imageView = view.findViewById(R.id.imageViewProduct);
            String imageUrl = product.getImageurl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(imageView);
            } else {
                // Xử lý khi URL trống, ví dụ load ảnh mặc định
                Picasso.get().load(R.drawable.logo).into(imageView);
            }

            TextView txtDiscountNote = view.findViewById(R.id.txtDiscountNote);
            txtDiscountNote.setText(product.getDiscountNote());

            TextView txtUnit = view.findViewById(R.id.txtUnit);
            txtUnit.setText(product.getUnit());
            } else {
                Log.e("ProductListActivity", "Product object is null");
            }

            // Set click listener for product detail
            view.findViewById(R.id.layoutProductDetail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProductDetailActivity(product);
                }
            });

            // Set click listener for add to cart area
            // Thay đổi phương thức gọi khi click vào nút "Thêm vào giỏ hàng"
            view.findViewById(R.id.constraintLayoutAddToCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCart(product);
                }
            });

            view.findViewById(R.id.imageButtonCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCart(product);
                }
            });
        }

        private void openProductDetailActivity(Product product) {
            Context context = view.getContext();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            context.startActivity(intent);
        }

        private void addToCart(Product product) {
            // Lấy danh sách sản phẩm từ SharedPreferences
            ArrayList<Product> cartProducts = CartUtility.getCartProducts(view.getContext());

            boolean found = false;

            // Tìm sản phẩm trong giỏ hàng
            for (Product p : cartProducts) {
                if (p.getProductId().equals(product.getProductId())) {
                    // Nếu sản phẩm đã tồn tại trong giỏ hàng, tăng số lượng lên 1
                    p.setCartQuantity(p.getCartQuantity() + 1);
                    found = true;
                    break;
                }
            }

            // Nếu sản phẩm chưa tồn tại trong giỏ hàng, thêm vào giỏ hàng với số lượng là 1
            if (!found) {
                product.setCartQuantity(1);
                cartProducts.add(product);
            }

            // Lưu danh sách sản phẩm giỏ hàng mới vào SharedPreferences
            CartUtility.saveCartProducts(view.getContext(), cartProducts);

            // Hiển thị thông báo
            Context context = null;
            Toast.makeText(view.getContext(), context.getString(R.string.added_to_cart_message), Toast.LENGTH_SHORT).show();


            // Mở CartActivity khi click vào dòng toast cuối cùng
            context = view.getContext();
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
        }
    }
}