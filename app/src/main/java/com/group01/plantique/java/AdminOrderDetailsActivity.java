package com.group01.plantique.java;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.adapter.ProductOrderAdapter;
import com.group01.plantique.model.Order;
import com.group01.plantique.model.Product;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderDetailsActivity extends AppCompatActivity {
    private TextView txtStatus, txtFullname, txtOrderId, txtUserId, txtEmail, txtSubTotal, txtTotal, txtShipFee, txtPaymentMethod;
    private EditText edtAddress, edtPhone, edtNote;
    private ListView lvProduct;
    private Spinner spinnerStatus;
    private Button btnUpdateOrder;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_details);
        initializeViews();
        populateOrderDetails();
        setupSpinner();
        setupUpdateButton();
    }

    private void initializeViews() {
        txtStatus = findViewById(R.id.txtStatus);
        txtFullname = findViewById(R.id.txtFullname);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtUserId = findViewById(R.id.txtUserId);
        txtEmail = findViewById(R.id.txtEmail);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtTotal = findViewById(R.id.txtTotal);
        txtShipFee = findViewById(R.id.txtShipFee);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtNote = findViewById(R.id.edtNote);
        lvProduct = findViewById(R.id.lvProduct);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateOrder = findViewById(R.id.btnUpdateOrder);
        // Add error checks and logs for all fields, not just txtStatus
        checkViewInitialization();
    }

    private void checkViewInitialization() {
        if (txtStatus == null || txtFullname == null || txtOrderId == null || txtUserId == null || txtEmail == null ||
                txtSubTotal == null || txtTotal == null || txtShipFee == null || txtPaymentMethod == null ||
                edtPhone == null || edtAddress == null || edtNote == null || lvProduct == null || spinnerStatus == null ||
                btnUpdateOrder == null) {
            Log.e("AdminOrderDetailsActivity", "One or more views are not properly initialized.");
            Toast.makeText(this, "Initialization error, please check your layout file.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void populateOrderDetails() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order != null) {
            txtOrderId.setText(order.getOrderId());
            txtStatus.setText(order.getOrderStatus());
            txtFullname.setText(order.getFullName());
            txtUserId.setText(order.getOrderBy());
            txtEmail.setText(order.getEmail());
            edtPhone.setText(order.getPhone());
            edtAddress.setText(order.getAddress());
            edtNote.setText(order.getOrderNote());
            txtSubTotal.setText(String.format("%s đ", order.getSubTotal()));
            txtShipFee.setText(String.format("%s đ", order.getShippingFee()));
            txtPaymentMethod.setText(order.getPaymentMethod());

            txtTotal.setText(String.format("%s đ", order.getTotalCost()));

            DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("allorders")
                    .child(order.getOrderBy())
                    .child("Orders")
                    .child(order.getOrderId())
                    .child("Items");

            itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Product> products = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        products.add(product);
                    }
                    ProductOrderAdapter adapter = new ProductOrderAdapter(AdminOrderDetailsActivity.this, products);
                    lvProduct.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("AdminOrderDetailsActivity", "Failed to read products.", databaseError.toException());
                }
            });
        } else {
            Log.e("AdminOrderDetailsActivity", "Failed to retrieve the order object.");
            Toast.makeText(this, "Failed to load order details.", Toast.LENGTH_SHORT).show();
        }
    }
    private void setupSpinner() {
        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("Processing");
        statusOptions.add("Delivering");
        statusOptions.add("Finished");
        statusOptions.add("Cancelled");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);
    }

    private void setupUpdateButton() {
        btnUpdateOrder.setOnClickListener(view -> {
            if (order != null && order.getOrderId() != null) {
                updateOrderFromUI();
            } else {
                Log.e("AdminOrderDetailsActivity", "Order object or Order ID is null");
                Toast.makeText(this, "Error: Order data is incomplete.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateOrderFromUI() {
        String newAddress = edtAddress.getText().toString();
        String newPhone = edtPhone.getText().toString();
        String newNote = edtNote.getText().toString();
        String newStatus = spinnerStatus.getSelectedItem().toString();
        order.setAddress(newAddress);
        order.setPhone(newPhone);
        order.setOrderNote(newNote);
        order.setOrderStatus(newStatus);
        updateOrderInFirebase(order);
    }

    private void updateOrderInFirebase(Order updatedOrder) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("allorders")
                .child(updatedOrder.getOrderBy())
                .child("Orders")
                .child(updatedOrder.getOrderId());

        orderRef.setValue(updatedOrder)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Order updated successfully in Firebase");
                    Toast.makeText(AdminOrderDetailsActivity.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);  // Set the result as OK
                    finish();  // Finish activity and return to the AdminOrderListActivity
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to update order in Firebase", e);
                    Toast.makeText(AdminOrderDetailsActivity.this, "Failed to update order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);  // Set the result as CANCELED
                });
    }

}

