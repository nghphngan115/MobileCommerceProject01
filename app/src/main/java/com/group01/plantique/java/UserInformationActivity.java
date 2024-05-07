package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityUserInformationBinding;
import com.squareup.picasso.Picasso;

public class UserInformationActivity extends DrawerBaseActivity {
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_CAMERA_CODE = 2;

    private EditText nameEditText, addressEditText, phoneEditText, emailEditText;
    private TextView editNameTextView, editAddressTextView, editPhoneTextView, saveTextView;
    private ImageView avatarImageView, cameraIconImageView;
    private ImageButton backButton;
    private ConstraintLayout btnChangePassword, btnLogOut;

    private DatabaseReference mDatabase;
    private Uri image_uri;
    private static final String PREF_LOGIN_STATUS = "loginStatus";
    ActivityUserInformationBinding activityUserInformationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUserInformationBinding= ActivityUserInformationBinding.inflate(getLayoutInflater());
        setContentView(activityUserInformationBinding.getRoot());
        allocateActivityTitle("Personal account");

        //Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.blog) {
                startActivity(new Intent(getApplicationContext(), BlogCategoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.notification) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.account) {
                return true;
            }
            return false;
        });

        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        editNameTextView = findViewById(R.id.editNameTextView);
        editAddressTextView = findViewById(R.id.editAddressTextView);
        editPhoneTextView = findViewById(R.id.editPhoneTextView);
        saveTextView = findViewById(R.id.textView3);
        avatarImageView = findViewById(R.id.avatarImageView);
        cameraIconImageView = findViewById(R.id.cameraIconImageView);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogOut = findViewById(R.id.btnLogOut);

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String loggedInUserID = sharedPreferences.getString("userID", "");

        if (loggedInUserID.isEmpty()) {
            // Nếu không có thông tin đăng nhập, chuyển người dùng về màn hình LoginActivity
            Toast.makeText(this, "Bạn cần đăng nhập trước khi xem thông tin người dùng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserInformationActivity.this, LoginActivity.class));
            finish();
        } else {
            // Nếu có thông tin đăng nhập, tiếp tục tải thông tin người dùng
            loadUserInfo(loggedInUserID);
        }

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

        editNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing name
            }
        });
        editAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing address
            }
        });
        editPhoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable editing phone
            }
        });

        cameraIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển từ LoginActivity sang ResetPasswordActivity
                Intent intent = new Intent(UserInformationActivity.this, ResetPasswordActivity.class);
                startActivity(intent); // Khởi chạy Intent để chuyển đến ResetPasswordActivity
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();

            }
        });
    }
    private void logoutUser() {
        // Xóa userID khỏi SharedPreferences
        saveUserIDToSharedPreferences(""); // Truyền giá trị rỗng để đánh dấu đăng xuất
        // Xóa trạng thái đăng nhập khỏi SharedPreferences
        saveLoginStatus(false);
        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(UserInformationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Kết thúc UserInformationActivity để người dùng không thể quay lại màn hình này bằng nút back
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


    private void openGallery() {
        // Check storage permission
        if (checkStoragePermission()) {
            // If permission is granted, open gallery
            pickFromGallery();
        } else {
            // If permission is not granted, request it
            requestStoragePermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            avatarImageView.setImageURI(imageUri);
            saveAvatarUrl(imageUri.toString()); // Lưu URL của ảnh vào cơ sở dữ liệu Firebase
        } else if (requestCode == PICK_IMAGE_CAMERA_CODE && resultCode == RESULT_OK) {
            avatarImageView.setImageURI(image_uri);
            saveAvatarUrl(image_uri.toString()); // Lưu URL của ảnh vào cơ sở dữ liệu Firebase
        }
    }
    private void pickFromGallery() {
        // Intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void requestStoragePermission() {
        // Request storage permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
    }

    private void openCamera() {
        // Check camera and storage permissions
        if (checkCameraPermission()) {
            // If permissions granted, open camera
            pickFromCamera();
        } else {
            // If permissions not granted, request them
            requestCameraPermission();
        }
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, PICK_IMAGE_CAMERA_CODE);
    }

    private void requestCameraPermission() {
        // Request camera and storage permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        // Check storage permission
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermission() {
        // Check camera and storage permissions
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Handle permission request result
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If storage permission granted, open gallery
                pickFromGallery();
            } else {
                // If storage permission not granted, show toast
                Toast.makeText(this, "Storage permission is required...", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If camera and storage permission granted, open camera
                pickFromCamera();
            } else {
                // If camera or storage permission not granted, show toast
                Toast.makeText(this, "Camera & Storage permissions are required...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserInfo(String loggedInUserID) {
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(loggedInUserID);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String address = snapshot.child("addresses").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);

                    nameEditText.setText(name);
                    emailEditText.setText(email);
                    addressEditText.setText(address);
                    phoneEditText.setText(phone);
                    Picasso.get().load(avatarUrl).into(avatarImageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle failed to load user info
            }
        });
    }


    private void saveUserInfo() {
        String newName = nameEditText.getText().toString();
        String newEmail = emailEditText.getText().toString();
        String newAddress = addressEditText.getText().toString();
        String newPhone = phoneEditText.getText().toString();

        mDatabase.child("username").setValue(newName);
        mDatabase.child("email").setValue(newEmail);
        mDatabase.child("addresses").setValue(newAddress);
        mDatabase.child("phone").setValue(newPhone);

        Toast.makeText(this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
    }
    private void saveAvatarUrl(String avatarUrl) {
        mDatabase.child("avatarUrl").setValue(avatarUrl);
    }
}
