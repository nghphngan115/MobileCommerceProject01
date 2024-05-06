package com.group01.plantique.java;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.Category;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    EditText searchEt;
    RecyclerView rvSearchResult, rvSearchSuggestion;
    ImageButton imgbtnBack;
    List<Product> productList = new ArrayList<>();
    List<Product> searchResultList = new ArrayList<>();
    List<Product> searchSuggestionList = new ArrayList<>();
    private SearchItemAdapter searchResultAdapter;
    private SearchItemAdapter searchSuggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        addViews();
        setupListeners();
        String searchKey = getIntent().getStringExtra("searchKey");


        searchEt.setText(searchKey);
        searchProducts(searchKey, searchKey.toLowerCase());


        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();
                searchProducts(searchKey, searchKey.toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rvSearchResult.setLayoutManager(new GridLayoutManager(this, 2));
        rvSearchSuggestion.setLayoutManager(new GridLayoutManager(this, 2));

        searchResultAdapter = new SearchItemAdapter(searchResultList);
        rvSearchResult.setAdapter(searchResultAdapter);

        searchSuggestionAdapter = new SearchItemAdapter(searchSuggestionList);
        rvSearchSuggestion.setAdapter(searchSuggestionAdapter);
    }

    private void searchProducts(String keyword, String lowerCaseKeyword) {
        searchResultList.clear();
        searchSuggestionList.clear();

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        Query query = productsRef.orderByChild("productName").startAt(keyword).endAt(keyword + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        // Kiểm tra tên sản phẩm chính xác
                        if (product.getProductName().toLowerCase().equals(lowerCaseKeyword)) {
                            searchResultList.add(product);
                            Log.d("ProductInfo", "Product found: " + product.getProductName());
                        }
                    }
                }
                updateSearchAdapters();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase Error", databaseError.getMessage());
            }
        });


        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        String categoryId = category.getCateId();
                        Query categoryQuery = productsRef.orderByChild("categoryId").equalTo(categoryId);
                        categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                    Product product = productSnapshot.getValue(Product.class);
                                    if (product != null) {
                                        searchSuggestionList.add(product);
                                        Log.d("ProductInfo", "Suggestion found: " + product.getProductName());
                                    }
                                }
                                updateSearchAdapters();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Firebase Error", databaseError.getMessage());
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase Error", databaseError.getMessage());
            }
        });
    }

    private void updateSearchAdapters() {
        // Cập nhật danh sách dữ liệu cho adapter
        searchResultAdapter.productList = searchResultList;
        searchSuggestionAdapter.productList = searchSuggestionList;

        // Thông báo cho adapter về sự thay đổi dữ liệu
        searchResultAdapter.notifyDataSetChanged();
        searchSuggestionAdapter.notifyDataSetChanged();
    }


    private void setupListeners() {
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc activity để quay lại màn hình trước đó
                finish();
            }
        });
    }

    private void addViews() {
        searchEt = findViewById(R.id.searchEt);
        rvSearchResult = findViewById(R.id.rvSearchResult);
        rvSearchSuggestion = findViewById(R.id.rvSearchSuggestion);
        imgbtnBack = findViewById(R.id.imgbtnBack);
    }

    private class SearchItemAdapter extends RecyclerView.Adapter
            <SearchItemAdapter.SearchItemViewHolder> {
        private List<Product> productList;

        public SearchItemAdapter(List<Product> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new SearchItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchItemViewHolder holder, int position) {
            holder.bind(productList.get(position));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class SearchItemViewHolder extends RecyclerView.ViewHolder {
            TextView productNameTextView;
            TextView textViewPrice;
            TextView txtDiscountPrice;
            ImageView imageViewProduct;
            TextView txtDiscountNote;
            TextView txtUnit;

            public SearchItemViewHolder(View itemView) {
                super(itemView);
                productNameTextView = itemView.findViewById(R.id.textViewProductName);
                textViewPrice = itemView.findViewById(R.id.textViewProductPrice);
                txtDiscountPrice = itemView.findViewById(R.id.textViewProductDiscount);
                imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
                txtDiscountNote = itemView.findViewById(R.id.txtDiscountNote);
                txtUnit = itemView.findViewById(R.id.txtUnit);

                itemView.findViewById(R.id.layoutProductDetail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && productList != null && position < productList.size()) {
                            Product product = productList.get(position);
                            openProductDetailActivity(product);
                        }
                    }
                });
                itemView.findViewById(R.id.imageButtonCart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && productList != null && position < productList.size()) {
                            Product product = productList.get(position);
                            openCartActivity(product);
                        }
                    }
                });
                itemView.findViewById(R.id.constraintLayoutAddToCart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && productList != null && position < productList.size()) {
                            Product product = productList.get(position);
                            openCartActivity(product);
                        }
                    }
                });
            }

            public void bind(final Product product) {
                productNameTextView.setText(product.getProductName());

                String discountPrice = String.valueOf(product.getDiscount_price());

                if (!discountPrice.isEmpty() && !discountPrice.equals("0")) {
                    textViewPrice.setPaintFlags(textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textViewPrice.setText("$" + product.getPrice());
                    txtDiscountPrice.setVisibility(View.VISIBLE);
                    txtDiscountPrice.setText("$" + discountPrice);
                } else {
                    textViewPrice.setPaintFlags(0);
                    textViewPrice.setText("$" + product.getPrice());
                    txtDiscountPrice.setVisibility(View.GONE);
                }

                String imageUrl = product.getImageurl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imageViewProduct);
                } else {
                    Picasso.get().load(R.drawable.logo).into(imageViewProduct);
                }

                if (product.getDiscountNote() != null && !product.getDiscountNote().isEmpty()) {
                    txtDiscountNote.setVisibility(View.VISIBLE);
                    txtDiscountNote.setText(product.getDiscountNote());
                } else {
                    txtDiscountNote.setVisibility(View.GONE);
                }
                txtUnit.setText(product.getUnit());

            }

            private void openProductDetailActivity(Product product) {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productId", product.getProductId());
                context.startActivity(intent);
            }
            private void openCartActivity(Product product) {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, CartActivity.class);
                // Truyền thông tin sản phẩm cần thêm vào giỏ hàng
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("productName", product.getProductName());
                intent.putExtra("price", product.getPrice());
                context.startActivity(intent);
            }
        }
    }
}
