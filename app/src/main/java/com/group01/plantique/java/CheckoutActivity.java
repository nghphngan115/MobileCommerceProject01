package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.group01.plantique.R;

import java.util.Random;

public class CheckoutActivity extends AppCompatActivity {
    ConstraintLayout btnConfirm;
    EditText edtFullname, edtAddress, edtEmail, edtPhone;
    RadioButton radTransfer, radCOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        addViews();
        addEvents();
    }

    private void addViews() {
        btnConfirm = findViewById(R.id.btnConfirm);
        edtFullname = findViewById(R.id.edtFullname);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        radTransfer = findViewById(R.id.radTransfer);
        radCOD = findViewById(R.id.radCOD);
    }

    private void addEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {  // Kiểm tra xem các trường nhập liệu đã hợp lệ chưa
                    String orderID = generateRandomOrderID();  // Tạo orderID
                    Intent intent = new Intent(CheckoutActivity.this, OrderConfirmActivity.class);

                    // Collect payment method
                    String paymentMethod = radTransfer.isChecked() ? "Chuyển khoản qua ngân hàng" : "Thanh toán khi nhận hàng";

                    // Put extras to intent
                    intent.putExtra("fullname", edtFullname.getText().toString());
                    intent.putExtra("address", edtAddress.getText().toString());
                    intent.putExtra("email", edtEmail.getText().toString());
                    intent.putExtra("phone", edtPhone.getText().toString());
                    intent.putExtra("paymentMethod", paymentMethod);
                    intent.putExtra("orderID", orderID);

                    startActivity(intent);
                }
            }
        });
    }

    private String generateRandomOrderID() {
        Random random = new Random();
        int number = random.nextInt(999999999); // Tạo số ngẫu nhiên
        return String.format("%09d", number); // Định dạng số với 9 chữ số, thêm số 0 ở đầu nếu cần
    }

    public boolean validateFields() {
        boolean isValid = true;

        // Check if the full name field is empty
        if (isEmpty(edtFullname)) {
            edtFullname.setError("This field cannot be empty");
            isValid = false;
        }

        // Check if the address field is empty
        if (isEmpty(edtAddress)) {
            edtAddress.setError("This field cannot be empty");
            isValid = false;
        }

        // Check if the email field is empty or not in the correct format
        if (isEmpty(edtEmail)) {
            edtEmail.setError("This field cannot be empty");
            isValid = false;
        } else if (!edtEmail.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            edtEmail.setError("Invalid email address");
            isValid = false;
        }

        // Check if the phone field is empty or not exactly 10 digits
        if (isEmpty(edtPhone)) {
            edtPhone.setError("This field cannot be empty");
            isValid = false;
        } else if (edtPhone.getText().toString().trim().length() != 10) {
            edtPhone.setError("Phone number must be 10 digits long");
            isValid = false;
        }

        // Check if a payment method is selected
        if (!radTransfer.isChecked() && !radCOD.isChecked()) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    // Helper method to check if EditText is empty
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
}
