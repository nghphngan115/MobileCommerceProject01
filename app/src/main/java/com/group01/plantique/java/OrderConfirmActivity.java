package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.group01.plantique.R;

public class OrderConfirmActivity extends AppCompatActivity {
ConstraintLayout btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
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
                showDialog();
            }
        });
    }

    private void showDialog() {
        // Tạo một instance của Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmation); // Sử dụng layout đã thiết kế

        // Cấu hình các thuộc tính của Dialog nếu cần
        dialog.setTitle("Confirmation");
        dialog.setCancelable(true); // Cho phép hủy Dialog khi nhấn ra ngoài

        // Hiển thị Dialog
        dialog.show();
    }
}