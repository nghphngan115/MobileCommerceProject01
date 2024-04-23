package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;

public class PersonalInfoActivity extends DrawerBaseActivity {
    BottomNavigationView bottomNavigationView;
    ActivityPersonalInfoBinding activityPersonalInfoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPersonalInfoBinding=ActivityPersonalInfoBinding.inflate(getLayoutInflater());
        setContentView(activityPersonalInfoBinding.getRoot());
        allocateActivityTitle("Personal account");

        // Initialize and setup BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.account);

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
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.account) {
                // Already on the account page
                return true;
            }
            return false;
        });
    }
}