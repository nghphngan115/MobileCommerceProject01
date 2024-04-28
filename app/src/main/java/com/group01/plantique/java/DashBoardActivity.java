package com.group01.plantique.java;

import android.os.Bundle;

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

    }
}