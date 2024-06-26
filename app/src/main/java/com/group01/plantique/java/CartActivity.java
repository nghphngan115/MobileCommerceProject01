package com.group01.plantique.java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.CartUtility;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView lvCart;
    private TextView txtTotalCart;
    private ArrayList<Product> cartProducts;
    private CartListAdapter cartListAdapter;
    private ConstraintLayout btnContinue;
    private ConstraintLayout btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.cart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.blog) {
                startActivity(new Intent(getApplicationContext(), BlogCategoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.cart) {
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

        lvCart = findViewById(R.id.lvCart);
        btnContinue = findViewById(R.id.btnContinue);
        btnOrder = findViewById(R.id.btnOrder);
        txtTotalCart = findViewById(R.id.txtTotalCart);

        btnOrder.setOnClickListener(v -> {
            if (cartProducts.isEmpty()) {
                Toast.makeText(CartActivity.this, getString(R.string.cart_empty_message), Toast.LENGTH_LONG).show();
                return; // Prevent proceeding to checkout if the cart is empty
            }

            boolean stockIssue = false;
            for (Product product : cartProducts) {
                if (product.getCartQuantity() > product.getStock()) {
                    product.setCartQuantity(product.getStock());  // Adjust the quantity to the maximum stock available
                    Toast.makeText(CartActivity.this, getString(R.string.product_stock_message, product.getProductName(), product.getStock()), Toast.LENGTH_LONG).show();

                    stockIssue = true;
                }
            }

            if (stockIssue) {
                cartListAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the views
                saveCartToSharedPreferences();  // Save the adjusted cart to SharedPreferences
                updateTotalCart();  // Update the total cart value shown
            } else {
                // If there are no stock issues, proceed to checkout
                Gson gson = new Gson();
                String cartJson = gson.toJson(cartProducts);
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("cartJson", cartJson);
                startActivity(intent);
            }
        });



        btnContinue.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, ProductCategoriesActivity.class));
        });

        // Initialize the product list from SharedPreferences
        loadCartFromSharedPreferences();
    }

    private void loadCartFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cart", null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        cartProducts = gson.fromJson(json, type);

        if (cartProducts == null) {
            cartProducts = new ArrayList<>(); // Create a new list if no data is found
        }
        showCart();
        setupCartListView();
    }

    private void saveCartToSharedPreferences() {
        CartUtility.saveCartProducts(this, cartProducts);
    }

    private void setupCartListView() {
        cartListAdapter = new CartListAdapter(this, cartProducts);
        lvCart.setAdapter(cartListAdapter);
        cartListAdapter.setOnQuantityChangeListener(() -> {
            updateTotalCart();
            saveCartToSharedPreferences();// This method recalculates the total whenever a quantity changes
        });
    }


    private void showCart() {
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, getString(R.string.cart_empty_message), Toast.LENGTH_SHORT).show();

            return;
        }

        updateTotalCart();  // Ensure the total is updated when showing the cart
    }

    private void updateTotalCart() {
        int totalCart = 0;
        for (Product product : cartProducts) {
            int effectivePrice = product.getDiscount_price() > 0 ? product.getDiscount_price() : product.getPrice();
            totalCart += effectivePrice * product.getCartQuantity();
        }
        txtTotalCart.setText(String.format("%dđ", totalCart));
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadCartFromSharedPreferences();
    }
}
