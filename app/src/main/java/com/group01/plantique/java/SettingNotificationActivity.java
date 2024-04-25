package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityNotificationBinding;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;
import com.group01.plantique.databinding.ActivitySettingNotificationBinding;

public class SettingNotificationActivity extends DrawerBaseActivity {
    Switch switchAllow;
    ActivitySettingNotificationBinding activitySettingNotificationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingNotificationBinding=ActivitySettingNotificationBinding.inflate(getLayoutInflater());
        setContentView(activitySettingNotificationBinding.getRoot());
        allocateActivityTitle("Notification");
        switchAllow=findViewById(R.id.switchAllow);
        switchAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    createNotification();
                }

            }
        });
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String CHANNEL_ID = "app_noti";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
