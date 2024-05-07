package com.group01.plantique.java;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.group01.plantique.R;
import com.group01.plantique.adapter.CategoryAdapter;
import com.group01.plantique.adapter.HighlightedBlogAdapter;
import com.group01.plantique.adapter.ProductAdapter;
import com.group01.plantique.model.BlogItem;
import com.group01.plantique.model.Category;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

public class HomeScreenActivity extends AppCompatActivity {

    SearchView svSearch;
    Button btnViewAll, btnViewAll2, btnViewAllCate;
    RecyclerView rvCategory, rvHighlightedProduct;
    List<Category> categoryList;
    CategoryAdapter categoryAdapter;
    ListView lvHiglightedBlog;
    HighlightedBlogAdapter highlightedblogAdapter;
    private ArrayList<BlogItem> blogItems;
    private ArrayList<Product> products;
    LinearLayout llProduct;
    ConstraintLayout  btnBuynow1, btnBuynow2, clCate;
    ViewFlipper ViewFlipper;

    Context context = this;



    DatabaseReference databaseReference;
    private String categoryId;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.blog) {
                startActivity(new Intent(getApplicationContext(), BlogCategoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.notification) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), UserInformationActivity.class));
                finish();
                return true;
            }
            return false;
        });

        //addView,addEvent
        svSearch = findViewById(R.id.svSearch);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnViewAll2 = findViewById(R.id.btnViewAll2);
        btnViewAllCate= findViewById(R.id.btnViewAllCate);
        btnBuynow2= findViewById(R.id.btnBuynow2);
        btnBuynow1 = findViewById(R.id.btnBuynow1);
        ViewFlipper =findViewById(R.id.ViewFlipper);
        clCate = findViewById(R.id.clCate);

        btnBuynow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, AllProductsActivity.class);
                startActivity(intent);
            }
        });

        btnBuynow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, AllProductsActivity.class);
                startActivity(intent);
            }
        });

        rvCategory = findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(new GridLayoutManager(this, 4));

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories"), Category.class)
                .build();

        CategoryAdapter.OnCategoryClickListener categoryClickListener = null;
        categoryAdapter = new CategoryAdapter(options, HomeScreenActivity.this, categoryClickListener);
        rvCategory.setAdapter(categoryAdapter);

        FirebaseRecyclerAdapter<Category, HomeScreenActivity.CategoryViewHolder> adapter =
                new FirebaseRecyclerAdapter<Category, HomeScreenActivity.CategoryViewHolder>(options) {
                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
                        return new HomeScreenActivity.CategoryViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull HomeScreenActivity.CategoryViewHolder holder, int position, @NonNull Category model) {
                        holder.setCategoryName(model.getCateName());
                        holder.setCategoryImage(getApplicationContext(), model.getImageurl());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeScreenActivity.this, ProductListActivity.class);
                                intent.putExtra("categoryId", model.getCateId());
                                intent.putExtra("categoryName", model.getCateName());
                                startActivity(intent);
                            }
                        });
                    }
                };
        rvCategory.setAdapter(adapter);
        adapter.startListening();

        lvHiglightedBlog = findViewById(R.id.lvHighlightedBlog);
        blogItems = new ArrayList<>();
        highlightedblogAdapter = new HighlightedBlogAdapter(this, blogItems);
        lvHiglightedBlog.setAdapter(highlightedblogAdapter);

        rvHighlightedProduct = findViewById(R.id.rvHighlightedProduct);
        rvHighlightedProduct.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this,AllProductsActivity.class);
                startActivity(intent);
            }
        });
        btnViewAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this,BlogCategoryActivity.class);
                startActivity(intent);
            }
        });
        btnViewAllCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this,ProductCategoriesActivity.class);
                startActivity(intent);
            }
        });
        getProductFromFirebase();
        getBlogFromFirebase();

        lvHiglightedBlog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlogItem selectedBlog = blogItems.get(position);

                // Open BlogDetailActivity with selected blog data
                Intent intent = new Intent(HomeScreenActivity.this, BlogDetailActivity.class);
                intent.putExtra("blogId", selectedBlog.getBlogId());
                startActivity(intent);
            }
        });

        //Search
        svSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Intent để chuyển từ HomeScreenActivity sang SearchProductActivity
                Intent intent = new Intent(HomeScreenActivity.this, SearchActivity.class);
                // Bắt đầu SearchProductActivity
                startActivity(intent);
            }
        });

    }

    private void getProductFromFirebase() {
        FirebaseRecyclerOptions<Product> productFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("products"), Product.class)
                .build();
        FirebaseRecyclerAdapter<Product, HomeScreenActivity.ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, HomeScreenActivity.ProductViewHolder>(productFirebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                        holder.setProductDetails(model);
                    }

                    @NonNull
                    @Override
                    public HomeScreenActivity.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highlighted_product, parent, false);
                        return new HomeScreenActivity.ProductViewHolder(view);
                    }
                };

        rvHighlightedProduct.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setProductDetails(Product products) {
            TextView txtProductName = view.findViewById(R.id.txtProductName);
            txtProductName.setText(products.getProductName());
            TextView txtUnitPrice = view.findViewById(R.id.txtUnitPrice);
            txtUnitPrice.setText(String.valueOf(products.getPrice() +"đ"));
            TextView txtItemUnit = view.findViewById(R.id.txtItemUnit);
            txtItemUnit.setText(products.getUnit());
            ImageView imgProduct =view.findViewById(R.id.imgProduct);
            String imageUrl = products.getImageurl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(imgProduct);
            }
            view.findViewById(R.id.llProduct).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProductDetailActivity(products);
                }
            });

        }

        private void openProductDetailActivity(Product product) {
            Context context = view.getContext();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            context.startActivity(intent);
        }

    }


    private void getBlogFromFirebase() {
        // Kết nối đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Blog");

        // Đọc dữ liệu từ Firebase và nạp vào Adapter
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blogItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get blog data and add to list
                    String blogId = snapshot.getKey();
                    String title = snapshot.child("blogTitle").getValue(String.class);
                    String imageUrl = snapshot.child("blogImage").getValue(String.class);
                    String content = snapshot.child("blogContent").getValue(String.class);
                    blogItems.add(new BlogItem(blogId, title, imageUrl, content));
                }
                // Notify adapter that data set has changed
                highlightedblogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    @Override
    public void onStart() {
        super.onStart();
        categoryAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        View view;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setCategoryName(String cateName) {
            TextView textView = view.findViewById(R.id.txtCateName);
            textView.setText(cateName);
        }

        public void setCategoryImage(Context applicationContext, String imageurl) {
            ImageView imageView = view.findViewById(R.id.imgCat);
            Picasso.get().load(imageurl).into(imageView);
        }
    }
}

