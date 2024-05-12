package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import java.util.stream.Collectors;

public class BlogCategoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView rvBlog;
    private BlogAdapter adapter;
    private ArrayList<BlogItem> blogItems;
    private ArrayList<BlogItem> filteredBlogItems;
    private DatabaseReference mDatabase;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_category);

        setupNavigationView();
        initializeFirebaseAndRecyclerView();
        setupSearchView();
    }

    private void setupNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.blog);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.blog) {
                return true;
            } else if (itemId == R.id.home) {
                switchActivity(HomeScreenActivity.class);
                return true;
            } else if (itemId == R.id.cart) {
                switchActivity(CartActivity.class);
                return true;
            } else if (itemId == R.id.notification) {
                switchActivity(NotificationActivity.class);
                return true;
            } else if (itemId == R.id.account) {
                switchActivity(UserInformationActivity.class);
                return true;
            }
            return false;
        });
    }

    private void switchActivity(Class<?> activityClass) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
        finish();
    }

    private void initializeFirebaseAndRecyclerView() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        rvBlog = findViewById(R.id.rvBlog);
        blogItems = new ArrayList<>();
        filteredBlogItems = new ArrayList<>();
        adapter = new BlogAdapter(this, filteredBlogItems);
        rvBlog.setAdapter(adapter);
        rvBlog.setLayoutManager(new LinearLayoutManager(this));
        loadDataFromFirebase();
    }

    private void setupSearchView() {
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
    }

    private void loadDataFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blogItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BlogItem blog = snapshot.getValue(BlogItem.class);
                    blogItems.add(blog);
                }
                filteredBlogItems.clear();
                filteredBlogItems.addAll(blogItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BlogCategoryActivity.this, "Failed to load blogs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterBlogs(newText);
        return true;
    }

    private void filterBlogs(String query) {
        filteredBlogItems.clear();
        if (query.isEmpty()) {
            filteredBlogItems.addAll(blogItems);
        } else {
            filteredBlogItems.addAll(blogItems.stream()
                    .filter(blog -> blog.getBlogTitle().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        adapter.notifyDataSetChanged();
    }
}
