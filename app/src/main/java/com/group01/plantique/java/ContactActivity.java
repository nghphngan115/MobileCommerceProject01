package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityContactBinding;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;

public class ContactActivity extends DrawerBaseActivity {

    ActivityContactBinding activityContactBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContactBinding=ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(activityContactBinding.getRoot());
        allocateActivityTitle("Contact");
    }
}