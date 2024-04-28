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
    private DatabaseReference productsRef;
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

        // Khởi tạo danh sách sản phẩm trong giỏ hàng
        cartProducts = new ArrayList<>();
        loadCartFromSharedPreferences();

        Intent intent = getIntent();
        if (intent != null) {
            String productId = intent.getStringExtra("productId");

            if (productId != null) {
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products").child(productId);
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String productName = dataSnapshot.child("productName").getValue(String.class);
                            int price = dataSnapshot.child("price").getValue(Integer.class);
                            String imageurl = dataSnapshot.child("imageurl").getValue(String.class);


                            String categoryId = dataSnapshot.child("categoryId").getValue(String.class);
                            String description = dataSnapshot.child("description").getValue(String.class);
                            String discountNote = dataSnapshot.child("discountNote").getValue(String.class);
                            int discount_price = dataSnapshot.child("discount_price").getValue(Integer.class);
                            int stock = dataSnapshot.child("stock").getValue(Integer.class);
                            String unit = dataSnapshot.child("unit").getValue(String.class);
                            Product product = new Product(productId, productName, price, imageurl, categoryId, description, discountNote, discount_price, stock, unit);
                            product.setCartQuantity(1);

                            cartProducts.add(product);


                            showCart();
                        } else {
                            Toast.makeText(CartActivity.this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching product details", databaseError.toException());
                        Toast.makeText(CartActivity.this, "Lỗi khi lấy thông tin sản phẩm từ Firebase", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ProductListActivity khi click vào nút "Tiếp tục mua hàng"
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
    }

    // Phương thức để lấy dữ liệu giỏ hàng từ SharedPreferences
    private void loadCartFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cart", null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        cartProducts = gson.fromJson(json, type);
        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }
    }

    // Phương thức để lưu giỏ hàng vào SharedPreferences
    private void saveCartToSharedPreferences() {
        Log.d("CartActivity", "saveCartToSharedPreferences: Saving cart data to SharedPreferences");
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartProducts);
        editor.putString("cart", json);
        editor.putInt("totalAmount", calculateTotalAmount());  // Lưu tổng tiền vào SharedPreferences
        editor.apply();
    }

    private int calculateTotalAmount() {
        int total = 0;
        for (Product product : cartProducts) {
            total += product.getPrice() * product.getCartQuantity();
        }
        return total;
    }

    private void showCart() {
        // Kiểm tra nếu giỏ hàng trống
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set adapter cho ListView
        cartListAdapter = new CartListAdapter(this, cartProducts);
        lvCart.setAdapter(cartListAdapter);

        // Thiết lập lắng nghe sự kiện thay đổi số lượng sản phẩm
        cartListAdapter.setOnQuantityChangeListener(new CartListAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged() {
                // Lưu lại mỗi khi số lượng thay đổi
                saveCartToSharedPreferences();

                // Cập nhật tổng giá trị giỏ hàng
                updateTotalCart();

                // Kiểm tra số lượng nhập vào so với số lượng tồn kho
                for (Product product : cartProducts) {
                    if (product.getCartQuantity() > product.getStock()) {
                        // Hiển thị thông điệp cảnh báo về số lượng tồn kho hiện tại
                        String warningMessage = "Vượt số lượng tồn kho. Tồn kho hiện tại " + product.getStock() + " sản phẩm.";
                        Toast.makeText(CartActivity.this, warningMessage, Toast.LENGTH_SHORT).show();

                        // Đặt số lượng sản phẩm trong giỏ hàng về số lượng tồn kho hiện có
                        product.setCartQuantity(product.getStock());
                        cartListAdapter.notifyDataSetChanged(); // Cập nhật lại adapter
                    }
                }
            }
        });

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