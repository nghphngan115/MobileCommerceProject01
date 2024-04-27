package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {
    // ui views
    private ImageButton backBtn;
    private TextView productNameTv;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private Button submitBtn;
    private String productId;
    private FirebaseAuth firebaseAuth;
    private ImageView productImageIv;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        // Init ui views
        backBtn = findViewById(R.id.backBtn);
        productNameTv = findViewById(R.id.txtProductName);
        productImageIv = findViewById(R.id.imgProduct);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        submitBtn = findViewById(R.id.submitBtn);

        // Get product id from intent
        productId = getIntent().getStringExtra("productId");

        firebaseAuth = FirebaseAuth.getInstance();

        // Load product info, image
        loadProductInfo();
        // If user has written review for this product, load it
        loadMyReview();

        // Go back to previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Input data
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void loadProductInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
        ref.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get product info
                String productName = dataSnapshot.child("productName").getValue(String.class);
                String productImage = dataSnapshot.child("imageurl").getValue(String.class);

                // Set product info to UI
                productNameTv.setText(productName);
                try {
                    Picasso.get().load(productImage).placeholder(R.drawable.placeholder_image).into(productImageIv);
                } catch (Exception e) {
                    productImageIv.setImageResource(R.drawable.placeholder_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });
    }

    private void loadMyReview() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
        ref.child(productId).child("Reviews").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // My review is available for this product
                            // Get review details
                            Float ratings = dataSnapshot.child("ratings").getValue(Float.class);
                            String review = dataSnapshot.child("review").getValue(String.class);

                            // Set review details to UI
                            ratingBar.setRating(ratings);
                            reviewEt.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors here
                    }
                });
    }

    private void inputData() {
        String ratings = String.valueOf(ratingBar.getRating());
        String review = reviewEt.getText().toString().trim();

        // Timestamp for review
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Setup data in hashmap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", firebaseAuth.getUid());
        hashMap.put("ratings", ratings);
        hashMap.put("review", review);
        hashMap.put("timestamp", timestamp);

        // Put to database: DB > PRODUCTS > productId > Reviews > uid
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
        ref.child(productId).child("Reviews").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Review added to database
                        Toast.makeText(WriteReviewActivity.this, "Review published successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed adding review to database
                        Toast.makeText(WriteReviewActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
