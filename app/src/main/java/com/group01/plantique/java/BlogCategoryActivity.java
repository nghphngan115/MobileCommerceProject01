package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private RecyclerView rvBlog;
    private BlogAdapter adapter;
    private ArrayList<BlogItem> blogItems;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_category);

        //Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.blog);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.blog) {
                return true;
            } else if (itemId == R.id.home) {
                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
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
                startActivity(new Intent(getApplicationContext(), UserInformationActivity.class));
                finish();
                return true;
            }
            return false;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        rvBlog = findViewById(R.id.rvBlog);
        blogItems = new ArrayList<>();
        adapter = new BlogAdapter(this, blogItems);
        rvBlog.setAdapter(adapter);
        rvBlog.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new BlogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BlogItem selectedBlog = blogItems.get(position);
                Intent intent = new Intent(BlogCategoryActivity.this, BlogDetailActivity.class);
                intent.putExtra("blogId", selectedBlog.getBlogId());
                intent.putExtra("blogTitle", selectedBlog.getBlogTitle());
                intent.putExtra("blogImage", selectedBlog.getBlogImage());
                intent.putExtra("blogContent", selectedBlog.getBlogContent());
                startActivity(intent);
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blogItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BlogItem blog = snapshot.getValue(BlogItem.class);
                    blogItems.add(blog);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BlogCategoryActivity.this, "Failed to load blogs.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}