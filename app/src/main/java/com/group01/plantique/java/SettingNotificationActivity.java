package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityNotificationBinding;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;
import com.group01.plantique.databinding.ActivitySettingNotificationBinding;

public class SettingNotificationActivity extends DrawerBaseActivity {
    Switch switchAllow;
    ImageButton imgbtnBack;

    private static final String CHANNEL_ID = "plantique";
    private static final String CHANNEL_NAME ="Plantique";
    private static final String CHANNEL_DESC = "Plantique Notification";

    private FirebaseAuth firebaseAuth;


    ActivitySettingNotificationBinding activitySettingNotificationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingNotificationBinding=ActivitySettingNotificationBinding.inflate(getLayoutInflater());
        setContentView(activitySettingNotificationBinding.getRoot());
        allocateActivityTitle("Notification");
         switchAllow=findViewById(R.id.switchAllow);
         imgbtnBack=findViewById(R.id.imgbtnBack);

         firebaseAuth = firebaseAuth.getInstance();
         imgbtnBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onBackPressed();
             }
         });
    }
    private void allowNotification(String title, String message){
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

    }
}