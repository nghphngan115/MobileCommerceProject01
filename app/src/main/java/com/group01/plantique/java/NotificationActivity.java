package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.R;
import com.group01.plantique.adapter.NotificationAdapter;
import com.group01.plantique.model.NotificationApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationApp> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.notification);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.blog) {
                startActivity(new Intent(getApplicationContext(), BlogCategoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.notification) {
                return true;
            } else if (itemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), UserInformationActivity.class));
                finish();
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = loadNotifications();

        adapter = new NotificationAdapter(this, notificationList, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClicked() {
                startActivity(new Intent(NotificationActivity.this, OrderHistoryActivity.class));
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private List<NotificationApp> loadNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("notifications", null);
        if (json == null) {
            return new ArrayList<>(); // Return an empty list if no data is found.
        }
        Type type = new TypeToken<ArrayList<NotificationApp>>(){}.getType();
        Gson gson = new Gson();
        List<NotificationApp> notifications = gson.fromJson(json, type); // Deserialize JSON into Java objects
        Collections.reverse(notifications); // Reverse the list to display the newest notification first
        return notifications;
    }

}
