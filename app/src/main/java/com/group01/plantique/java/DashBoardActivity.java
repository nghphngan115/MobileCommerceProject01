package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group01.plantique.R;

public class DashBoardActivity extends AppCompatActivity {
    ConstraintLayout btnProduct, btnOrders, btnPromotion, btnCustomer, btnBlog, btnFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        btnProduct = findViewById(R.id.btnProduct);
        btnOrders = findViewById(R.id.btnOrders);
        btnPromotion = findViewById(R.id.btnPromotion);
        btnCustomer = findViewById(R.id.btnCustomer);
        btnBlog = findViewById(R.id.btnBlog);
        btnFeedback = findViewById(R.id.btnFeedback);

        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, AdminProductActivity.class);
            }
        });
        btnPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, AdminPromotionActivity.class);
            }
        });
        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, AdminOrderListActivity.class);
            }
        });
    }
}