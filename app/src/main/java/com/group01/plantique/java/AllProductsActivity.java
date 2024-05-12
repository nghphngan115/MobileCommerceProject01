package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private DatabaseReference productsRef;
    private String categoryId;
    private TextView textViewTitle, filterTv;
    EditText searchEt;
    ImageButton searchBtn, filterBtn, imgbtnBack;
    private FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

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
                Intent intent = new Intent(AllProductsActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        searchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllProductsActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference().child("products");

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(productsRef, Product.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position, @NonNull Product model) {
                holder.setProductDetails(model);
            }

            @NonNull
            @Override
            public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                return new ProductAdapter().new ProductViewHolder(view);
            }
        };

        recyclerViewProducts.setAdapter(adapter);
        adapter.startListening();
    }

    private void filterDialog() {
        String[] options = getResources().getStringArray(R.array.filter_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.filter_products)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            filterTv.setText(options[0]);
                            loadPriceAscending();
                        } else if (i == 1) {
                            filterTv.setText(options[1]);
                            loadPriceDescending();
                        } else if (i == 2) {
                            Intent intent = new Intent(AllProductsActivity.this, ProductCategoriesActivity.class);
                            startActivity(intent);
                        } else if (i == 3) {
                            filterTv.setText(options[3]);
                            loadAllProduct();
                        }
                    }
                })
                .show();
    }


    private void loadAllProduct() {
        Query allProductsQuery = productsRef.orderByKey();
        FirebaseRecyclerOptions<Product> allProductsOptions =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(allProductsQuery, Product.class)
                        .build();

        adapter.updateOptions(allProductsOptions);
    }

    private void loadPriceAscending() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        ProductAdapter adapter = new ProductAdapter(productList); // Tạo adapter với danh sách đã sắp xếp
        sortedRecyclerView.setAdapter(adapter);
    }

    // Phương thức ProductAdapter để hiển thị danh sách sản phẩm
    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private List<Product> productList;

        public ProductAdapter(List<Product> productList) {
            this.productList = productList;
        }

        // Constructor không tham số
        public ProductAdapter() {
            productList = new ArrayList<>(); // Khởi tạo danh sách sản phẩm trống
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            holder.setProductDetails(productList.get(position));
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class ProductViewHolder extends RecyclerView.ViewHolder {
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
                        textViewPrice.setText(product.getPrice() +"đ");
                        txtDiscountPrice.setVisibility(View.VISIBLE);
                        txtDiscountPrice.setText(discountPrice +"đ");
                    } else {
                        // If discount_price is empty, zero, or null, show regular price and hide discount_price
                        textViewPrice.setPaintFlags(0); // Remove strike through if present
                        textViewPrice.setText(product.getPrice() +"đ");
                        textViewPrice.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.main_green));
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
                    if (product.getDiscountNote() != null && !product.getDiscountNote().isEmpty()) {
                        txtDiscountNote.setVisibility(View.VISIBLE);
                        txtDiscountNote.setText(product.getDiscountNote());
                    } else {
                        txtDiscountNote.setVisibility(View.GONE);
                    }

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
                view.findViewById(R.id.constraintLayoutAddToCart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCartActivity(product);
                    }
                });
                view.findViewById(R.id.imageButtonCart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCartActivity(product);
                    }
                });
            }

            private void openProductDetailActivity(Product product) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productId", product.getProductId());
                context.startActivity(intent);
            }

            private void openCartActivity(Product product) {
                Context context = view.getContext();
                Intent intent = new Intent(context, CartActivity.class);
                // Truyền thông tin sản phẩm cần thêm vào giỏ hàng nếu cần
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("productName", product.getProductName());
                intent.putExtra("price", product.getPrice());
                context.startActivity(intent);
            }
        }
    }
}