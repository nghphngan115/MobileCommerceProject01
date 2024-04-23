package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.group01.plantique.model.User;

import org.mindrot.jbcrypt.BCrypt;

public class SignUpActivity extends AppCompatActivity {
    private DatabaseReference usersRef;
    private EditText edtEmail, edtPassword, edtConfirmPassword, edtUsername, edtPhoneNumber;
    private TextView txtUsernameError, txtPhoneError, txtPasswordError, txtConfirmPasswordError, txtEmailError, txtSignUp;
    private ConstraintLayout signUpButton;
    private TextInputLayout tilEmail, tilPhoneNumber, tilUsername, tilPassword, tilConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Realtime Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtUsername = findViewById(R.id.edtFullname);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        tilEmail =findViewById(R.id.tilEmail);
        tilPassword =findViewById(R.id.tilPassword);
        tilPhoneNumber =findViewById(R.id.tilPhoneNumber);
        tilConfirmPassword =findViewById(R.id.tilConfirmPassword);
        tilUsername = findViewById(R.id.tilUsername);
        txtUsernameError = findViewById(R.id.txtUsernameError);
        txtPhoneError = findViewById(R.id.txtPhoneError);
        txtPasswordError =findViewById(R.id.txtPasswordError);
        txtConfirmPasswordError =findViewById(R.id.txtConfirmPasswordError);
        txtEmailError =findViewById(R.id.txtEmailError);

        // Initialize Sign Up button
        signUpButton = findViewById(R.id.btnSignUp);
        signUpButton.setOnClickListener(v -> {
            signUpUser();
        });
        txtSignUp = findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(v -> {
            signUpUser();
            txtSignUp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_green)));
        });


        // Set onFocusChangeListener to hide hint when EditText is focused
        setEditTextFocusChangeListeners();
        setEditTextOnClickListener();

        // Handle back button click
        ImageButton backButton = findViewById(R.id.imgbtnBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void setEditTextOnClickListener() {
        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });
        edtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilEmail.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });
        edtConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilConfirmPassword.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });
        edtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilUsername.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });
        edtPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilPhoneNumber.setBoxStrokeColor(getResources().getColor(R.color.main_green));
            }
        });
    }


    private void signUpUser() {
        final String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        final String username = edtUsername.getText().toString();
        String phone = edtPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(username) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true); // Re-enable button
            txtSignUp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_green)));
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true); // Re-enable button
            return;
        }

        // Check email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Invalid email format");
            signUpButton.setEnabled(true);
            return;
        }

        // Check password complexity
        if (!isPasswordValid(password)) {
            edtPassword.setError("Password must be at least 8 characters, contain a digit, and a special character");
            signUpButton.setEnabled(true);
            return;
        }

        // Check if username or email already exists
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    edtUsername.setError("Username already exists");
                    signUpButton.setEnabled(true);
                } else {
                    usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                edtEmail.setError("Email already exists");
                                signUpButton.setEnabled(true);
                            } else {
                                // Hash the password using BCrypt
                                String hashedPassword = hashPassword(password);

                                // Save user information to Realtime Database with custom user ID
                                String userId = "UID_" + username; // Create a custom user ID
                                User newUser = new User(userId, username, email, phone, hashedPassword);
                                usersRef.child(userId).setValue(newUser);
                                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                // Redirect to login or home activity
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(SignUpActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                            signUpButton.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                signUpButton.setEnabled(true);
            }
        });
    }

    private void setEditTextFocusChangeListeners() {
        // Set onFocusChangeListener for Phone EditText
        edtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilPhoneNumber.setHint("");
                txtPhoneError.setVisibility(View.GONE);
            } else {
                String phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    tilPhoneNumber.setHint("Enter phone number");
                } else {
                    txtPhoneError.setVisibility(View.GONE);
                }
            }
        });
        // Set onFocusChangeListener for Confirm Password EditText
        edtConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String confirmPass = edtConfirmPassword.getText().toString().trim();
                if (!confirmPass.equals(edtPassword.getText().toString())) {
                    txtConfirmPasswordError.setVisibility(View.VISIBLE);
                    txtConfirmPasswordError.setText("Passwords do not match");
                } else {
                    txtConfirmPasswordError.setVisibility(View.GONE);
                }
            }
            tilConfirmPassword.setHint(hasFocus || !TextUtils.isEmpty(edtConfirmPassword.getText().toString()) ? "" : "Enter Confirm Password");
        });

        // Set onFocusChangeListener for Email EditText
        edtEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = edtEmail.getText().toString().trim();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmailError.setVisibility(View.VISIBLE);
                    txtEmailError.setText("Invalid email format");
                } else {
                    txtEmailError.setVisibility(View.GONE);
                }
            }
            tilEmail.setHint(hasFocus || !TextUtils.isEmpty(edtEmail.getText().toString()) ? "" : "Enter Email");
        });

        // Set onFocusChangeListener for Password EditText
        edtPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = edtPassword.getText().toString().trim();
                if (!isPasswordValid(password)) {
                    txtPasswordError.setVisibility(View.VISIBLE);
                    txtPasswordError.setText("Password must be at least 8 characters, contain a digit, and a special character");
                } else {
                    txtPasswordError.setVisibility(View.GONE);
                }
            }
            tilPassword.setHint(hasFocus || !TextUtils.isEmpty(edtPassword.getText().toString()) ? "" : "Enter Password");
        });

        // Set onFocusChangeListener for Username EditText
        edtUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilUsername.setHint("");
                txtUsernameError.setVisibility(View.GONE);
            } else {
                String username = edtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    tilUsername.setHint("Enter username");
                } else if (!isUsernameValid(username)) {
                    txtUsernameError.setVisibility(View.VISIBLE);
                    txtUsernameError.setText("Username must contain both letters and numbers");
                } else {
                    txtUsernameError.setVisibility(View.GONE);
                }
            }
        });
    }


    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }

    private boolean isUsernameValid(String username) {
        // Simple validation for username (contains at least one letter and one digit)
        return username.matches(".*[a-zA-Z].*") && username.matches(".*\\d.*");
    }

    private String hashPassword(String password) {
        // Hash the password using BCrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
