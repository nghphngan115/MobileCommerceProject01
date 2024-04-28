package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.group01.plantique.R;

import java.util.concurrent.TimeUnit;

public class ResetPasswordOTPActivity extends AppCompatActivity {
    private EditText edtOTP;
    private Button btnVerifyOTP;
    private TextView resendOtpTextView;
    private ProgressBar progressBar;
    private String phoneNumber;
    private String verificationId;
    private FirebaseAuth mAuth;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000; // 60 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_otp);

        mAuth = FirebaseAuth.getInstance();

        edtOTP = findViewById(R.id.edtOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        verificationId = intent.getStringExtra("verificationId");

        startCountdownTimer();

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOTP = edtOTP.getText().toString().trim();
                if (!enteredOTP.isEmpty()) {
                    verifyOTPAndProceed(enteredOTP);
                } else {
                    Toast.makeText(ResetPasswordOTPActivity.this, getString(R.string.enter_otp_message), Toast.LENGTH_SHORT).show();
                }
            }
        });

        resendOtpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
            }
        });
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                // Enable Resend OTP button after timer finishes
                resendOtpTextView.setEnabled(true);
                resendOtpTextView.setText("Resend OTP");
            }
        }.start();
    }

    private void updateCountdownText() {
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String resendMessage = getString(R.string.resend_otp_format, seconds);
        resendOtpTextView.setText(resendMessage);
    }

    private void verifyOTPAndProceed(String enteredOTP) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ResetPasswordOTPActivity.this, ChangePasswordActivity.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordOTPActivity.this, R.string.otp_verification_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void resendOTP() {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ResetPasswordOTPActivity.this, R.string.failed_resend_otp, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(newVerificationId, forceResendingToken);
                        verificationId = newVerificationId;
                        startCountdownTimer();
                        Toast.makeText(ResetPasswordOTPActivity.this, R.string.otp_resent, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
