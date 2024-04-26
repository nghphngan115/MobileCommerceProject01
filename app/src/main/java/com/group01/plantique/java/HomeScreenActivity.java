package com.group01.plantique.java;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.group01.plantique.R;
import com.group01.plantique.adapter.CategoryAdapter;
import com.group01.plantique.model.Category;
import com.group01.plantique.model.Product;

public class HomeScreenActivity extends AppCompatActivity {

    SearchView svSearch;
    Button btnMuangay, btnViewAll, btnViewAll2;
    RecyclerView rvCategory;
    List<Category> categoryList;
    CategoryAdapter categoryAdapter;


    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.blog) {
                startActivity(new Intent(getApplicationContext(), BlogCategoryActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.notification) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.account) {
                startActivity(new Intent(getApplicationContext(), PersonalInfoActivity.class));
                finish();
                return true;
            }
            return false;
        });

        svSearch = findViewById(R.id.svSearch);
        btnMuangay = findViewById(R.id.btnMuaNgay);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnViewAll2 = findViewById(R.id.btnViewAll2);

        rvCategory = findViewById(R.id.rvCategory);
        rvCategory.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("categories"),Category.class)
                .build();

        categoryAdapter = new CategoryAdapter(options);
        rvCategory.setAdapter(categoryAdapter);


        //Search
        svSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Intent để chuyển từ HomeScreenActivity sang SearchProductActivity
                Intent intent = new Intent(HomeScreenActivity.this, SearchActivity.class);
                // Bắt đầu SearchProductActivity
                startActivity(intent);
            }
        });

        // Bạn có thể giữ nguyên phần này nếu muốn xử lý khi người dùng thay đổi query
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Không cần xử lý khi submit vì đã chuyển màn hình khi bấm vào khung search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Bạn có thể giữ nguyên hoặc xóa phương thức này, vì sẽ không cần thiết khi đã chuyển màn hình
                return false;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        categoryAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
    }
}