package com.group01.plantique.java;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityAboutUsBinding;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;

public class AboutUsActivity extends DrawerBaseActivity {
    ActivityAboutUsBinding activityAboutUsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityAboutUsBinding= ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(activityAboutUsBinding.getRoot());
        allocateActivityTitle("About us");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}