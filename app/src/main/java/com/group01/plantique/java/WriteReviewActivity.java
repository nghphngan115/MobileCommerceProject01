package com.group01.plantique.java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    ArrayList<String> productIds;
    int currentIndex;

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

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("reviews");
        mAuth = FirebaseAuth.getInstance();

        // Get productId from Intent
        Intent intent = getIntent();
        String productId = null;

        // Inside onCreate or wherever you initialize your activity
        productIds = getIntent().getStringArrayListExtra("productIds");
        currentIndex = 0;
        if (intent != null) {
            if (productIds != null && !productIds.isEmpty()) {
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentIndex >= 0 && currentIndex < productIds.size()) {
                            String currentProductId = productIds.get(currentIndex);
                            // Write review for the current product
                            writeReview(currentProductId);
                            submitBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    submitReview(currentProductId);
                                    writeReview(currentProductId);
                                }
                            });
                        }
                    }
                });

            }
        }
    }

    private void writeReview(String productId) {
        // Assuming you have logic to write a review for the product
        // After writing the review, move to the next item
        if (currentIndex < productIds.size()) {
            // If there are more items, load details of the next item
            loadProductDetails(productIds.get(currentIndex));
            currentIndex++;
        } else {
            Intent intent = new Intent(WriteReviewActivity.this, HomeScreenActivity.class);
            startActivity(intent);
            finish();
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
                        // Check if image URL is not empty or null
                        if (product.getImageurl() != null && !product.getImageurl().isEmpty()) {
                            // Initialize views before using them
                            txtProductName.setText(product.getProductName());
                            Picasso.get().load(product.getImageurl()).into(imgProduct);
                        } else {
                            Toast.makeText(WriteReviewActivity.this, getString(R.string.product_image_url_empty), Toast.LENGTH_SHORT).show();
                        }

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

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", null);
        String userName = sharedPreferences.getString("userName", null);
        return userId != null && !userId.isEmpty() && userName != null && !userName.isEmpty();
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        return sharedPreferences.getString("userName", null);
    }

    private void submitReview(String productId) {
        if (isUserLoggedIn()) {
            if (productId != null && !productId.isEmpty()) {
                // User is logged in and productId is not null or empty
                String userId = mAuth.getUid(); // Get userId from FirebaseAuth
                double rating = ratingBar.getRating();
                String userName = getUserName(); // Get userName from SharedPreferences
                String reviewText = reviewEt.getText().toString().trim();
                long timestamp = System.currentTimeMillis();

                Review review = new Review(userId, rating, reviewText, timestamp);
                review.setUserName(userName); // Set userName to the Review object

                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(productId);
                reviewsRef.push().setValue(review)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Review submitted successfully");
                                Toast.makeText(WriteReviewActivity.this, getString(R.string.review_submitted_successfully), Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to submit review", e);
                                Toast.makeText(WriteReviewActivity.this, getString(R.string.failed_to_submit_review), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // productId is null or empty
                Log.e(TAG, "Invalid product ID");
                Toast.makeText(WriteReviewActivity.this, getString(R.string.invalid_product_id), Toast.LENGTH_SHORT).show();
            }
        } else {
            // User is not logged in
            showLoginPrompt();
        }
    }

    private void showLoginPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.login_required_title));
        builder.setMessage(getString(R.string.login_required_message));
        builder.setPositiveButton(getString(R.string.login_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove login status from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("userID");
                editor.remove("loginStatus"); // If saving login status
                editor.apply();

                // Navigate to LoginActivity
                Intent intent = new Intent(WriteReviewActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish WriteReviewActivity
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_button_label), null);
        builder.show();
    }
}
