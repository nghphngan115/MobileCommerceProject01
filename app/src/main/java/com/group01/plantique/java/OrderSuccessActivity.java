package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.group01.plantique.R;

public class OrderSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Get the order ID passed from the previous activity
        String orderId = getIntent().getStringExtra("ORDER_ID");

        TextView txtOrderIdConfirm = findViewById(R.id.txtOrderIdConfirm);
        txtOrderIdConfirm.setText(getString(R.string.strOrdID)+ " " + orderId);
        Button btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishHomeScreen();
            }
        });
    }

    public void finishHomeScreen() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}