package com.group01.plantique.java;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group01.plantique.R;
import com.group01.plantique.model.BlogItem;

public class AddBlogActivity extends AppCompatActivity {

    private ImageView blogIconIv;
    private Button submitBlogBtn;
    private Uri imageUri;


    private static final String TAG = "AddBlogActivity";
    private EditText blogTitleEdt, blogContentEdt;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private final ActivityResultLauncher<String> contentPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri; // Save the URI globally if you need to use it later
                    blogIconIv.setImageURI(uri);
                    uploadImageToFirebase(uri); // Call your method to handle image upload
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        storageReference = FirebaseStorage.getInstance().getReference();

        blogTitleEdt = findViewById(R.id.blogTitle);
        blogContentEdt = findViewById(R.id.blogContentedt);
        submitBlogBtn = findViewById(R.id.edtBlogBtn);
        blogIconIv = findViewById(R.id.blogIconIv);

        blogIconIv.setOnClickListener(v -> openImagePicker());


        submitBlogBtn.setOnClickListener(v -> submitBlog());
    }
    private void openImagePicker() {
        contentPickerLauncher.launch("image/*"); // This opens the document picker for images
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child("blog_images/" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        this.imageUri = uri; // Update the global imageUri with the actual URL from Firebase
                        blogIconIv.setImageURI(imageUri);
                        Toast.makeText(AddBlogActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddBlogActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    private void submitBlog() {
        String blogTitle = blogTitleEdt.getText().toString();
        String blogContent = blogContentEdt.getText().toString();
        String blogImage = (imageUri != null) ? imageUri.toString() : "default_image_url_here"; // Replace with actual image URL or default

        if (!blogTitle.isEmpty() && !blogContent.isEmpty()) {
            String blogId = databaseReference.push().getKey(); // Generates a new unique key for each blog post
            BlogItem blogItem = new BlogItem(blogId, blogTitle, blogContent, blogImage);
            databaseReference.child(blogId).setValue(blogItem)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddBlogActivity.this, "Blog added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddBlogActivity.this, "Failed to add blog", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(AddBlogActivity.this, AdminBlogListActivity.class);
        startActivity(intent);
        finish();
    }

}
