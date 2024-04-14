package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group01.plantique.R;

import java.util.HashMap;

public class OrderConfirmActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    TextView txtFullname, txtAddress, txtEmail, txtPhone, txtPaymentMethod,txtTotal;
    ConstraintLayout btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        addViews();
        populateDataFromIntent();
        db = FirebaseDatabase.getInstance();
        databaseReference=db.getReference();

    }

    private void addViews() {
        txtFullname = findViewById(R.id.txtFullname);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtTotal=findViewById(R.id.txtTotal);
        btnConfirm=findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderConfirmationDialog("Dummy-Order-ID");
                pushOrderToFirebase();
            }
        });
    }
    private void pushOrderToFirebase() {
        HashMap<String, String> orderInfo = new HashMap<>();
        orderInfo.put("fullname", txtFullname.getText().toString());
        orderInfo.put("address", txtAddress.getText().toString());
        orderInfo.put("email", txtEmail.getText().toString());
        orderInfo.put("phone", txtPhone.getText().toString());
        orderInfo.put("paymentMethod", txtPaymentMethod.getText().toString());

        DatabaseReference newOrderRef = databaseReference.push();
        newOrderRef.setValue(orderInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String orderId = newOrderRef.getKey();
                    Log.d("FirebaseSuccess", "Order ID: " + orderId);
                    showOrderConfirmationDialog(orderId);
                } else {
                    Log.e("FirebaseError", "Failed to write order", task.getException());
                    showOrderConfirmationDialog("Failed to create order");
                }
            }
        });
    }

    public void showOrderConfirmationDialog(String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView txtOrderIdConfirm = dialogView.findViewById(R.id.txtOrderIdConfirm);
        txtOrderIdConfirm.setText("Mã đơn hàng: " + orderId);

        ConstraintLayout btnBack = dialogView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go back to the home screen (MainActivity in this case)
                Intent intent = new Intent(OrderConfirmActivity.this, HomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Optionally finish the current activity if it shouldn't be in the back stack
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }





    private void populateDataFromIntent() {
        Intent intent = getIntent();

        String fullname = intent.getStringExtra("fullname");
        String address = intent.getStringExtra("address");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String paymentMethod = intent.getStringExtra("paymentMethod");

        // Set the text of the TextViews
        txtFullname.setText(fullname != null ? fullname : "N/A");
        txtAddress.setText(address != null ? address : "N/A");
        txtEmail.setText(email != null ? email : "N/A");
        txtPhone.setText(phone != null ? phone : "N/A");
        txtPaymentMethod.setText(paymentMethod != null ? paymentMethod : "N/A");
    }
}
