package com.group01.plantique;

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
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class BlogCategoryActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView lvCart;
    private EditText edtSearch;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> blogTitles;
    private ArrayList<String> blogImages;
    private ImageView imgButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_category);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        // Initialize views
        lvCart = findViewById(R.id.lvCart);
        edtSearch = findViewById(R.id.edtSearch);
        imgButton1 = findViewById(R.id.imgButton2); // Ánh xạ imgButton

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
            }
        });

        // Handle click event for a blog
        lvCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = blogTitles.get(position);
                String imageUrl = blogImages.get(position);
                // Khi một blog được click, chuyển sang BlogDetailActivity và truyền dữ liệu về blog được click
                Intent intent = new Intent(BlogCategoryActivity.this, BlogDetailActivity.class);
                intent.putExtra("blogTitle", title);
                intent.putExtra("blogImage", imageUrl);
                startActivity(intent);
            }
        });
        // Thiết lập sự kiện click cho imgButton
        imgButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc activity hiện tại và quay lại activity trước đó
                finish();
            }
        });

        // Handle search functionality
        edtSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String searchText = edtSearch.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    // Perform search
                    performSearch(searchText);
                    return true;
                }
            }
            return false;
        });
    }

    // Method to perform search
    private void performSearch(String searchText) {
        ArrayList<String> searchResults = new ArrayList<>();
        for (String title : blogTitles) {
            if (title.toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(title);
            }
        }
        // Clear previous data and add search results to adapter
        blogTitles.clear();
        blogTitles.addAll(searchResults);
        adapter.notifyDataSetChanged();
    }

}
