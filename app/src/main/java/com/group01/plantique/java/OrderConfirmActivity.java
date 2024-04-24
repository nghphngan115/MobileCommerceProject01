package com.group01.plantique.java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderConfirmActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ArrayList<Product> productList;
    private TextView txtFullName, txtAddress, txtPhone, txtEmail, txtPaymentMethod, txtSubTotal, txtShippingFee,txtTotal;
    private ConstraintLayout btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        initializeFirebase();
        initializeViews();
        populateDataFromIntent();
    }

    private void initializeFirebase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("allorders");
    }

    private void initializeViews() {
        txtFullName = findViewById(R.id.txtFullname);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtTotal = findViewById(R.id.txtTotal);
        txtSubTotal=findViewById(R.id.txtDiscount);
        txtShippingFee=findViewById(R.id.txtShipFee);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushOrderToFirebase();
            }
        });
    }

    private void populateDataFromIntent() {
        Intent intent = getIntent();
        String fullname = intent.getStringExtra("FULL_NAME");
        String address = intent.getStringExtra("ADDRESS");
        String email = intent.getStringExtra("EMAIL");
        String phone = intent.getStringExtra("PHONE");
        String paymentMethod = intent.getStringExtra("PAYMENT_METHOD");
        String subTotalAmount = intent.getStringExtra("totalAmount");
        String productListJson = intent.getStringExtra("productListJson");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        productList = gson.fromJson(productListJson, type);

        txtFullName.setText(fullname != null ? fullname : "N/A");
        txtAddress.setText(address != null ? address : "N/A");
        txtEmail.setText(email != null ? email : "N/A");
        txtPhone.setText(phone != null ? phone : "N/A");
        txtPaymentMethod.setText(paymentMethod != null ? paymentMethod : "N/A");
        txtSubTotal.setText(subTotalAmount);
        txtShippingFee.setText("30000");

        // Calculate the total by adding subtotal and shipping fee
        int subtotal = Integer.parseInt(subTotalAmount.replace("đ", "").trim());
        int shippingFee = 30000;  // As per your fixed value
        int total = subtotal + shippingFee;
        txtTotal.setText(String.format("%d đ", total));



        // Populate the ListView with the product list
        CartListAdapter adapter = new CartListAdapter(this, productList);
        ListView lvProduct = findViewById(R.id.lvProduct);
        lvProduct.setAdapter(adapter);
    }

    private void pushOrderToFirebase() {
        String userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "User ID is not available, please sign in again.", Toast.LENGTH_LONG).show();
            return;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String orderId = timestamp;
        String cost = txtTotal.getText().toString().trim().replace("đ", "");

        HashMap<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", orderId);
        orderInfo.put("orderDate", timestamp);
        orderInfo.put("orderStatus", "Processing");
        orderInfo.put("orderBy", userId); // Thêm userId vào trường orderBy
        orderInfo.put("fullName", txtFullName.getText().toString());
        orderInfo.put("totalCost", cost);
        orderInfo.put("subTotal",txtSubTotal);
        orderInfo.put("address", txtAddress.getText().toString());
        orderInfo.put("email", txtEmail.getText().toString());
        orderInfo.put("phone", txtPhone.getText().toString());
        orderInfo.put("paymentMethod", txtPaymentMethod.getText().toString());

        DatabaseReference orderRef = databaseReference.child(userId).child("Orders").child(timestamp);
        orderRef.setValue(orderInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        processOrderItems(orderRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Failed to push order data", e);
                    }
                });
    }


    private void processOrderItems(DatabaseReference orderRef) {
        for (Product product : productList) {
            HashMap<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", product.getProductId());
            itemMap.put("productName", product.getProductName());
            itemMap.put("price", product.getPrice());
            itemMap.put("stock", product.getStock());
            itemMap.put("discount_price", product.getDiscountPrice());

            orderRef.child("Items").child(product.getProductId()).setValue(itemMap);

            // Update stock in the database
            updateProductStock(product.getProductId(), product.getStock());
        }
        showOrderConfirmationDialog(orderRef.getKey());
    }

    private void updateProductStock(String productId, int quantityOrdered) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);

        productRef.child("stock").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentStock = dataSnapshot.getValue(Integer.class);
                if (currentStock != null && currentStock >= quantityOrdered) {
                    productRef.child("stock").setValue(currentStock - quantityOrdered)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "Stock updated successfully!"))
                            .addOnFailureListener(e -> Log.e("Firebase", "Failed to update stock", e));
                } else {
                    Log.e("Firebase", "Not enough stock for productId: " + productId);
                    // Handle the case where there is not enough stock
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read stock for productId: " + productId, databaseError.toException());
            }
        });
    }


    private void showOrderConfirmationDialog(String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView txtOrderIdConfirm = dialogView.findViewById(R.id.txtOrderIdConfirm);
        txtOrderIdConfirm.setText("Mã đơn hàng: " + orderId);

        ConstraintLayout btnBack = dialogView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishHomeScreen();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void finishHomeScreen() {
        Intent intent = new Intent(OrderConfirmActivity.this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        return sharedPreferences.getString("userID", null);
    }
}
