package com.group01.plantique.java;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;

import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private Button btnChangePassword;
    private TextView txtPasswordError, txtConfirmPasswordError;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextInputLayout tilPassword, tilConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setEditTextFocusChangeListeners();
        setEditTextOnClickListener();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        tilPassword =findViewById(R.id.tilPassword);
        tilConfirmPassword =findViewById(R.id.tilConfirmPassword);
        txtPasswordError =findViewById(R.id.txtPasswordError);
        txtConfirmPasswordError =findViewById(R.id.txtConfirmPasswordError);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void setEditTextFocusChangeListeners() {
        if (edtConfirmPassword != null) {
            edtConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String confirmPassword = edtConfirmPassword.getText().toString().trim();
                    String newPassword = edtNewPassword.getText().toString().trim();
                    if (!confirmPassword.equals(newPassword)) {
                        txtConfirmPasswordError.setVisibility(View.VISIBLE);
                        txtConfirmPasswordError.setText(getString(R.string.error_passwords_not_match));
                    } else {
                        txtConfirmPasswordError.setVisibility(View.GONE);
                    }
                }
                tilConfirmPassword.setHint(hasFocus || !TextUtils.isEmpty(edtConfirmPassword.getText().toString()) ? "" : getString(R.string.hint_confirm_password));
            });
        }

        if (edtNewPassword != null) {
            edtNewPassword.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String newPassword = edtNewPassword.getText().toString().trim();
                    if (!isPasswordValid(newPassword)) {
                        txtPasswordError.setVisibility(View.VISIBLE);
                        txtPasswordError.setText(getString(R.string.error_invalid_password));
                    } else {
                        txtPasswordError.setVisibility(View.GONE);
                    }
                }
                tilPassword.setHint(hasFocus || !TextUtils.isEmpty(edtNewPassword.getText().toString()) ? "" : getString(R.string.hint_password));
            });
        }
    }

    private void setEditTextOnClickListener() {
        if (edtNewPassword != null && edtConfirmPassword != null) {
            edtNewPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tilPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
                }
            });
            edtConfirmPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tilConfirmPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
                }
            });
        }
    }

    private void changePassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, getString(R.string.toast_enter_passwords), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.toast_passwords_not_match), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isPasswordValid(newPassword)) {
            edtNewPassword.setError(getString(R.string.invalid_password_format));
            return;
        }

        final String phoneNumber = getIntent().getStringExtra("phoneNumber");
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_phone_number_not_found), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.toast_password_changed), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.toast_password_change_failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.toast_user_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.toast_database_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isPasswordValid(String newPassword) {
        return newPassword.length() >= 8 && newPassword.matches(".*\\d.*") && newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
    private String hashPassword(String password) {
        // Hash the password using BCrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}


