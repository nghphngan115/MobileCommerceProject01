package com.group01.plantique.java;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.squareup.picasso.Picasso;

public class AdminEditBlogActivity extends AppCompatActivity {

    private EditText blogTitleEdt, blogContentEdt;
    private ImageView blogIconIv;
    private Button updateBlogBtn, deleteBlogBtn;
    private String blogId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_blog);

        blogId = getIntent().getStringExtra("blogId");

        blogTitleEdt = findViewById(R.id.blogTitle);
        blogContentEdt = findViewById(R.id.blogContentedt);
        blogIconIv = findViewById(R.id.imgBlog);
        updateBlogBtn = findViewById(R.id.btnUpdateBlog);
        deleteBlogBtn = findViewById(R.id.btnDeleteBlog);

        updateBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBlog();
            }
        });

        deleteBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBlog();
            }
        });

        loadBlogDataFromFirebase(blogId);
    }

    private void loadBlogDataFromFirebase(String blogId) {
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference().child("Blog").child(blogId);
        blogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.child("blogTitle").getValue(String.class);
                    String content = dataSnapshot.child("blogContent").getValue(String.class);
                    String imageUrl = dataSnapshot.child("blogImage").getValue(String.class);

                    blogTitleEdt.setText(title);
                    blogContentEdt.setText(content);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(blogIconIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminEditBlogActivity.this, "Failed to load blog data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBlog() {
        String newTitle = blogTitleEdt.getText().toString();
        String newContent = blogContentEdt.getText().toString();

        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference().child("Blog").child(blogId);
        blogRef.child("blogTitle").setValue(newTitle);
        blogRef.child("blogContent").setValue(newContent);

        Toast.makeText(AdminEditBlogActivity.this, "Blog updated successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void deleteBlog() {
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference().child("Blog").child(blogId);
        blogRef.removeValue();

        Toast.makeText(AdminEditBlogActivity.this, "Blog deleted successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
