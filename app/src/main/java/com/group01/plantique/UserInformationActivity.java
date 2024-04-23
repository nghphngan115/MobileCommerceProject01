package com.group01.plantique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserInformationActivity extends AppCompatActivity {
    private EditText nameEditText, addressEditText, phoneEditText, emailEditText;
    private TextView editNameTextView, editAddressTextView, editPhoneTextView, saveTextView;
    private ImageView avatarImageView, cameraIconImageView;
    private ImageButton backButton;

    private DatabaseReference mDatabase;
    private String currentUserID;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        // Ánh xạ các view từ layout
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
        backButton = findViewById(R.id.imgButton3);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Người dùng đã đăng nhập
            currentUserID = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
            // Tiếp tục với việc load thông tin người dùng và các hoạt động khác
            loadUserInfo();
        } else {
            // Người dùng chưa đăng nhập, bạn có thể xử lý tùy ý, ví dụ: chuyển người dùng đến màn hình đăng nhập
            startActivity(new Intent(UserInformationActivity.this, LoginActivity.class));
            finish();
        }

        // Lắng nghe sự kiện khi người dùng bấm nút Save
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lưu thông tin người dùng sau khi chỉnh sửa
                saveUserInfo();
            }
        });

        // Lắng nghe sự kiện khi người dùng bấm vào chữ Edit ở từng dòng
        editNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cho phép chỉnh sửa tên
            }
        });
        editAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cho phép chỉnh sửa địa chỉ
            }
        });
        editPhoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cho phép chỉnh sửa số điện thoại
            }
        });

        // Lắng nghe sự kiện khi người dùng bấm vào icon camera
        cameraIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở gallery hoặc camera để chọn ảnh mới
                openGallery();
            }
        });

        // Lắng nghe sự kiện khi người dùng bấm nút Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại activity trước đó (LoginActivity)
                finish();
            }
        });

        // Load thông tin người dùng từ Firebase
        loadUserInfo();
    }

    // Phương thức để mở thư viện ảnh của người dùng
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Phương thức để xử lý kết quả trả về từ Intent khi người dùng đã chọn ảnh từ thư viện của họ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Hiển thị ảnh đã chọn lên ImageView
            avatarImageView.setImageURI(imageUri);
        }
    }

    // Phương thức để load thông tin người dùng từ Firebase và hiển thị lên giao diện
    private void loadUserInfo() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông tin từ snapshot và hiển thị lên giao diện
                    String name = snapshot.child("name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String address = snapshot.child("address").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();
                    String avatarUrl = snapshot.child("avatarUrl").getValue().toString();

                    // Hiển thị thông tin lên giao diện
                    nameEditText.setText(name);
                    emailEditText.setText(email);
                    addressEditText.setText(address);
                    phoneEditText.setText(phone);
                    // Load avatar từ URL và hiển thị lên ImageView (cần sử dụng thư viện Picasso hoặc Glide)
                    // Picasso.get().load(avatarUrl).into(avatarImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi load thông tin thất bại
            }
        });
    }

    // Phương thức để lưu thông tin người dùng sau khi chỉnh sửa
    private void saveUserInfo() {
        // Lấy thông tin từ các EditText
        String newName = nameEditText.getText().toString();
        String newEmail = emailEditText.getText().toString();
        String newAddress = addressEditText.getText().toString();
        String newPhone = phoneEditText.getText().toString();

        // Cập nhật thông tin người dùng trên Firebase Database
        mDatabase.child("name").setValue(newName);
        mDatabase.child("email").setValue(newEmail);
        mDatabase.child("address").setValue(newAddress);
        mDatabase.child("phone").setValue(newPhone);

        // Thông báo lưu thành công
        Toast.makeText(this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
    }
}
