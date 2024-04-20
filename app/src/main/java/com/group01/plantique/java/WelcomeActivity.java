package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;

import org.mindrot.jbcrypt.BCrypt;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView signInTextView = findViewById(R.id.txtSignIn);
        TextView signUpTextView = findViewById(R.id.txtSignUp);

        // Set click listener for Sign in TextView
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apply animation
                animateView(v);

                // Start LoginActivity
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for Sign up TextView
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apply animation
                animateView(v);

                // Start SignUpActivity
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void animateView(View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        view.startAnimation(anim);
    }

    public static class ChangePasswordActivity extends AppCompatActivity {
        private EditText edtNewPassword;
        private EditText edtConfirmPassword;
        private Button btnChangePassword;
        private FirebaseAuth mAuth;
        private DatabaseReference mDatabase;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_change_password);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

            edtNewPassword = findViewById(R.id.edtNewPassword);
            edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
            btnChangePassword = findViewById(R.id.btnChangePassword);

            btnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePassword();
                }
            });
        }

        private void changePassword() {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please enter new password and confirm password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            final String phoneNumber = getIntent().getStringExtra("phoneNumber");
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Phone number not found", Toast.LENGTH_SHORT).show();
                return;
            }
            // Mã hóa mật khẩu trước khi lưu vào database
            final String hashedPassword = hashPassword(newPassword);

            Query query = mDatabase.orderByChild("phone").equalTo(phoneNumber);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = snapshot.getKey();
                            mDatabase.child(userId).child("password").setValue(hashedPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                finish(); // Đóng activity sau khi thay đổi mật khẩu thành công
                                            } else {
                                                Toast.makeText(ChangePasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChangePasswordActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        private String hashPassword(String password) {
            // Hash the password using BCrypt
            return BCrypt.hashpw(password, BCrypt.gensalt());
        }
    }
}
