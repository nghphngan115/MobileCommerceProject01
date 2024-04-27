package com.group01.plantique.java;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.adapter.AdminOrderAdapter;
import com.group01.plantique.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AdminOrderListActivity extends AppCompatActivity {
    private RecyclerView ordersRecyclerView;
    private AdminOrderAdapter ordersAdapter;
    private List<Order> ordersList;
    private List<Order> filteredList = new ArrayList<>();
    private Spinner statusSpinner;
    private String currentStatus = "All Statuses";
    private SearchView searchOrderView;
    private ArrayAdapter<CharSequence> spinnerAdapter; // Spinner adapter
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);
        setupFirebaseListener();
        createNotificationChannel();

        statusSpinner = findViewById(R.id.spinnerOrderStatus);
        searchOrderView = findViewById(R.id.searchOrder);

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.order_statuses, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(spinnerAdapter);
        statusSpinner.setSelection(spinnerAdapter.getPosition("All Statuses"));

        ordersList = new ArrayList<>();
        ordersRecyclerView = findViewById(R.id.ordersadminRv);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ordersAdapter = new AdminOrderAdapter(this, ordersList);
        ordersRecyclerView.setAdapter(ordersAdapter);

        setupSpinner();
        setupSearchView();
        fetchAllOrders(); // Initial fetch
    }

    private void setupSpinner() {
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStatus = parent.getItemAtPosition(position).toString();
                fetchAllOrders(); // Refetch orders every time the status changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void setupFirebaseListener() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("allorders");
        ordersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String orderId = userSnapshot.getKey();
                    if (!ordersListContainsOrderId(orderId)) {
                        Order order = userSnapshot.getValue(Order.class);
                        if (order != null && (currentStatus.equals("All Statuses") || order.getOrderStatus().equalsIgnoreCase(currentStatus))) {
                            ordersList.add(order);
                            Collections.reverse(ordersList);
                            ordersAdapter.notifyDataSetChanged();
                            sendNewOrderNotification(order);
                        }
                    }
                }
            }

            // Các phương thức còn lại có thể để trống hoặc không cần cài đặt
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // Phương thức kiểm tra xem orderId đã tồn tại trong danh sách đơn hàng hay chưa
    private boolean ordersListContainsOrderId(String orderId) {
        for (Order order : ordersList) {
            if (order != null && order.getOrderId() != null && order.getOrderId().equals(orderId)) {
                return true;
            }
        }
        return false;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            String channelId = getString(R.string.channel_id); // Unique ID for the notification channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.e("NotificationChannel", "Failed to create notification channel");
            }
        }
    }


    private void fetchAllOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("allorders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ordersList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Correctly iterating through each user's orders
                    DataSnapshot ordersSnapshot = userSnapshot.child("Orders");
                    for (DataSnapshot orderSnapshot : ordersSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && (currentStatus.equals("All Statuses") || order.getOrderStatus().equalsIgnoreCase(currentStatus))) {
                            ordersList.add(order);
                        }
                    }
                }
                Collections.reverse(ordersList); // Filter immediately after fetching
                ordersAdapter.notifyDataSetChanged(); // Notify adapter about data change
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AdminOrderListActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // This code will execute when coming back from AdminOrderDetailsActivity
            // Refresh your list
            fetchAllOrders(); // Call your method to refresh or re-fetch the orders list
        }
    }
    private void sendNewOrderNotification(Order order) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, AdminOrderDetailsActivity.class);
        intent.putExtra("order", order);
        // Default ID in case order ID is null
        int notificationId = order.getOrderId() != null ? order.getOrderId().hashCode() : new Random().nextInt();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentTitle("New Order Received")
                .setContentText("New order: " + (order.getOrderId() != null ? order.getOrderId() : "Unknown ID"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        } else {
            Log.e("sendNewOrderNotification", "Notification Manager not available");
        }
    }






    private void filterOrders(String status) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : ordersList) {
            if (status.equals("All Statuses") || (order.getOrderStatus() != null && order.getOrderStatus().equalsIgnoreCase(status))) {
                filteredOrders.add(order);
            }
        }
        filteredList = new ArrayList<>(filteredOrders); // Update filteredList
        ordersAdapter.updateList(filteredOrders); // Update the adapter with the filtered list
    }

    private void searchOrders(String query) {
        if (query == null) return; // Check for null to avoid NullPointerException
        List<Order> searchedList = new ArrayList<>();
        for (Order order : filteredList) {
            if (order.getOrderId() != null && order.getOrderId().toLowerCase().contains(query.toLowerCase())) {
                searchedList.add(order);
            }
        }
        ordersAdapter.updateList(searchedList); // Update the adapter with the searched list
    }


    private void setupSearchView() {
        searchOrderView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOrders(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchOrders(newText);
                return false;
            }
        });
    }


}
