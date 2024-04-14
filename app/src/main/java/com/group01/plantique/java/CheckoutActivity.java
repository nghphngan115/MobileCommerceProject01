package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.group01.plantique.R;

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
        edtPhone = findViewById(R.id.edtVoucher);
        radTransfer = findViewById(R.id.radTransfer);
        radCOD = findViewById(R.id.radCOD);
    }

    private void addEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, OrderConfirmActivity.class);

                // Collect payment method
                String paymentMethod;
                if (radTransfer.isChecked()) {
                    paymentMethod = "Chuyển khoản qua ngân hàng";
                } else if (radCOD.isChecked()) {
                    paymentMethod = "Thanh toán khi nhận hàng";
                } else {
                    paymentMethod = "Not Selected"; // Fallback
                }

                // Put extras to intent
                intent.putExtra("fullname", edtFullname.getText().toString());
                intent.putExtra("address", edtAddress.getText().toString());
                intent.putExtra("email", edtEmail.getText().toString());
                intent.putExtra("phone", edtPhone.getText().toString());
                intent.putExtra("paymentMethod", paymentMethod);

                startActivity(intent);
            }
        });
    }
}
