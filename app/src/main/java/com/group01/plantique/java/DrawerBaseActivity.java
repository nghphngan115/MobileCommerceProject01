package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.group01.plantique.R;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int itemId = item.getItemId();

        if (itemId == R.id.nav_account) {
            startActivity(new Intent(this, PersonalInfoActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.nav_noti) {
            startActivity(new Intent(this, NotificationActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.nav_order) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.nav_order_address) {
            startActivity(new Intent(this, SettingAddressActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.nav_about_us) {
            startActivity(new Intent(this, AboutUsActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.nav_policy) {
            startActivity(new Intent(this, PolicyActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.nav_term) {
            startActivity(new Intent(this, TermsActivity.class));
            overridePendingTransition(0, 0);
        } else if (itemId == R.id.nav_contact) {
            startActivity(new Intent(this, ContactActivity.class));
            overridePendingTransition(0, 0);
        }

        return false;
    }

    protected void allocateActivityTitle(String titleString) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleString);
        }
    }


}