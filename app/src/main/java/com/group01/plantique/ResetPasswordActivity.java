package com.group01.plantique;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtPhoneNumber;
    private Button btnSendOTP;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        progressBar = findViewById(R.id.progressBar);

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    sendOTPToPhoneNumber(phoneNumber);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendOTPToPhoneNumber(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // Xác thực tự động hoàn tất (không cần nhập mã OTP)
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ResetPasswordActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordOTPActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("verificationId", verificationId);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
