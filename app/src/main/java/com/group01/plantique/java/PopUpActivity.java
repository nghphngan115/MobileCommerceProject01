package com.group01.plantique.java;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.group01.plantique.R;

public class PopUpActivity extends AppCompatActivity {
    private ImageView productlv;
    private TextView productTitleTv, finalPriceTv, productQuantityTv;
    private ImageButton btnMinus, btnPlus;
    private int quantity = 1;
    private int price; // Giá của sản phẩm

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Không sử dụng setContentView, sử dụng Dialog thay vào đó
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_pop_up);
        dialog.setCancelable(true); // Cho phép hủy bỏ bằng cách nhấn ra bên ngoài

        // Khởi tạo các thành phần UI
        productTitleTv = dialog.findViewById(R.id.productTitleTv);
        finalPriceTv = dialog.findViewById(R.id.finalPriceTv);
        productQuantityTv = dialog.findViewById(R.id.productQuantityTv);
        btnMinus = dialog.findViewById(R.id.btnMinus);
        btnPlus = dialog.findViewById(R.id.btnPlus);

        // Lấy thông tin sản phẩm từ Intent và hiển thị
        Intent intent = getIntent();
        if (intent != null) {
            String productName = intent.getStringExtra("productName");
            price = intent.getIntExtra("price", 0);
            productTitleTv.setText(productName);
            updateFinalPrice();
        }

        // Sự kiện cho nút minus
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    productQuantityTv.setText(String.valueOf(quantity));
                    updateFinalPrice();
                }
            }
        });

        // Sự kiện cho nút plus
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                productQuantityTv.setText(String.valueOf(quantity));
                updateFinalPrice();
            }
        });

        // Sự kiện khi click vào nút "Add To Cart"
        findViewById(R.id.btnAddToCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Truyền lại dữ liệu số lượng đã chọn về cho CartActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("quantity", quantity);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Hiển thị dialog
        dialog.show();
    }

    // Phương thức để cập nhật giá cuối cùng dựa trên số lượng và giá của sản phẩm
    private void updateFinalPrice() {
        int finalPrice = price * quantity;
        finalPriceTv.setText(String.valueOf(finalPrice));
    }
}
