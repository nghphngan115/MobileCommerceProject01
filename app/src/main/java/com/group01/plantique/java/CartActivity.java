package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;

import java.util.ArrayList;

public class CartActivity extends DrawerBaseActivity {

    private TextView txtTotalCart;
    private ListView lvCart;
    private ArrayList<Product> cartProducts;
    private CartListAdapter cartListAdapter;
    private DatabaseReference productsRef;
    private ConstraintLayout btnContinue;
    private ConstraintLayout btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        txtTotalCart = findViewById(R.id.txtTotalCart);
        btnContinue = findViewById(R.id.btnContinue);
        btnOrder = findViewById(R.id.btnOrder);
        lvCart= findViewById(R.id.lvCart);
        cartProducts = new ArrayList<>();
        productsRef = FirebaseDatabase.getInstance().getReference().child("products");

        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartProducts.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setQuantity(0);
                        cartProducts.add(product);
                    }
                }
                showCart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data", databaseError.toException());
                Toast.makeText(CartActivity.this, "Lỗi khi lấy dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, ProductListActivity.class));
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cartProducts.isEmpty()) {
                    if (validateQuantities()) { // Validate quantities before placing order
                        Gson gson = new Gson();
                        String productListJson = gson.toJson(cartProducts);

                        int totalCartValue = calculateTotalCartValue();
                        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                        intent.putExtra("productListJson", productListJson);
                        intent.putExtra("totalCartValue", totalCartValue);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateQuantities() {
        boolean valid = true;
        for (int i = 0; i < cartProducts.size(); i++) {
            View view = lvCart.getChildAt(i - lvCart.getFirstVisiblePosition());
            if (view != null) {
                EditText quantityText = view.findViewById(R.id.edtProductQuantity);
                int quantity = Integer.parseInt(quantityText.getText().toString());
                if (quantity > cartProducts.get(i).getStock()) {
                    Toast.makeText(this, "Not enough stock for " + cartProducts.get(i).getProductName(), Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            }
        }
        return valid;
    }

    private void showCart() {
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cartListAdapter == null) {
            cartListAdapter = new CartListAdapter(this, cartProducts);
            lvCart.setAdapter(cartListAdapter);
        } else {
            cartListAdapter.notifyDataSetChanged();
        }
        calculateTotalCartValue();
    }

    private int calculateTotalCartValue() {
        int total = 0;
        for (Product product : cartProducts) {
            total += product.getPrice() * product.getQuantity();
        }
        txtTotalCart.setText(String.format("%s đ", total));
        return total;
    }

    private void addToCart(Product product) {
        product.setQuantity(1);
        cartProducts.add(product);
        showCart();
    }

    private void updateQuantity(Product product, int newQuantity) {
        if (newQuantity >= 0 && newQuantity <= product.getStock()) {
            product.setQuantity(newQuantity);
            showCart();
        } else {
            Toast.makeText(this, "Invalid quantity or exceeds available stock.", Toast.LENGTH_SHORT).show();
        }
    }
}
