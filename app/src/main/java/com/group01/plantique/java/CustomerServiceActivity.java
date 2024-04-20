package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.group01.plantique.R;

public class CustomerServiceActivity extends AppCompatActivity {
    LinearLayout llContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cskh);
        addViews();
        addEvents();
    }

    private void addViews() {
        llContact=findViewById(R.id.llContact);
    }

    private void addEvents() {
        llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerServiceActivity.this, ContactActivity.class));

            }
        });
    }
}