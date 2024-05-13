package com.group01.plantique.java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        txtPasswordError = findViewById(R.id.txtPasswordError);
        txtConfirmPasswordError = findViewById(R.id.txtConfirmPasswordError);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        Log.d("ChangePasswordActivity", "Phone Number: " + phoneNumber);


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        setEditTextFocusChangeListeners();
        setEditTextOnClickListener();
    }

    private void setEditTextFocusChangeListeners() {
        if (edtNewPassword != null && edtConfirmPassword != null) {
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
        } else {
            Log.e("ChangePasswordActivity", "EditText is null");
        }
    }
    private void setEditTextOnClickListener() {
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
    private boolean isPasswordValid(String newPassword) {
        return newPassword.length() >= 8 && newPassword.matches(".*\\d.*") && newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
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
                                            handleSuccessfulPasswordChange();
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

    private void handleSuccessfulPasswordChange() {
        Toast.makeText(ChangePasswordActivity.this, getString(R.string.toast_password_changed), Toast.LENGTH_SHORT).show();
        clearLoginStatus(); // Xóa trạng thái đăng nhập và thông tin đăng nhập khác
        goToLoginScreen();
    }
    private void goToLoginScreen() {
        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Kết thúc activity hiện tại để người dùng không thể quay lại màn hình đã đổi mật khẩu
    }
    private void clearLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userID"); // Xóa userID hoặc các thông tin đăng nhập khác
        editor.remove("loginStatus"); // Xóa trạng thái đăng nhập
        editor.apply();
    }

    private String hashPassword(String password) {
        // Hash the password using BCrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}


