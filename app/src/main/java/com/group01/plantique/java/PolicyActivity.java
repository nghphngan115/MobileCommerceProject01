package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.group01.plantique.R;

import com.group01.plantique.databinding.ActivityPolicyBinding;

public class PolicyActivity extends DrawerBaseActivity {


    ActivityPolicyBinding activityPolicyBinding;
    private TextView txtPolicyContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPolicyBinding=ActivityPolicyBinding.inflate(getLayoutInflater());
        setContentView(activityPolicyBinding.getRoot());
        allocateActivityTitle("Policy");
        TextView txtPolicyContent = findViewById(R.id.txtPolicyContent);
        txtPolicyContent.setText(Html.fromHtml(getString(R.string.policy_details), Html.FROM_HTML_MODE_COMPACT));




    }
















}
