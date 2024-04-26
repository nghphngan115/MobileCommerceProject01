package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.adapter.ProductOrderAdapter;
import com.group01.plantique.model.Order;
import com.group01.plantique.model.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {
    private TextView txtStatus, txtFullname, txtAddress, txtEmail, txtPhone, txtSubTotal, txtShipFee, txtPaymentMethod, txtDiscount, txtTotal;
    private ListView lvProduct;
    private ConstraintLayout btnCancelOrder;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details); // Replace with your actual layout file name

        initializeViews();
        populateOrderDetails();
    }

    private void initializeViews() {
        txtStatus = findViewById(R.id.txtStatus);
        txtFullname = findViewById(R.id.txtFullname);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtShipFee = findViewById(R.id.txtShipFee);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtTotal = findViewById(R.id.txtTotal);
        lvProduct = findViewById(R.id.lvProduct);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnCancelOrder.setOnClickListener(v -> cancelOrder());
    }

    private void cancelOrder() {
        if (order != null && order.getOrderId() != null && order.getOrderBy() != null) {
            order.setOrderStatus("Cancelled");
            updateOrderStatusInFirebase();
        } else {
            Log.e("OrderDetailsActivity", "Order or essential fields are null.");
            Toast.makeText(this, "Order details are incomplete.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateOrderStatusInFirebase() {
        if (order.getOrderBy() != null && order.getOrderId() != null) {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("allorders")
                    .child(order.getOrderBy())
                    .child("Orders")
                    .child(order.getOrderId());

            orderRef.child("orderStatus").setValue("Cancelled")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(OrderDetailsActivity.this, "Order has been cancelled", Toast.LENGTH_SHORT).show();
                        txtStatus.setText("Cancelled");
                    })
                    .addOnFailureListener(e -> Toast.makeText(OrderDetailsActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Cannot update order status due to missing information.", Toast.LENGTH_SHORT).show();
        }
    }



    private void populateOrderDetails() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order != null) {
            // Set UI elements from the order, but skip orderId and orderBy
            txtStatus.setText(order.getOrderStatus());
            txtFullname.setText(order.getFullName());
            txtAddress.setText(order.getAddress());
            txtEmail.setText(order.getEmail());
            txtPhone.setText(order.getPhone());
            txtSubTotal.setText(String.format("%s đ", order.getSubTotal()));
            txtShipFee.setText(String.format("%s đ", order.getShippingFee()));
            txtPaymentMethod.setText(order.getPaymentMethod());
            txtDiscount.setText("Calculate if applicable"); // Implement if you have discount data
            txtTotal.setText(String.format("%s đ", order.getTotalCost()));

            // Setup list adapter for products
            setupProductList(order.getItems());
        } else {
            Log.e("OrderDetailsActivity", "Failed to retrieve the order object.");
            Toast.makeText(this, "Failed to load order details.", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupProductList(HashMap<String, Product> items) {
        ArrayList<Product> products = new ArrayList<>(items.values());
        ProductOrderAdapter adapter = new ProductOrderAdapter(this, products);
        lvProduct.setAdapter(adapter);
    }
}
