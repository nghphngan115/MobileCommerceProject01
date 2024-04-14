package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.group01.plantique.R;

public class CheckoutActivity extends AppCompatActivity {
ConstraintLayout btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        addViews();
        addEvents();
    }


    private void addViews() {
        btnConfirm=findViewById(R.id.btnConfirm);
    }
    private void addEvents() {
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivity(new Intent(CheckoutActivity.this, OrderConfirmActivity.class));
                }
            });
        }

    }
