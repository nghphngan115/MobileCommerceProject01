package com.group01.plantique.java;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class AdminOrderListActivity extends AppCompatActivity {
    private RecyclerView ordersRecyclerView;
    private AdminOrderAdapter ordersAdapter;
    private List<Order> ordersList;
    private List<Order> filteredList = new ArrayList<>();
    private Spinner statusSpinner;
    private String currentStatus = "All Statuses";
    private SearchView searchOrderView;
    private ArrayAdapter<CharSequence> spinnerAdapter; // Spinner adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

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

    private void fetchAllOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("allorders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ordersList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.child("Orders").getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) {
                            ordersList.add(order);
                        }
                    }
                }
                filterOrders(currentStatus);
                Collections.reverse(ordersList);// Filter immediately after fetching
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AdminOrderListActivity", "Database error: " + databaseError.getMessage());
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
