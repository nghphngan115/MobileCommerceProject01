package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        titleEt = findViewById(R.id.promoCodeEt);
        descriptionEt = findViewById(R.id.promoDescriptionEt);
        unitEt = findViewById(R.id.promoPriceEt);
        stockEt = findViewById(R.id.stockEt);
        priceEt = findViewById(R.id.priceEt);
        discountedPriceEt = findViewById(R.id.discountedPriceEt);
        discountedNoteEt = findViewById(R.id.discountedNoteEt);
        backBtn = findViewById(R.id.backBtn);

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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

        String discountNote = discountedNoteEt.getText().toString().trim();
        String discount = discountNote.trim();
        double discountRate = Double.parseDouble(discount.replace("%", "").trim()) / 100.0;
        double discountedPrice = price * (1 - discountRate);


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
                            // Product updated successfully
                            Toast.makeText(EditProductActivity.this, getString(R.string.toast_product_updated), Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK, resultIntent);
                            finish(); // Close the EditProductActivity screen
                        } else {
                            // Failed to update product
                            String errorMessage = getString(R.string.toast_update_failed, task.getException().getMessage());
                            Toast.makeText(EditProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
