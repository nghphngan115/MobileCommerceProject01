package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.group01.plantique.R;

public class ContactActivity extends AppCompatActivity {
TextView txtFullname,txtPhone,txtEmail,txtContent;
ConstraintLayout btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        addViews();
    }
    private void addViews() {
        txtFullname = findViewById(R.id.edtFullname);
        txtPhone = findViewById(R.id.edtPhone);
        txtEmail = findViewById(R.id.edtEmail);
        txtContent = findViewById(R.id.edtContent);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }
    private void sendEmail() {
        String fullname = txtFullname.getText().toString();
        String phone = txtPhone.getText().toString();
        String userEmailAddress = txtEmail.getText().toString();  // This is the user's own email.
        String content = txtContent.getText().toString();

        if (fullname.isEmpty() || phone.isEmpty() || userEmailAddress.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Email address where you want to send the user's message
        String recipientEmailAddress = "ngannhp21411ca@st.uel.edu.vn";  // Change this to your desired email

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { recipientEmailAddress });  // Recipient's email
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact from " + fullname);
        intent.putExtra(Intent.EXTRA_TEXT, "Name: " + fullname + "\nPhone: " + phone + "\nEmail: " + userEmailAddress + "\nContent: " + content);

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


}