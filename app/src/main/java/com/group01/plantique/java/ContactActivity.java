package com.group01.plantique.java;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityContactBinding;


public class  ContactActivity extends DrawerBaseActivity {
    EditText edtFullname, edtPhone, edtContent;
    ConstraintLayout btnConfirm;
    ActivityContactBinding activityContactBinding;
    private static final int REQUEST_CODE_EMAIL = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContactBinding=ActivityContactBinding.inflate(getLayoutInflater());

        setContentView(activityContactBinding.getRoot());
        allocateActivityTitle(getString(R.string.nav_contact_support));
        addViews();



    }


    private void addViews() {
        edtFullname = findViewById(R.id.edtFullname);
        edtPhone = findViewById(R.id.edtPhone);
        edtContent = findViewById(R.id.edtContent);
        btnConfirm = findViewById(R.id.btnConfirm);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    sendEmail();
                }
            }
        });
    }


    private boolean validateFields() {
        String fullname = edtFullname.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String content = edtContent.getText().toString().trim();


        boolean isValid = true;


        if (fullname.isEmpty()) {
            edtFullname.setError("Full name is required");
            isValid = false;
        }


        if (phone.isEmpty()) {
            edtPhone.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() != 10) {
            edtPhone.setError("Phone number must be 10 digits long");
            isValid = false;
        }


        if (content.isEmpty()) {
            edtContent.setError("Message content is required");
            isValid = false;
        }


        return isValid;
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EMAIL) { // Kiểm tra requestCode
            // Xóa nội dung trong EditText sau khi quay lại từ ứng dụng Gmail
            edtFullname.setText("");
            edtPhone.setText("");
            edtContent.setText("");
        }
    }
}
