package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends DrawerBaseActivity {
    ActivityAboutUsBinding activityAboutUsBinding;
    Button btnMuaNgay;
    ImageButton btnBack, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityAboutUsBinding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(activityAboutUsBinding.getRoot());
        allocateActivityTitle(getString(R.string.nav_about_us));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupButtons();
    }

    private void setupButtons() {
        btnMuaNgay = findViewById(R.id.btnMuaNgay);
        btnMap = findViewById(R.id.btnMap);
        btnBack = findViewById(R.id.btnBack); // Ensure btnBack is in the XML with correct ID

        if (btnMap != null) {
            btnMap.setOnClickListener(v -> openMap());
        } else {
            Log.e("AboutUsActivity", "ImageButton btnMap is null");
        }

        if (btnMuaNgay != null) {
            btnMuaNgay.setOnClickListener(v -> {
                Intent intent = new Intent(AboutUsActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e("AboutUsActivity", "Button btnMuaNgay is null");
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        } else {
            Log.e("AboutUsActivity", "ImageButton btnBack is null");
        }
    }

    private void openMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("latitude", 10.869650919143284);
        intent.putExtra("longitude", 106.77860939273114);
        intent.putExtra("name", "Plantique");
        intent.putExtra("address", "669 QL1A Thu Duc");
        intent.putExtra("image", "https://vcdn-dulich.vnecdn.net/2023/07/26/cb1-1690342765-1195-1690342797.jpg");
        startActivity(intent);
    }
}
