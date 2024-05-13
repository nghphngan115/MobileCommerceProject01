package com.group01.plantique.java;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivitySettingNotificationBinding;

public class SettingNotificationActivity extends DrawerBaseActivity {
    Switch switchAllow;
    private static final String CHANNEL_ID = "plantique";
    private static final int PERMISSION_REQUEST_NOTIFICATION_POLICY = 1001;
    private SharedPreferences sharedPreferences;

    ActivitySettingNotificationBinding activitySettingNotificationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingNotificationBinding = ActivitySettingNotificationBinding.inflate(getLayoutInflater());
        setContentView(activitySettingNotificationBinding.getRoot());
        setupNotificationChannel();

        allocateActivityTitle(getString(R.string.nav_notification));
        switchAllow = findViewById(R.id.switchAllow);
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        updateSwitchState();

        switchAllow.setOnClickListener(v -> allowNotification());
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Plantique Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications from Plantique app");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void allowNotification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Show an explanation dialog before redirecting
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This app needs notification access to function properly. Please enable it in the next screen.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Intent to open the specific app's notification settings
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        // for Android 8 and above
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, getApplicationInfo().uid);

                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            // If permission is already granted, update the UI accordingly
            updateSwitchState();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_NOTIFICATION_POLICY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updatePreferences(true);
            } else {
                updatePreferences(false);
            }
        }
    }

    private void updatePreferences(boolean hasPermission) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NotificationPermission", hasPermission);
        editor.apply();
        updateSwitchState();
    }

    private void updateSwitchState() {
        boolean isAllowed = sharedPreferences.getBoolean("NotificationPermission", false);
        switchAllow.setChecked(isAllowed);
        if (isAllowed) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }
}
