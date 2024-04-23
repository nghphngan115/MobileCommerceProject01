package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityNotificationBinding;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;
import com.group01.plantique.databinding.ActivitySettingNotificationBinding;

public class SettingNotificationActivity extends DrawerBaseActivity {

    ActivitySettingNotificationBinding activitySettingNotificationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingNotificationBinding=ActivitySettingNotificationBinding.inflate(getLayoutInflater());
        setContentView(activitySettingNotificationBinding.getRoot());
        allocateActivityTitle("Notification");
    }
}