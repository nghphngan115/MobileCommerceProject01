package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.group01.plantique.model.Review;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {
    private ImageView imgProduct;
    private TextView txtProductName;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private Button submitBtn;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private String productId;
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

        // Lấy productId từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getStringExtra("productId");
            if (productId != null && !productId.isEmpty()) {
                // productId hợp lệ, tiếp tục xử lý
                // Lấy thông tin sản phẩm từ Firebase hoặc nguồn dữ liệu khác
                loadProductDetails(productId);
            } else {
                // productId không hợp lệ, hiển thị thông báo lỗi
                Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Intent là null, hiển thị thông báo lỗi
            Toast.makeText(this, "Invalid intent", Toast.LENGTH_SHORT).show();
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview(productId);
            }
        });
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WriteReviewActivity.this, "Failed to fetch product data", Toast.LENGTH_SHORT).show();
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
                // Người dùng đã đăng nhập và productId không phải là null hoặc rỗng
                String userId = mAuth.getUid(); // Lấy userId từ FirebaseAuth
                double rating = ratingBar.getRating();
                String userName = getUserName(); // Lấy userName từ SharedPreferences
                String reviewText = reviewEt.getText().toString().trim();
                long timestamp = System.currentTimeMillis();

                Review review = new Review(userId, rating, reviewText, timestamp);
                review.setUserName(userName); // Gán userName vào đối tượng Review

                DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(productId);
                reviewsRef.push().setValue(review)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Review submitted successfully");
                                Toast.makeText(WriteReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WriteReviewActivity.this, HomeScreenActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to submit review", e);
                                Toast.makeText(WriteReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // productId là null hoặc rỗng
                Log.e(TAG, "Invalid product ID");
                Toast.makeText(WriteReviewActivity.this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Người dùng chưa đăng nhập
            showLoginPrompt();
        }
    }

    private void showLoginPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login Required");
        builder.setMessage("You need to be logged in to write a review. Do you want to log in now?");
        builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xóa trạng thái đăng nhập từ SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("userID");
                editor.remove("loginStatus"); // Nếu có lưu trạng thái đăng nhập
                editor.apply();

                // Chuyển đến LoginActivity
                Intent intent = new Intent(WriteReviewActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Kết thúc WriteReviewActivity
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}