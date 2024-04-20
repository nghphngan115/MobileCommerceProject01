package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;
import com.group01.plantique.databinding.ActivitySettingNotification2Binding;

public class SettingNotificationActivity2 extends DrawerBaseActivity {

    ActivitySettingNotification2Binding activitySettingNotification2Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingNotification2Binding=ActivitySettingNotification2Binding.inflate(getLayoutInflater());
        setContentView(activitySettingNotification2Binding.getRoot());
        allocateActivityTitle("Order address");
    }
}