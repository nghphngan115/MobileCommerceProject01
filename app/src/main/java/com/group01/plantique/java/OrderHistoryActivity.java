package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityOrderHistoryBinding;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;

public class OrderHistoryActivity extends DrawerBaseActivity {

    ActivityOrderHistoryBinding activityOrderHistoryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderHistoryBinding=ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityOrderHistoryBinding.getRoot());
        allocateActivityTitle("Order");
    }
}