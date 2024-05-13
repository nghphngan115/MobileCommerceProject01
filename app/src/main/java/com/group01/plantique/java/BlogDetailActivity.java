package com.group01.plantique.java;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.squareup.picasso.Picasso;

public class BlogDetailActivity extends AppCompatActivity {

    private ImageView imgButton2;
    private ImageView imgBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        // Initialize views
        TextView txtTitle = findViewById(R.id.txtTitle1);
        TextView txtContent = findViewById(R.id.txtContent);
        TextView txtAuthor = findViewById(R.id.txtAuthor);
        TextView txtCreateDate = findViewById(R.id.txtCreateDate);
        imgBlog = findViewById(R.id.imgBlog);
        imgButton2 = findViewById(R.id.imgButton2);

        // Get data from intent
        String blogTitle = getIntent().getStringExtra("blogTitle");
        String blogImage = getIntent().getStringExtra("blogImage");
        String blogContent = getIntent().getStringExtra("blogContent");
        String blogAuthor = getIntent().getStringExtra("blogAuthor");
        String blogDate = getIntent().getStringExtra("blogDate");

        // Display data
        txtTitle.setText(blogTitle);
        txtAuthor.setText(blogAuthor);
        txtCreateDate.setText(blogDate);
        txtContent.setText(blogContent);
        Glide.with(this).load(blogImage).into(imgBlog);
        // Get blog ID from intent
        String blogId = getIntent().getStringExtra("blogId");

        // Reference to the blog in Firebase
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("Blog").child(blogId);

        // Listen for changes in Firebase data
        blogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the blog exists
                if (dataSnapshot.exists()) {
                    // Get blog data from dataSnapshot
                    String title = dataSnapshot.child("blogTitle").getValue(String.class);
                    String content = dataSnapshot.child("blogContent").getValue(String.class);
                    String imageUrl = dataSnapshot.child("blogImage").getValue(String.class);
                    String author=dataSnapshot.child("blogAuthor").getValue(String.class);
                    String date=dataSnapshot.child("blogDate").getValue(String.class);
                    // Display blog data in layout
                    txtTitle.setText(title);
                    txtContent.setText(content);
                    txtAuthor.setText(author);
                    txtCreateDate.setText(date);
                    // Use Picasso to load image from URL and display in ImageView
                    Picasso.get().load(imageUrl).into(imgBlog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // Handle click event for imgButton
        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish current activity and return to previous activity (BlogCategoryActivity)
                finish();
            }
        });
    }
}
