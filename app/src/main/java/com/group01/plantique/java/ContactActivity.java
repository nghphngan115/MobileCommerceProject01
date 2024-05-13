package com.group01.plantique.java;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityContactBinding;
import com.squareup.picasso.Picasso;


public class ContactActivity extends DrawerBaseActivity {
    EditText edtFullname, edtPhone, edtContent;
    ConstraintLayout btnConfirm;
    ActivityContactBinding activityContactBinding;
    private static final int REQUEST_CODE_EMAIL = 101;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContactBinding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(activityContactBinding.getRoot());
        allocateActivityTitle(getString(R.string.nav_contact_support));
        addViews();
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String loggedInUserID = sharedPreferences.getString("userID", "");

        if (loggedInUserID.isEmpty()) {
            // Nếu không có thông tin đăng nhập, chuyển người dùng về màn hình LoginActivity
            Toast.makeText(this, "Bạn cần đăng nhập trước khi xem thông tin người dùng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ContactActivity.this, LoginActivity.class));
            finish();
        } else {
            // Nếu có thông tin đăng nhập, tiếp tục tải thông tin người dùng
            fetchUserData(loggedInUserID);
        }
    }

    private void addViews() {
        edtFullname = findViewById(R.id.edtFullname);
        edtPhone = findViewById(R.id.edtPhone);
        edtContent = findViewById(R.id.edtContent);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(v -> {
            if (validateFields()) {
                sendEmail();
            }
        });
    }

    private void fetchUserData(String loggedInUserID) {
        userRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUserID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);

                    edtFullname.setText(name);
                    edtPhone.setText(phone);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContactActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validateFields() {
        String content = edtContent.getText().toString().trim();

        if (content.isEmpty()) {
            edtContent.setError("Message content is required");
            return false;
        }
        return true;
    }

    private void sendEmail() {
        String fullname = edtFullname.getText().toString();
        String phone = edtPhone.getText().toString();
        String content = edtContent.getText().toString();
        String recipientEmailAddress = "plantiqueshop01@gmail.com";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Q&A: Contact from " + fullname);
        intent.putExtra(Intent.EXTRA_TEXT, "Name: " + fullname + "\nPhone: " + phone + "\nContent: " + content);

        intent.setPackage("com.google.android.gm");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Gmail app is not installed.", Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        }
    }
}
