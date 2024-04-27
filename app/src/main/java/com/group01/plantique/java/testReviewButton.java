package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.group01.plantique.R;

public class testReviewButton extends AppCompatActivity {
    private ImageButton backBtn, writeReviewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_review_button);

        // Khởi tạo
        writeReviewBtn = findViewById(R.id.writeReviewBtn);

        // Xử lý khi click vào writeReviewBtn, mở activity WriteReview
        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(testReviewButton.this, WriteReviewActivity.class);
                startActivity(intent1);
            }
        });
    }
}
