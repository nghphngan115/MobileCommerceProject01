package com.group01.plantique.java;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.group01.plantique.R;
import com.group01.plantique.adapter.ProductListAdapter;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private DatabaseReference productsRef;
    private String categoryId;
    private TextView textViewTitle, filterTv;
    EditText searchEt;
    ImageButton searchBtn, filterBtn, imgbtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        textViewTitle = findViewById(R.id.textViewTitle);
        searchBtn = findViewById(R.id.searchBtn);
        searchEt = findViewById(R.id.searchEt);
        filterBtn = findViewById(R.id.filterBtn);
        filterTv = findViewById(R.id.filterTv);
        imgbtnBack = findViewById(R.id.imgbtnBack);

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc activity để quay lại màn hình trước đó
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        searchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });

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

        FirebaseRecyclerAdapter<Product, ProductListAdapter.ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductListAdapter.ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductListAdapter.ProductViewHolder holder, int position, @NonNull Product model) {
                        holder.setProductDetails(model);
                    }

                    @NonNull
                    @Override
                    public ProductListAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                        return new ProductListAdapter.ProductViewHolder(view);
                    }
                };

        recyclerViewProducts.setAdapter(adapter);
        adapter.startListening();
    }

    private void filterDialog() {
        String[] options = {getString(R.string.price_ascending), getString(R.string.price_descending), getString(R.string.sort_by_category), getString(R.string.all_products)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.filter_products))
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            filterTv.setText(getString(R.string.price_ascending));
                            loadPriceAscending();
                        } else if (i == 1) {
                            filterTv.setText(getString(R.string.price_descending));
                            loadPriceDescending();
                        } else if (i == 2) {
                            Intent intent = new Intent(ProductListActivity.this, ProductCategoriesActivity.class);
                            startActivity(intent);
                        } else if (i == 3) {
                            Intent intent = new Intent(ProductListActivity.this, AllProductsActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }



    private void loadPriceAscending() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        Query query = productsRef.orderByChild("categoryId").equalTo(categoryId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Product> productList = new ArrayList<>();
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    // Sắp xếp danh sách sản phẩm theo giá tăng dần
                    Collections.sort(productList, new Comparator<Product>() {
                        @Override
                        public int compare(Product p1, Product p2) {
                            return Double.compare(p1.getPrice(), p2.getPrice());
                        }
                    });
                    // Hiển thị danh sách sản phẩm sau khi đã sắp xếp
                    displaySortedProducts(productList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AllProductsActivity", "Failed to fetch products data: " + error.getMessage());
            }
        });
    }

    private void loadPriceDescending() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        Query query = productsRef.orderByChild("categoryId").equalTo(categoryId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Product> productList = new ArrayList<>();
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    // Sắp xếp danh sách sản phẩm theo giá giảm dần
                    Collections.sort(productList, new Comparator<Product>() {
                        @Override
                        public int compare(Product p1, Product p2) {
                            return Double.compare(p2.getPrice(), p1.getPrice());
                        }
                    });
                    // Hiển thị danh sách sản phẩm sau khi đã sắp xếp
                    displaySortedProducts(productList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AllProductsActivity", "Failed to fetch products data: " + error.getMessage());
            }
        });
    }


    private void displaySortedProducts(List<Product> productList) {
        // Hiển thị danh sách sản phẩm đã sắp xếp trên RecyclerView hoặc giao diện của bạn
        // Ví dụ:
        RecyclerView sortedRecyclerView = findViewById(R.id.recyclerViewProducts);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        sortedRecyclerView.setLayoutManager(layoutManager); // Đổi LayoutManager tùy vào giao diện của bạn
        ProductListAdapter adapter = new ProductListAdapter(productList); // Tạo adapter với danh sách đã sắp xếp
        sortedRecyclerView.setAdapter(adapter);
    }
}