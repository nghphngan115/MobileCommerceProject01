package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
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
import com.group01.plantique.adapter.BlogAdapter;
import com.group01.plantique.model.BlogItem;

import java.util.ArrayList;

public class BlogCategoryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    private DatabaseReference mDatabase;
    private ListView lvCart;
    private EditText edtSearch;
    private BlogAdapter adapter;
    private ArrayList<BlogItem> blogItems;
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
        imgButton2 = findViewById(R.id.imgButton2);

        // Initialize array list to store blog items
        blogItems = new ArrayList<>();

        // Initialize adapter and set it to list view
        adapter = new BlogAdapter(this, blogItems);
        lvCart.setAdapter(adapter);

        // Read data from Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous data
                blogItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get blog data and add to list
                    String blogId = snapshot.getKey();
                    String title = snapshot.child("blogTitle").getValue(String.class);
                    String imageUrl = snapshot.child("blogImage").getValue(String.class);
                    String content = snapshot.child("blogContent").getValue(String.class);
                    blogItems.add(new BlogItem(blogId, title, imageUrl, content));
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
                // Get selected blog item
                BlogItem selectedBlog = blogItems.get(position);

                // Open BlogDetailActivity with selected blog data
                Intent intent = new Intent(BlogCategoryActivity.this, BlogDetailActivity.class);
                intent.putExtra("blogId", selectedBlog.getBlogId());
                startActivity(intent);
            }
        });

        // Handle click event for imgButton
        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish current activity and return to previous activity
                finish();
            }
        });
    }
}
