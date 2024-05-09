package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.group01.plantique.R;

import java.util.concurrent.TimeUnit;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtPhoneNumber;
    private Button btnSendOTP;
    private ImageButton imgbtnBack;
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
        imgbtnBack = findViewById(R.id.imgbtnBack);

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    sendOTPToPhoneNumber(phoneNumber);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.enter_phone_number), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendOTPToPhoneNumber(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        btnSendOTP.setVisibility(View.INVISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84"+edtPhoneNumber.getText().toString(),
                60,
                TimeUnit.SECONDS,
                ResetPasswordActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                btnSendOTP.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ResetPasswordActivity.this, getString(R.string.failed_to_send_otp), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                btnSendOTP.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                btnSendOTP.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(getApplicationContext(), ResetPasswordOTPActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("verificationId", verificationId);
                                startActivity(intent);
                                finish();
                            }
                        }
                );

    }
}
