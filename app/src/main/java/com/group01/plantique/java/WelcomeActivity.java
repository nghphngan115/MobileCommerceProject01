package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.content.Intent;

import com.group01.plantique.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView signInTextView = findViewById(R.id.txtSignIn);
        TextView signUpTextView = findViewById(R.id.txtSignUp);

        // Set click listener for Sign in TextView
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apply animation
                animateView(v);

                // Start LoginActivity
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for Sign up TextView
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apply animation
                animateView(v);

                // Start SignUpActivity
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void animateView(View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        view.startAnimation(anim);
    }
}
