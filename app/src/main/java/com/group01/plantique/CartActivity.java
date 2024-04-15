package com.group01.plantique;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView lvCart;
    private TextView txtTotalCart;
    private ArrayList<Product> cartProducts;
    private CartListAdapter cartListAdapter;
    private ConstraintLayout btnOrderLayout;
    private ConstraintLayout btnContinueShoppingLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        lvCart = findViewById(R.id.lvCart);
        txtTotalCart = findViewById(R.id.txtTotalCart);
        btnOrderLayout = findViewById(R.id.btnOrder);
        btnContinueShoppingLayout = findViewById(R.id.btnContinueShopping);

        // Khởi tạo giỏ hàng và adapter
        cartProducts = getCartProducts();
        if (cartProducts.isEmpty()) {
            // Nếu giỏ hàng trống, hiển thị thông báo và kết thúc activity
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tính toán và hiển thị tổng giá trị của giỏ hàng
        int totalCartValue = calculateTotalCartValue(cartProducts);
        txtTotalCart.setText(String.valueOf(totalCartValue) + " đ");

        // Sự kiện click cho nút "Tiến hành đặt hàng"
        btnOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình ListOrderActivity để tiến hành đặt hàng
                Intent intent = new Intent(CartActivity.this, ListOrderActivity.class);
                startActivity(intent);
            }
        });

        // Sự kiện click cho nút "Tiếp tục mua sắm"
        btnContinueShoppingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay trở về activity trước đó
                finish();
            }
        });

        // Set adapter cho ListView
        cartListAdapter = new CartListAdapter(this, cartProducts);
        lvCart.setAdapter(cartListAdapter);
    }

    // Phương thức để tính toán tổng giá trị của giỏ hàng
    private int calculateTotalCartValue(ArrayList<Product> products) {
        int total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    // Phương thức này cần được triển khai để lấy danh sách sản phẩm trong giỏ hàng
    private ArrayList<Product> getCartProducts() {
        // Trả về danh sách sản phẩm trong giỏ hàng, có thể làm từ cơ sở dữ liệu hoặc lưu trữ tạm thời
        // Đây chỉ là một phương thức mẫu, bạn cần thay thế bằng logic thích hợp
        ArrayList<Product> products = new ArrayList<>();
        // Thêm các sản phẩm vào danh sách
        return products;
    }
}
