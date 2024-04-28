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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.CartUtility;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class CartActivity extends DrawerBaseActivity {

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
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ProductCategoriesActivity khi click vào nút "Tiếp tục mua hàng"
                startActivity(new Intent(CartActivity.this, ProductCategoriesActivity.class));
            }
        });

        // Thiết lập sự kiện click cho nút "Đặt hàng"
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ListOrderActivity khi click vào nút "Đặt hàng"
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });

        // Khởi tạo danh sách sản phẩm trong giỏ hàng từ SharedPreferences
        loadCartFromSharedPreferences();
    }

    // Phương thức để lấy dữ liệu giỏ hàng từ SharedPreferences
    private void loadCartFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cart", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        if (json != null) {
            cartProducts = gson.fromJson(json, type);
        } else {
            cartProducts = new ArrayList<>(); // Nếu không có dữ liệu, tạo một danh sách mới
        }
        // Hiển thị danh sách sản phẩm trong giỏ hàng
        showCart();
    }

    // Phương thức để lưu giỏ hàng vào SharedPreferences
    private void saveCartToSharedPreferences() {
        // Lưu danh sách sản phẩm vào SharedPreferences sử dụng CartUtility
        CartUtility.saveCartProducts(this, cartProducts);
    }

    private void showCart() {
        // Kiểm tra nếu giỏ hàng trống
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, getString(R.string.cart_empty_message), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set adapter cho ListView
        cartListAdapter = new CartListAdapter(this, cartProducts);
        lvCart.setAdapter(cartListAdapter);

        // Cập nhật tổng giá trị giỏ hàng
        updateTotalCart();
    }

    // Phương thức cập nhật tổng giá trị giỏ hàng
    private void updateTotalCart() {
        int totalCart = 0;
        for (Product product : cartProducts) {
            totalCart += product.getPrice() * product.getCartQuantity();
        }
        txtTotalCart.setText(String.valueOf(totalCart));
    }
}