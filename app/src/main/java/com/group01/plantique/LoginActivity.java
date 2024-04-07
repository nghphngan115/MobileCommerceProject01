package com.group01.plantique;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton backButton = findViewById(R.id.imgbtnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Tạo các đối tượng view và layout
        TextInputLayout tilUsername = findViewById(R.id.tilUsername);
        EditText edtUsername = findViewById(R.id.edtFullname);
        TextInputLayout tilPassword = findViewById(R.id.tilPassword);
        EditText edtPassword = findViewById(R.id.edtPassword);

        // Xử lý sự kiện click cho EditText Username
        edtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đổi màu viền của TextInputLayout khi EditText được click
                tilUsername.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });

        // Xử lý sự kiện click cho EditText Password
        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đổi màu viền của TextInputLayout khi EditText được click
                tilPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });
        // Xử lý sự kiện focus cho EditText Username
        edtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edtUsername.getText().toString().isEmpty()) {
                    // Nếu không còn tập trung và không có văn bản, hiển thị hint
                    tilUsername.setHint("Enter username");
                } else {
                    // Nếu có tập trung hoặc có văn bản, ẩn hint
                    tilUsername.setHint("");
                }
            }
        });

        // Xử lý sự kiện focus cho EditText Password
        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edtPassword.getText().toString().isEmpty()) {
                    // Nếu không còn tập trung và không có văn bản, hiển thị hint
                    tilPassword.setHint("Enter password");
                } else {
                    // Nếu có tập trung hoặc có văn bản, ẩn hint
                    tilPassword.setHint("");
                }
            }
        });
    }
}
