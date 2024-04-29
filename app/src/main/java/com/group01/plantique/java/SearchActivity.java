package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.Category;
import com.group01.plantique.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ImageButton imgbtnBack;
    private EditText searchEt;
    private LinearLayout productSearchLL;
    private ListView lvSearchResults;
    private RecyclerView rvSearchSuggestion;
    private ArrayAdapter<String> searchHistoryAdapter;
    private List<String> searchHistoryList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private DatabaseReference productsRef, categoriesRef;
    private FirebaseRecyclerAdapter<Category, ProductCategoriesActivity.CategoryViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imgbtnBack = findViewById(R.id.imgbtnBack);
        searchEt = findViewById(R.id.searchEt);
        productSearchLL = findViewById(R.id.productSearchLL);
        lvSearchResults = findViewById(R.id.lvSearchResults);
        rvSearchSuggestion = findViewById(R.id.rvSearchSuggestion);

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEt.requestFocus();

        // Khởi tạo adapter cho rvSearchSuggestion
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvSearchSuggestion.setLayoutManager(layoutManager);

        // Khởi tạo adapter cho ListView
        searchHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchHistoryList);
        lvSearchResults.setAdapter(searchHistoryAdapter);

        // Khởi tạo Firebase references
        productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        loadProducts();
        loadCategories();

        // Xử lý khi click vào item trong lvSearchResults
        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSearch = searchHistoryList.get(position);
                startSearchResultActivity(selectedSearch);
            }
        });

        // Xử lý khi click vào item trong rvSearchSuggestion
        rvSearchSuggestion.addOnItemTouchListener(new RecyclerItemClickListener(this, rvSearchSuggestion,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Category selectedCategory = categoryList.get(position);
                        startSearchResultActivity(selectedCategory.getCateName());
                    }
                }));

        // Xử lý tìm kiếm khi người dùng nhập text vào EditText
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();
                searchProducts(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Khởi tạo adapter
        adapter = new FirebaseRecyclerAdapter<Category, ProductCategoriesActivity.CategoryViewHolder>(
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("categories"), Category.class)
                        .build()
        ) {
            @Override
            protected void onBindViewHolder(@NonNull ProductCategoriesActivity.CategoryViewHolder holder, int position, @NonNull Category model) {
                holder.setCategoryName(model.getCateName());
                holder.setCategoryImage(getApplicationContext(), model.getImageurl());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleCategoryClick(model);
                    }
                });
            }

            @NonNull
            @Override
            public ProductCategoriesActivity.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
                return new ProductCategoriesActivity.CategoryViewHolder(view);
            }
        };

        rvSearchSuggestion.setAdapter(adapter);
        adapter.startListening();
    }
    private void startSearchResultActivity(String searchKey) {
        Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
        intent.putExtra("searchKey", searchKey);
        startActivity(intent);
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
                searchProducts("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    private void loadCategories() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }

    private void searchProducts(String keyword) {
        searchHistoryList.clear();

        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(keyword)) {
                searchHistoryList.add(product.getProductName());
            }
        }

        for (Category category : categoryList) {
            if (category.getCateName().toLowerCase().contains(keyword)) {
                searchHistoryList.add(category.getCateName());
            }
        }

        searchHistoryAdapter.notifyDataSetChanged();
    }


    private void handleCategoryClick(Category category) {
        addToSearchHistory(category.getCateName());
        Intent intent = new Intent(SearchActivity.this, ProductListActivity.class);
        intent.putExtra("categoryId", category.getCateId());
        intent.putExtra("categoryName", category.getCateName());
        startActivity(intent);
    }

    private void addToSearchHistory(String itemName) {
        searchHistoryList.add(itemName);
        adapter.notifyDataSetChanged();
    }
}
