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

    ActivityOrderHistoryBinding activityOrderHistoryBinding;
    private ArrayList<Order> orderArrayList;
    private OrderAdapter orderAdapter;
    private RecyclerView ordersRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderHistoryBinding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityOrderHistoryBinding.getRoot());
        allocateActivityTitle("Order History");
        ordersRv = findViewById(R.id.ordersRv);
        ordersRv.setLayoutManager(new LinearLayoutManager(this));  // Correct context reference
        ordersRv.setAdapter(orderAdapter);  // Set the adapter

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userID", null);

        if (currentUserId != null) {
            loadOrders(currentUserId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }



    private void loadOrders(String userId) {
        orderArrayList = new ArrayList<>();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("allorders").child(userId).child("Orders");
        ordersRef.orderByChild("orderBy").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseData", "Snapshot count: " + snapshot.getChildrenCount());
                orderArrayList.clear();
                for (DataSnapshot orderDs : snapshot.getChildren()) {
                    Log.d("FirebaseData", "Order data: " + orderDs.getValue());
                    Order order = orderDs.getValue(Order.class);
                    if (order != null) {
                        orderArrayList.add(order);
                    }
                }
                if (orderAdapter == null) {
                    orderAdapter = new OrderAdapter(OrderHistoryActivity.this, orderArrayList);
                    ordersRv.setAdapter(orderAdapter);
                } else {
                    orderAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderHistory", "Database error: " + error.getMessage());
            }
        });
    }

}
