package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.group01.plantique.R;

public class PromotionCodesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_codes);

        // Xử lý sự kiện khi nhấn nút "back"
        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng Activity hiện tại và quay trở về Activity trước đó
               onBackPressed();
            }
        });

        // Xử lý sự kiện khi nhấn nút "add"
        ImageButton addButton = findViewById(R.id.btnAddPromotion);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PromotionCodesActivity.this, AddPromotionCodeActivity.class);
                startActivity(intent);
            }
        });
    }
}
