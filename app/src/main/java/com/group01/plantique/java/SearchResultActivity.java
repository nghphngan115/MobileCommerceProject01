package com.group01.plantique.java;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.group01.plantique.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    TextView txtSearchResult;
    GridView gvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);
        addViews();


    }

    private void addViews() {
        txtSearchResult=findViewById(R.id.txtSearchResult);
        gvResult=findViewById(R.id.gvResult);

    }






}