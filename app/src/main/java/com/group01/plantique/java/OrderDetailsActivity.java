package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Order;
import com.group01.plantique.model.Product;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {
    private TextView txtFullName, txtAddress, txtPhone, txtEmail, txtPaymentMethod, txtSubTotal, txtShippingFee, txtTotal, txtStatus;
    private ListView lvProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Initialize views
        txtFullName = findViewById(R.id.txtFullname);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtSubTotal = findViewById(R.id.txtDiscount);  // Check if this is correct
        txtShippingFee = findViewById(R.id.txtShipFee);
        txtTotal = findViewById(R.id.txtTotal);
        txtStatus = findViewById(R.id.txtStatus);
        lvProduct = findViewById(R.id.lvProduct);

        // Retrieve and display the order
        Order order = (Order) getIntent().getSerializableExtra("order");
        if (order != null) {
            Log.d("OrderDetails", "Order loaded: " + order.getOrderId());
            displayOrderDetails(order);
        } else {
            Log.e("OrderDetails", "Failed to load order details");
            Toast.makeText(this, "Order details not available", Toast.LENGTH_SHORT).show();
        }

    }

    private void displayOrderDetails(Order order) {
        txtFullName.setText(order.getFullName());
        txtAddress.setText(order.getAddress());
        txtPhone.setText(order.getPhone());
        txtEmail.setText(order.getEmail());
        txtPaymentMethod.setText(order.getPaymentMethod());
        txtSubTotal.setText(order.getTotalCost());
        txtShippingFee.setText("30000");
        txtTotal.setText(order.getTotalCost());
        txtStatus.setText(order.getOrderStatus());

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            ArrayList<Product> products = new ArrayList<>(order.getItems().values());
            CartListAdapter adapter = new CartListAdapter(this, products);
            lvProduct.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No products in this order.", Toast.LENGTH_SHORT).show();
        }
    }

}
