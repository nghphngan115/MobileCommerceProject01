package com.group01.plantique;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordOTPActivity extends AppCompatActivity {
    private EditText edtOTPReset;
    private Button btnVerifyOTP;
    private String resetEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_otp);

        mAuth = FirebaseAuth.getInstance();

        edtOTPReset = findViewById(R.id.edtOTPReset);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        Intent intent = getIntent();
        resetEmail = intent.getStringExtra("email");

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOTP = edtOTPReset.getText().toString().trim();
                if (!enteredOTP.isEmpty()) {
                    verifyOTPAndResetPassword(enteredOTP);
                } else {
                    Toast.makeText(ResetPasswordOTPActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyOTPAndResetPassword(String enteredOTP) {
        mAuth.verifyPasswordResetCode(enteredOTP)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            // Mã OTP chính xác, chuyển đến màn hình đổi mật khẩu
                            Intent intent = new Intent(ResetPasswordOTPActivity.this, ChangePasswordActivity.class);
                            intent.putExtra("email", resetEmail);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordOTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
