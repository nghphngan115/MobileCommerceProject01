package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.model.ModelReview;
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

        // Lấy thông tin sản phẩm từ Intent (nếu có)
        Intent intent = getIntent();
        if (intent != null) {
            String productName = intent.getStringExtra("productName");
            int productImage = intent.getIntExtra("productImage", 0);
            if (productName != null) {
                txtProductName.setText(productName);
            }
            if (productImage != 0) {
                imgProduct.setImageResource(productImage);
            }
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    double rating = ratingBar.getRating();
                    String reviewText = reviewEt.getText().toString().trim();
                    long timestamp = System.currentTimeMillis();

                    ModelReview review = new ModelReview(userId, rating, reviewText, timestamp);

                    databaseReference.child(userId).push().setValue(review)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(WriteReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(WriteReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Xử lý trường hợp người dùng chưa đăng nhập
                    Toast.makeText(WriteReviewActivity.this, "Please sign in to submit a review", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}