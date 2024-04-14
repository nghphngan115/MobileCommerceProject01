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

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmailReset);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "Reset email sent. Check your email for instructions.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordOTPActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
