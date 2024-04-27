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
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;


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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminOrderDetailsActivity extends AppCompatActivity {
    private TextView txtStatus, txtFullname, txtOrderId, txtUserId, txtEmail, txtSubTotal, txtTotal, txtShipFee, txtPaymentMethod, txtDiscount;
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
        txtDiscount=findViewById(R.id.txtDiscount);
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
            switch (order.getOrderStatus()) {
                case "Processing":
                    txtStatus.setTextColor(getResources().getColor(R.color.processing));
                    break;
                case "Delivering":
                    txtStatus.setTextColor(getResources().getColor(R.color.delivering));
                    break;
                case "Finished":
                    txtStatus.setTextColor(getResources().getColor(R.color.finished));
                    break;
                case "Cancelled":
                    txtStatus.setTextColor(getResources().getColor(R.color.cancelled));
                    break;
                default:
                    // Màu mặc định khi trạng thái không xác định
                    txtStatus.setTextColor(getResources().getColor(android.R.color.black));
                    break;
            }
            txtFullname.setText(order.getFullName());
            txtUserId.setText(order.getOrderBy());
            txtEmail.setText(order.getEmail());
            edtPhone.setText(order.getPhone());
            txtDiscount.setText(order.getDiscount());
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
                showUpdateConfirmationDialog();
            } else {
                Log.e("AdminOrderDetailsActivity", "Order object or Order ID is null");
                Toast.makeText(this, "Error: Order data is incomplete.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showUpdateConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Update");
        builder.setMessage("Are you sure you want to update this order?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateOrderFromUI();  // Proceed with the update
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // Dismiss the dialog and do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateOrderFromUI() {
        Map<String, Object> updates = new HashMap<>();
        String newAddress = edtAddress.getText().toString();
        String newPhone = edtPhone.getText().toString();
        String newNote = edtNote.getText().toString();
        String newStatus = spinnerStatus.getSelectedItem().toString();

        updates.put("address", newAddress);
        updates.put("phone", newPhone);
        updates.put("orderNote", newNote);
        updates.put("orderStatus", newStatus);

        updateOrderInFirebase(updates);
    }

    private void updateOrderInFirebase(Map<String, Object> updates) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("allorders")
                .child(order.getOrderBy())
                .child("Orders")
                .child(order.getOrderId());

        orderRef.updateChildren(updates)
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

