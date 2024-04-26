package com.group01.plantique.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        searchProductEt = findViewById(R.id.searchEt);
        filterProductBtn = findViewById(R.id.filterBtn);
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
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Xử lý sự kiện khi click vào sản phẩm
                showBottomSheet(product);
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
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Xử lý sự kiện khi click vào sản phẩm
                showBottomSheet(product);
            }
        });
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
    private void showBottomSheet(Product product) {
        // Khởi tạo BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bs_product_admin, null);

        // Ánh xạ các view trong bottom sheet layout
        ImageView imageViewProduct = bottomSheetView.findViewById(R.id.imgProduct);
        TextView txtProductName = bottomSheetView.findViewById(R.id.txtProductName);
        TextView txtProductCategory = bottomSheetView.findViewById(R.id.txtProductCategory);
        TextView txtPrice = bottomSheetView.findViewById(R.id.txtPrice);
        TextView txtDiscountPrice = bottomSheetView.findViewById(R.id.txtDiscountPrice);
        TextView txtDescription = bottomSheetView.findViewById(R.id.txtDescription);
        TextView txtStock = bottomSheetView.findViewById(R.id.txtStock);
        TextView txtUnit = bottomSheetView.findViewById(R.id.txtUnit);

        // Hiển thị thông tin chi tiết của sản phẩm
        txtProductName.setText(product.getProductName());
        txtProductCategory.setText(product.getCategoryId()); // Hoặc set tên danh mục nếu có
        txtPrice.setText("$" + product.getPrice());
        txtDescription.setText(product.getDescription());
        txtStock.setText(String.valueOf(product.getStock()));
        txtUnit.setText(product.getUnit());

        // Kiểm tra và hiển thị giá giảm nếu có
        int discountPrice = product.getDiscount_price();
        if (discountPrice > 0) {
            txtDiscountPrice.setVisibility(View.VISIBLE);
            txtDiscountPrice.setText("$" + discountPrice);
            txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            txtDiscountPrice.setVisibility(View.GONE);
        }

        // Sử dụng Picasso hoặc Glide để load ảnh sản phẩm từ URL và hiển thị trong ImageView
        Picasso.get().load(product.getImageurl()).into(imageViewProduct);

        // Gán sự kiện cho các button trong bottom sheet (ví dụ: xóa, chỉnh sửa)
        ImageButton  btnEditProduct = bottomSheetView.findViewById(R.id.btnEditProduct);
        ImageButton btnDeleteProduct = bottomSheetView.findViewById(R.id.btnDeleteProduct);

        btnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProductActivity.this, EditProductActivity.class);
                intent.putExtra("EDIT_PRODUCT", product); // Truyền thông tin sản phẩm cần chỉnh sửa
                startActivity(intent);
                bottomSheetDialog.dismiss(); // Đóng bottom sheet sau khi chuyển màn hình
            }
        });

        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi nhấn nút xóa sản phẩm
                showDeleteConfirmationDialog(product);
                bottomSheetDialog.dismiss(); // Đóng bottom sheet sau khi xử lý
            }
        });

        // Đặt view vào bottom sheet và hiển thị bottom sheet
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showDeleteConfirmationDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this product?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý xóa sản phẩm từ Firebase hoặc một nguồn dữ liệu khác
                // productsRef.child(product.getProductId()).removeValue(); // Ví dụ xóa từ Firebase
                // Cập nhật giao diện hoặc thông báo xóa thành công
                Toast.makeText(AdminProductActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng dialog nếu người dùng chọn Cancel
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
