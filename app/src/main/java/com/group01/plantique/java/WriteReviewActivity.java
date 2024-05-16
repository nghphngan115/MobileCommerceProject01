package com.group01.plantique.java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.group01.plantique.model.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WriteReviewActivity extends AppCompatActivity {
    private ImageView imgProduct;
    private TextView txtProductName;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private Button submitBtn;
    private ImageButton imgbtnBack;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ArrayList<String> productIds;
    private int currentIndex;

    private static final String TAG = "WriteReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        submitBtn = findViewById(R.id.submitBtn);
        imgbtnBack = findViewById(R.id.imgbtnBack);
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("reviews");
        mAuth = FirebaseAuth.getInstance();

        productIds = getIntent().getStringArrayListExtra("productIds");
        currentIndex = 0;

        if (productIds != null && !productIds.isEmpty()) {
            loadProductDetails(productIds.get(currentIndex));
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentIndex >= 0 && currentIndex < productIds.size()) {
                        String currentProductId = productIds.get(currentIndex);
                        submitReview(currentProductId);
                    }
                }
            });
        } else {
            Log.e(TAG, "No product IDs found in the intent");
        }
    }

    private void loadProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        txtProductName.setText(product.getProductName());
                        Picasso.get().load(product.getImageurl()).into(imgProduct);
                    } else {
                        Toast.makeText(WriteReviewActivity.this, getString(R.string.product_is_null), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WriteReviewActivity.this, getString(R.string.product_not_exist), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WriteReviewActivity.this, getString(R.string.failed_to_fetch_product_data), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", null);
        Log.d(TAG, "Retrieved userID: " + userId); // Add logging to check the user ID
        return userId;
    }

    private void fetchUserNameAndSubmitReview(String productId) {
        String userId = getUserId();
        if (userId != null && !userId.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("username").getValue(String.class);
                        if (userName != null && !userName.isEmpty()) {
                            submitReviewWithUserName(productId, userName);
                        } else {
                            Log.e(TAG, "User name is null or empty");

                        }
                    } else {
                        Log.e(TAG, "User does not exist");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to fetch user data", error.toException());

                }
            });
        } else {
            Log.e(TAG, "User is not logged in");

        }
    }

    private void submitReviewWithUserName(String productId, String userName) {
        double rating = ratingBar.getRating();
        String reviewText = reviewEt.getText().toString().trim();
        long timestamp = System.currentTimeMillis();

        String userId = getUserId(); // Ensure we have the userId
        Review review = new Review(userId, rating, reviewText, timestamp);
        review.setUserName(userName);

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(productId);
        reviewsRef.push().setValue(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Review submitted successfully");
                        Toast.makeText(WriteReviewActivity.this, getString(R.string.review_submitted_successfully), Toast.LENGTH_SHORT).show();
                        loadNextProductOrFinish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to submit review", e);
                        Toast.makeText(WriteReviewActivity.this, getString(R.string.failed_to_submit_review), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitReview(String productId) {
        fetchUserNameAndSubmitReview(productId);
    }

    private void loadNextProductOrFinish() {
        currentIndex++;
        if (currentIndex < productIds.size()) {
            loadProductDetails(productIds.get(currentIndex));
            ratingBar.setRating(0);
            reviewEt.setText("");
        } else {
            Intent intent = new Intent(WriteReviewActivity.this, HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
