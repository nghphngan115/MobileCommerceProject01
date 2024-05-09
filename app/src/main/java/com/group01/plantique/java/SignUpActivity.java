package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.User;

import org.mindrot.jbcrypt.BCrypt;

import io.reactivex.rxjava3.annotations.NonNull;

public class SignUpActivity extends AppCompatActivity {
    private ImageButton imgbtnBack;
    private DatabaseReference usersRef;
    private EditText edtEmail, edtPassword, edtConfirmPassword, edtUsername, edtPhoneNumber;
    private TextView txtUsernameError, txtPhoneError, txtPasswordError, txtConfirmPasswordError, txtEmailError, txtSignUp, txtSignIn;
    private ConstraintLayout signUpButton;
    private TextInputLayout tilEmail, tilPhoneNumber, tilUsername, tilPassword, tilConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imgbtnBack = findViewById(R.id.imgbtnBack);
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize Realtime Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        String defaultPrefix = "+84";

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtUsername = findViewById(R.id.edtFullname);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPhoneNumber.setText(defaultPrefix);
        edtPhoneNumber.setSelection(edtPhoneNumber.getText().length());
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
        txtSignIn = findViewById(R.id.txtSignIn);

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

        int color = ContextCompat.getColor(SignUpActivity.this, R.color.dark_green);
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSignIn.setTextColor(color);
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
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
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true); // Re-enable button
            txtSignUp.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_green)));
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true); // Re-enable button
            return;
        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError(getString(R.string.invalid_email_format));
            signUpButton.setEnabled(true);
            return;
        }


        if (!isPasswordValid(password)) {
            edtPassword.setError(getString(R.string.invalid_password_format));
            signUpButton.setEnabled(true);
            return;
        }

        // Check if username or email already exists
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    edtUsername.setError(getString(R.string.username_exists));
                    signUpButton.setEnabled(true);
                } else {
                    usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                edtEmail.setError(getString(R.string.email_exists));
                                signUpButton.setEnabled(true);
                            } else {
                                // Hash the password using BCrypt
                                String hashedPassword = hashPassword(password);

                                // Save user information to Realtime Database with custom user ID
                                String userId = "UID_" + username; // Create a custom user ID
                                User newUser = new User(userId, username, email, phone, hashedPassword);
                                usersRef.child(userId).setValue(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpActivity.this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                                                // Redirect to login activity after successful registration
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show();
                                                signUpButton.setEnabled(true);
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
                            signUpButton.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpActivity.this, getString(R.string.database_error), Toast.LENGTH_SHORT).show();
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
                    tilPhoneNumber.setHint(getString(R.string.hint_phone_number));
                } else {
                    txtPhoneError.setVisibility(View.GONE);
                }
            }
        });
        // Set onFocusChangeListener for Confirm Password EditText
        edtConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String confirmPass = edtConfirmPassword.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (!confirmPass.equals(password)) {
                    txtConfirmPasswordError.setVisibility(View.VISIBLE);
                    txtConfirmPasswordError.setText(getString(R.string.error_passwords_not_match));
                } else {
                    txtConfirmPasswordError.setVisibility(View.GONE);
                }
            }
            tilConfirmPassword.setHint(hasFocus || !TextUtils.isEmpty(edtConfirmPassword.getText().toString()) ? "" : getString(R.string.hint_confirm_password));
        });


        // Set onFocusChangeListener for Email EditText
        edtEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = edtEmail.getText().toString().trim();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    txtEmailError.setVisibility(View.VISIBLE);
                    txtEmailError.setText(getString(R.string.error_invalid_email));
                } else {
                    txtEmailError.setVisibility(View.GONE);
                }
            }
            tilEmail.setHint(hasFocus || !TextUtils.isEmpty(edtEmail.getText().toString()) ? "" : getString(R.string.hint_email));
        });

        edtPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = edtPassword.getText().toString().trim();
                if (!isPasswordValid(password)) {
                    txtPasswordError.setVisibility(View.VISIBLE);
                    txtPasswordError.setText(getString(R.string.error_invalid_password));
                } else {
                    txtPasswordError.setVisibility(View.GONE);
                }
            }
            tilPassword.setHint(hasFocus || !TextUtils.isEmpty(edtPassword.getText().toString()) ? "" : getString(R.string.hint_password));
        });

        edtUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilUsername.setHint("");
                txtUsernameError.setVisibility(View.GONE);
            } else {
                String username = edtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    tilUsername.setHint(getString(R.string.hint_username));
                } else if (!isUsernameValid(username)) {
                    txtUsernameError.setVisibility(View.VISIBLE);
                    txtUsernameError.setText(getString(R.string.error_invalid_username));
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
