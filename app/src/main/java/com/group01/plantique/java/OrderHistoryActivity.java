package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.adapter.OrderAdapter;
import com.group01.plantique.model.Order;
import com.group01.plantique.databinding.ActivityOrderHistoryBinding;

import java.util.ArrayList;

public class OrderHistoryActivity extends DrawerBaseActivity {

    ActivityOrderHistoryBinding binding;
    private ArrayList<Order> orderArrayList;
    private OrderAdapter orderAdapter;
    private RecyclerView ordersRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allocateActivityTitle("Order History");

        ordersRv = binding.ordersRv;
        ordersRv.setLayoutManager(new LinearLayoutManager(this));

        orderArrayList = new ArrayList<>();
        orderAdapter = new OrderAdapter(OrderHistoryActivity.this, orderArrayList);
        ordersRv.setAdapter(orderAdapter);  // Initialize adapter with empty list

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userID", null);

        if (currentUserId != null) {
            loadOrders(currentUserId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadOrders(String userId) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("allorders").child(userId).child("Orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderArrayList.clear(); // Clear existing data
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderArrayList.add(order);
                    }
                }
                orderAdapter.notifyDataSetChanged();  // Notify adapter to update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load orders: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
