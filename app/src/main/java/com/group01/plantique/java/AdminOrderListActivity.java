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
    public static final int REQUEST_CODE = 1;
    private RecyclerView ordersRecyclerView;
    private AdminOrderAdapter ordersAdapter;
    private List<Order> ordersList;
    private List<Order> filteredList = new ArrayList<>();
    private Spinner statusSpinner;
    private String currentStatus = "All Statuses";
    private SearchView searchOrderView;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        createNotificationChannel();  // Ensure the notification channel is created
        setupFirebaseListener();  // Setup listener to detect new orders
        setupUIComponents();  // Setup UI Components
        fetchAllOrders();  // Fetch all orders initially
    }

    private void setupUIComponents() {
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
    }

    private void setupFirebaseListener() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("allorders");
        ordersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot ordersSnapshot = userSnapshot.child("Orders");
                    for (DataSnapshot orderSnapshot : ordersSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && (currentStatus.equals("All Statuses") || order.getOrderStatus().equalsIgnoreCase(currentStatus))) {
                            if (!ordersListContainsOrderId(order.getOrderId())) {
                                ordersList.add(order);
                                Collections.reverse(ordersList);
                                ordersAdapter.notifyDataSetChanged();
                                sendNewOrderNotification(order);
                            }
                        }
                    }
                }
            }

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
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot ordersSnapshot = userSnapshot.child("Orders");
                    for (DataSnapshot orderSnapshot : ordersSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null && (currentStatus.equals("All Statuses") || order.getOrderStatus().equalsIgnoreCase(currentStatus))) {
                            ordersList.add(order);
                        }
                    }
                }
                Collections.reverse(ordersList); // Filter immediately after fetching
                ordersAdapter.notifyDataSetChanged();
                filterOrders(currentStatus); // Notify adapter about data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminOrderListActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void sendNewOrderNotification(Order order) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e("sendNewOrderNotification", "NotificationApp Manager not available");
            return;
        }

        Intent intent = new Intent(this, AdminOrderDetailsActivity.class);
        intent.putExtra("order", order);
        int notificationId = order.getOrderId() != null ? order.getOrderId().hashCode() : new Random().nextInt();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Order Received")
                .setContentText("Order ID: " + (order.getOrderId() != null ? order.getOrderId() : "Unknown ID"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
        Log.d("sendNewOrderNotification", "NotificationApp sent for Order ID: " + order.getOrderId());
    }


    private void setupSpinner() {
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentStatus = parent.getItemAtPosition(position).toString();
                fetchAllOrders(); // Refetch orders every time the status changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchView() {
        searchOrderView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchOrders(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchOrders(newText);
                return true;
            }
        });
    }

    private void filterOrders(String status) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : ordersList) {
            if (status.equals("All Statuses") || (order.getOrderStatus() != null && order.getOrderStatus().equalsIgnoreCase(status))) {
                filteredOrders.add(order);
            }
        }
        filteredList = new ArrayList<>(filteredOrders);
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
}
