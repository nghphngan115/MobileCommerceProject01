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
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CartActivity  extends AppCompatActivity {

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

        BottomNavigationView bottomNavigationView =findViewById(R.id.bottomNavigationView);
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

        // Lấy tham chiếu đến cơ sở dữ liệu Firebase
        productsRef = FirebaseDatabase.getInstance().getReference().child("products");

        // Thực hiện lắng nghe sự kiện khi dữ liệu thay đổi
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Duyệt qua danh sách sản phẩm
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    // Lấy thông tin về sản phẩm
                    String productId = productSnapshot.getKey();
                    String productName = productSnapshot.child("productName").getValue(String.class);
                    String description = productSnapshot.child("description").getValue(String.class);
                    int price = productSnapshot.child("price").getValue(Integer.class);
                    int discountPrice = productSnapshot.child("discount_price").getValue(Integer.class);
                    String imageUrl = productSnapshot.child("imageurl").getValue(String.class);
                    String categoryId = productSnapshot.child("categoryId").getValue(String.class);
                    int stock = productSnapshot.child("stock").getValue(Integer.class); // Lấy số lượng từ Firebase
                    // Tạo đối tượng sản phẩm với thông tin từ Firebase
                    String discountNote = productSnapshot.child("discountNote").getValue(String.class);
                    String unit = productSnapshot.child("unit").getValue(String.class);
                    Product product = new Product(productId, productName, description, price, discountPrice, imageUrl, categoryId, discountNote, unit, stock);

                    // Thêm sản phẩm vào giỏ hàng
                    cartProducts.add(product);
                }

                // Hiển thị danh sách sản phẩm trong giỏ hàng
                showCart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Firebase", "Error fetching data", databaseError.toException());
                Toast.makeText(CartActivity.this, "Lỗi khi lấy dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập sự kiện click cho nút "Tiếp tục mua hàng"
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ProductListActivity khi click vào nút "Tiếp tục mua hàng"
                startActivity(new Intent(CartActivity.this, ProductListActivity.class));
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

    // Phương thức để hiển thị danh sách sản phẩm trong giỏ hàng
    private void showCart() {
        // Kiểm tra nếu giỏ hàng trống
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set adapter cho ListView
        // Set adapter cho ListView
        cartListAdapter = new CartListAdapter(this, cartProducts);
        lvCart.setAdapter(cartListAdapter);

        // Thiết lập lắng nghe sự kiện thay đổi số lượng sản phẩm
        cartListAdapter.setOnQuantityChangeListener(new CartListAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged() {
                saveCartToSharedPreferences();  // Lưu lại mỗi khi số lượng thay đổi
                updateTotalCart();  // Cập nhật tổng giá trị giỏ hàng
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

    // Phương thức để lưu giỏ hàng vào SharedPreferences
    private void saveCartToSharedPreferences() {
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
}
