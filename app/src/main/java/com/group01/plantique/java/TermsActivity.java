package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.group01.plantique.R;
import com.group01.plantique.databinding.ActivityPersonalInfoBinding;
import com.group01.plantique.databinding.ActivityTermsBinding;

public class TermsActivity extends DrawerBaseActivity {

    ActivityTermsBinding activityTermsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTermsBinding=ActivityTermsBinding.inflate(getLayoutInflater());
        setContentView(activityTermsBinding.getRoot());
        allocateActivityTitle("Terms");
        TextView termsTextView = findViewById(R.id.textViewTerms);
        termsTextView.setText(Html.fromHtml(getString(R.string.terms_and_conditions), Html.FROM_HTML_MODE_COMPACT));

    }

}