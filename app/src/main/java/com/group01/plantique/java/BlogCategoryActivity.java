package com.group01.plantique.java;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;

import java.util.ArrayList;

public class BlogCategoryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    private DatabaseReference mDatabase;
    private ListView lvCart;
    private EditText edtSearch;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> blogTitles;
    private ArrayList<String> blogImages;
    private ImageView imgButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_category);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        // Initialize views
        lvCart = findViewById(R.id.lvCart);
        edtSearch = findViewById(R.id.edtSearch);
        imgButton2 = findViewById(R.id.imgButton2); // Ánh xạ imgButton

        // Initialize array lists to store blog titles and images
        blogTitles = new ArrayList<>();
        blogImages = new ArrayList<>();

        // Initialize array adapter and set it to list view
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, blogTitles);
        lvCart.setAdapter(adapter);

        // Read data from Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous data
                blogTitles.clear();
                blogImages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get blog title and image URL and add them to the lists
                    String title = snapshot.child("Title").getValue(String.class);
                    String imageUrl = snapshot.child("Image").getValue(String.class);
                    blogTitles.add(title);
                    blogImages.add(imageUrl);
                }
                // Notify adapter that data set has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                // Log error message or show error toast
            }
        });

        // Handle click event for a blog
        lvCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = blogTitles.get(position);
                String imageUrl = blogImages.get(position);
                // Chuyển sang BlogDetailActivity và chờ đợi kết quả
                Intent intent = new Intent(BlogCategoryActivity.this, BlogDetailActivity.class);
                intent.putExtra("blogTitle", title);
                intent.putExtra("blogImage", imageUrl);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // Thiết lập sự kiện click cho imgButton
        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc activity hiện tại và quay lại activity trước đó
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.blog);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.blog) {
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
    }
}
