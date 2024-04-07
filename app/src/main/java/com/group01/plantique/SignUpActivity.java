package com.group01.plantique;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ImageButton backButton = findViewById(R.id.imgbtnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Tạo các đối tượng view và layout
        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        EditText edtEmail = findViewById(R.id.edtEmail);
        TextInputLayout tilUsername = findViewById(R.id.tilUsername);
        EditText edtUsername = findViewById(R.id.edtUsername);
        TextInputLayout tilPassword = findViewById(R.id.tilPassword);
        EditText edtPassword = findViewById(R.id.edtPassword);
        TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        EditText edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        // Xử lý sự kiện click cho EditText Email
        edtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilEmail.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });

        // Xử lý sự kiện click cho EditText Username
        edtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilUsername.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });

        // Xử lý sự kiện click cho EditText Password
        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });

        // Xử lý sự kiện click cho EditText Confirm Password
        edtConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilConfirmPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });

        // Xử lý sự kiện focus cho EditText Email
        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edtEmail.getText().toString().isEmpty()) {
                    tilEmail.setHint("Enter email");
                } else {
                    tilEmail.setHint("");
                }
            }
        });

        // Xử lý sự kiện focus cho EditText Username
        edtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edtUsername.getText().toString().isEmpty()) {
                    tilUsername.setHint("Enter username");
                } else {
                    tilUsername.setHint("");
                }
            }
        });

        // Xử lý sự kiện focus cho EditText Password
        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edtPassword.getText().toString().isEmpty()) {
                    tilPassword.setHint("Enter password");
                } else {
                    tilPassword.setHint("");
                }
            }
        });

        // Xử lý sự kiện focus cho EditText Confirm Password
        edtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && edtConfirmPassword.getText().toString().isEmpty()) {
                    tilConfirmPassword.setHint("Confirm password");
                } else {
                    tilConfirmPassword.setHint("");
                }
            }
        });
    }
}
