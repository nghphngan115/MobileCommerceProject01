package com.group01.plantique.java;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.group01.plantique.adapter.ProductAdapter;
import com.group01.plantique.model.Category;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminProductActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProducts;
    private DatabaseReference productsRef, categoriesRef;
    private EditText searchProductEt;
    private ImageButton filterProductBtn, searchBtn;
    private ProductAdapter productAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private String selectedCategory = "All"; // Danh mục được chọn mặc định
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        searchBtn = findViewById(R.id.searchBtn);

        productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        loadCategories();
        setupRecyclerView();

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchProductEt.getText().toString();
                filterProducts(searchText);
            }
        });
    }

    private void loadCategories() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    categoryList.clear();
                    categoryList.add(new Category("All", "All"));
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        Category category = categorySnapshot.getValue(Category.class);
                        if (category != null) {
                            categoryList.add(category);
                        }
                    }
                    loadProducts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProductActivity.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProductActivity.this, "Failed to load products: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(productList);
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void filterProducts(String keyword) {
        List<Product> filteredList;

        if (selectedCategory.equals("All")) {
            // Lọc theo từ khóa khi danh mục được chọn là "All"
            filteredList = filterByKeyword(productList, keyword);
        } else {
            // Lọc theo danh mục và từ khóa
            List<Product> categoryFilteredList = filterByCategory(productList, selectedCategory);
            filteredList = filterByKeyword(categoryFilteredList, keyword);
        }

        // Cập nhật danh sách sản phẩm lọc được vào Adapter
        productAdapter.updateProductList(filteredList);
        Log.d("AdminProductActivity", "Selected Category: " + selectedCategory);
    }

    private List<Product> filterByCategory(List<Product> products, String category) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategoryId().equals(category)) {
                filteredList.add(product);
            }
        }
        return filteredList;
    }

    private List<Product> filterByKeyword(List<Product> products, String keyword) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : products) {
            if (product.getProductName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(product);
            }
        }
        return filteredList;
    }



    @Override
    protected void onStart() {
        super.onStart();

        // Xử lý tìm kiếm realtime khi thay đổi nội dung của ô tìm kiếm
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lọc sản phẩm theo từ khóa khi text thay đổi
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No implementation needed
            }
        });
    }




    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category");

        String[] categoryNames = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categoryNames[i] = categoryList.get(i).getCateName();
        }

        builder.setItems(categoryNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategory = categoryList.get(which).getCateId();
                filterProducts(""); // Áp dụng filter khi chọn danh mục mới
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
