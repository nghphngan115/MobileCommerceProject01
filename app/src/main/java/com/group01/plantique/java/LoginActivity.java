package com.group01.plantique.java;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.java.ResetPasswordActivity;
import com.group01.plantique.model.User;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilUsername, tilPassword;
    private TextView txtForgotPassword, txtSignUp;
    private EditText edtUsername, edtPassword;
    private ImageButton backButton;
    private DatabaseReference usersRef;
    private ConstraintLayout btnSignIn;
    private int loginAttempts = 0;
    private SharedPreferences sharedPreferences;
    private String loggedInUserID;
    private static final String PREF_LOGIN_STATUS = "loginStatus";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        loggedInUserID = sharedPreferences.getString("userID", null);

        // Kiểm tra nếu đã có thông tin đăng nhập, tự động chuyển đến màn hình chính
        if (loggedInUserID != null && !loggedInUserID.isEmpty()) {
            startHomeScreen();
            return; // Không tiếp tục hiển thị màn hình đăng nhập nếu đã đăng nhập
        }

        backButton = findViewById(R.id.imgbtnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize views
        tilUsername = findViewById(R.id.tilUsername);
        edtUsername = findViewById(R.id.edtFullname);
        tilPassword = findViewById(R.id.tilPassword);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtSignUp = findViewById(R.id.txtSignUp);

        // Initialize Firebase
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Handle click events for Username and Password EditText
        handleEditTextClicks();

        // Handle focus change events for Username and Password EditText
        handleFocusChange();

        // Handle sign in button click
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
        int color = ContextCompat.getColor(LoginActivity.this, R.color.dark_green);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSignUp.setTextColor(color);
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtForgotPassword.setTextColor(color);
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleEditTextClicks() {
        // Handle click event for Username EditText
        edtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilUsername.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });

        // Handle click event for Password EditText
        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });
    }

    private void handleFocusChange() {
        // Handle focus change event for Username EditText
        edtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && TextUtils.isEmpty(edtUsername.getText().toString().trim())) {
                    tilUsername.setHint(getString(R.string.hint_enter_username));
                } else {
                    tilUsername.setHint("");
                }
            }
        });

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    tilPassword.setHint(getString(R.string.hint_enter_password));
                } else {
                    tilPassword.setHint("");
                }
            }
        });

    }
    // Đây là biến để lưu userID của người dùng sau khi đăng nhập thành công

    // Method to sign in user
    private void signInUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, getString(R.string.toast_enter_credentials), Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve user details from Firebase based on username
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            // Check if the entered password matches the stored hashed password
                            if (BCrypt.checkpw(password, user.getPassword())) {
                                loggedInUserID = user.getId();
                                saveUserIDToSharedPreferences(loggedInUserID); // Lưu userID vào SharedPreferences
                                saveLoginStatus(true); // Lưu trạng thái đăng nhập
                                Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(LoginActivity.this, getString(R.string.toast_signin_success), Toast.LENGTH_SHORT).show();
                            } else {
                                // Passwords do not match
                                loginAttempts++;
                                if (loginAttempts >= 3) {
                                    showResetPasswordDialog();
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.toast_invalid_password), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                } else {
                    // Username not found
                    Toast.makeText(LoginActivity.this, getString(R.string.toast_username_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String errorMessage = getString(R.string.toast_error_prefix) + databaseError.getMessage();
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginStatus(boolean status) {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_LOGIN_STATUS, status);
        editor.apply();
    }

    private String userID;

    // Method to save userID to SharedPreferences
    private void saveUserIDToSharedPreferences(String userID) {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", userID);
        editor.apply();
    }


    // Method to show reset password dialog
    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_reset_password_title));
        builder.setMessage(getString(R.string.dialog_reset_password_message));
        builder.setPositiveButton(getString(R.string.dialog_reset_password_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Navigate to ResetPasswordActivity
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_reset_password_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
