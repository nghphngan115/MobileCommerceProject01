package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.adapter.AdminBlogAdapter;
import com.group01.plantique.model.BlogItem;

import java.util.ArrayList;

public class AdminBlogListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminBlogAdapter adapter;
    private ArrayList<BlogItem> blogItems;
    private DatabaseReference databaseReference;
    private ImageButton btnAddProduct,btnBack;

    private static final int EDIT_BLOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_blog_list);

        recyclerView = findViewById(R.id.blogsadminRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        blogItems = new ArrayList<>();
        adapter = new AdminBlogAdapter(this, blogItems);
        recyclerView.setAdapter(adapter);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnBack=findViewById(R.id.btnBack);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến AddBlogActivity khi nút được nhấn
                Intent intent = new Intent(AdminBlogListActivity.this, AddBlogActivity.class);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseReference.addValueEventListener(new ValueEventListener() {
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
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminBlogListActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        setupSearchView();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchBlog);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        ArrayList<BlogItem> filteredList = new ArrayList<>();
        for (BlogItem item : blogItems) {
            if (item.getBlogTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter = new AdminBlogAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);
    }
}
