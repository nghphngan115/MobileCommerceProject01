package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity {
    private EditText titleEt, descriptionEt, unitEt, stockEt, priceEt, discountedPriceEt, discountedNoteEt;
    private Product productToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        unitEt = findViewById(R.id.unitEt);
        stockEt = findViewById(R.id.stockEt);
        priceEt = findViewById(R.id.priceEt);
        discountedPriceEt = findViewById(R.id.discountedPriceEt);
        discountedNoteEt = findViewById(R.id.discountedNoteEt);

        // Nhận thông tin sản phẩm từ Intent
        productToUpdate = (Product) getIntent().getSerializableExtra("EDIT_PRODUCT");

        // Điền thông tin sản phẩm vào các EditText
        if (productToUpdate != null) {
            titleEt.setText(productToUpdate.getProductName());
            descriptionEt.setText(productToUpdate.getDescription());
            unitEt.setText(productToUpdate.getUnit());
            stockEt.setText(String.valueOf(productToUpdate.getStock()));
            priceEt.setText(String.valueOf(productToUpdate.getPrice()));
            discountedPriceEt.setText(String.valueOf(productToUpdate.getDiscount_price()));
            discountedNoteEt.setText(productToUpdate.getDiscountNote());
        }

        Button edtProductBtn = findViewById(R.id.edtProductBtn);
        edtProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });
    }

    private void updateProduct() {
        // Lấy thông tin mới từ các EditText
        String title = titleEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();
        String unit = unitEt.getText().toString().trim();
        int stock = Integer.parseInt(stockEt.getText().toString().trim());
        double price = Double.parseDouble(priceEt.getText().toString().trim());
        int discountedPrice = Integer.parseInt(discountedPriceEt.getText().toString().trim());
        String discountNote = discountedNoteEt.getText().toString().trim();

        // Tạo HashMap để chứa thông tin cập nhật
        HashMap<String, Object> updateMap = new HashMap<>();
        updateMap.put("productName", title);
        updateMap.put("description", description);
        updateMap.put("unit", unit);
        updateMap.put("stock", stock);
        updateMap.put("price", price);
        updateMap.put("discount_price", discountedPrice);
        updateMap.put("discountNote", discountNote);

        // Cập nhật thông tin sản phẩm trên Firebase
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        productsRef.child(productToUpdate.getProductId()).updateChildren(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Cập nhật thành công, hiển thị thông báo hoặc chuyển trang khác
                            Toast.makeText(EditProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                            // Chuyển về màn hình trước hoặc màn hình khác
                            finish(); // Đóng màn hình EditProductActivity
                        } else {
                            // Xử lý khi cập nhật không thành công
                            Toast.makeText(EditProductActivity.this, "Failed to update product: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
