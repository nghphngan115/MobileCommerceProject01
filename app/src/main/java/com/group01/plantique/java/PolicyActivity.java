package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;
import com.group01.plantique.databinding.ActivityPolicyBinding;

public class PolicyActivity extends DrawerBaseActivity {


    ActivityPolicyBinding activityPolicyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPolicyBinding=ActivityPolicyBinding.inflate(getLayoutInflater());
        setContentView(activityPolicyBinding.getRoot());
        allocateActivityTitle("Policy");}}