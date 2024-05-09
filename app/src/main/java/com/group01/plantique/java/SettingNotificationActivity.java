package com.group01.plantique.java;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivitySettingNotificationBinding;
import com.group01.plantique.model.NotificationApp;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SettingNotificationActivity extends DrawerBaseActivity {
    Switch switchAllow;
    ImageButton imgbtnBack;

    private static final String CHANNEL_ID = "plantique";
    private static final String CHANNEL_NAME = "Plantique";
    private static final String CHANNEL_DESC = "Plantique NotificationApp";

    private FirebaseAuth firebaseAuth;


    ActivitySettingNotificationBinding activitySettingNotificationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingNotificationBinding = ActivitySettingNotificationBinding.inflate(getLayoutInflater());
        setContentView(activitySettingNotificationBinding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        allocateActivityTitle("NotificationApp");
        switchAllow = findViewById(R.id.switchAllow);
        imgbtnBack = findViewById(R.id.imgbtnBack);

        firebaseAuth = firebaseAuth.getInstance();
        switchAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowNotification();
            }
        });


    }



    private void allowNotification() {
        Intent intent = new Intent(this, com.group01.plantique.java.HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mNotificationMgr.notify(1, mbuilder.build());
        saveNotificationToPreferences(getString(R.string.notification_title), getString(R.string.notification_title));
    }

    private void saveNotificationToPreferences(String title, String content) {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        List<NotificationApp> notifications = loadNotifications();
        notifications.add(new NotificationApp(title, content, R.mipmap.ic_notification)); // Assuming an icon is associated
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notifications);
        editor.putString("notifications", json);
        editor.apply();
    }

    private List<NotificationApp> loadNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("notifications", null);
        Type type = new TypeToken<ArrayList<NotificationApp>>() {}.getType();
        List<NotificationApp> notifications = gson.fromJson(json, type);
        return notifications != null ? notifications : new ArrayList<>();
    }

}
