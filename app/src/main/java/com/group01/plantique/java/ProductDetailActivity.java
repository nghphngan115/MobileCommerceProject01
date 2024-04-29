package com.group01.plantique.java;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.adapter.ReviewAdapter;
import com.group01.plantique.model.ModelReview;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageViewProduct;
    private TextView textViewProductName;
    private TextView textViewDiscountNote;
    private TextView textViewProductDescription;
    private TextView textViewProductPrice;
    private TextView textViewProductDiscountPrice;
    private ImageButton imgbtnBack;

    private DatabaseReference productsRef;
    private RatingBar ratingBar; //ANH THU
    private ArrayList<ModelReview> reviewArrayList; //ANH THU, contain list of all reviews
    private ReviewAdapter adapterReview; //ANH THU
    private FirebaseAuth firebaseAuth; //ANH THU
    private RecyclerView reviewRv; //ANH THU
    private TextView ratingsTv; //ANH THU



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imageViewProduct = findViewById(R.id.imgProduct);
        textViewProductName = findViewById(R.id.txtProductName);
        textViewProductDescription = findViewById(R.id.txtDescription);
        textViewProductPrice = findViewById(R.id.txtPrice);
        textViewProductDiscountPrice = findViewById(R.id.txtDiscountPrice);
        textViewDiscountNote = findViewById(R.id.txtDiscountNote);
        imgbtnBack = findViewById(R.id.imgbtnBack);

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ratingBar = findViewById(R.id.ratingBar); //ANH THU

        String productId = getIntent().getStringExtra("productId");
        productsRef = FirebaseDatabase.getInstance().getReference().child("products").child(productId);


        //ANH THU THEM KHUC NAY

        reviewRv = findViewById(R.id.reviewRv);
        ratingsTv = findViewById(R.id.txtNumberOfReviews);

        firebaseAuth = FirebaseAuth.getInstance();
        loadReview(); //for review list
    }
        private float ratingSum = 0;
        private void loadReview() {
            //init list
            reviewArrayList = new ArrayList<>();
            DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
            ref.child("Ratings")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //clear list before adding data to it
                            reviewArrayList.clear();
                            ratingSum = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                                ratingSum = ratingSum +rating;

                                ModelReview modelReview = ds.getValue(ModelReview.class);
                                reviewArrayList.add(modelReview);
                            }
                            //set up adapter
                            adapterReview = new ReviewAdapter(ProductDetailActivity.this, reviewArrayList);
                            //set to recyclerview
                            reviewRv.setAdapter(adapterReview);

                            long numberOfReviews = snapshot.getChildrenCount();
                            float avgRating= ratingSum/numberOfReviews;

                            ratingsTv.setText(String.format("%.2f", avgRating) + "[" +numberOfReviews+ "]");
                            ratingBar.setRating(avgRating);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            // ANH THU DONE




        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        displayProductDetails(product);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, getString(R.string.failed_to_fetch_data), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductDetails(Product product) {
        textViewProductName.setText(product.getProductName());
        textViewProductDescription.setText(product.getDescription());

        String discountPrice = String.valueOf(product.getDiscount_price());

        if (!discountPrice.isEmpty() && !discountPrice.equals("0")) {
            textViewProductPrice.setPaintFlags(textViewProductPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textViewProductPrice.setText("$" + product.getPrice());
            textViewProductDiscountPrice.setVisibility(View.VISIBLE);
            textViewProductDiscountPrice.setText("$" + discountPrice);
        } else {
            textViewProductPrice.setPaintFlags(0);
            textViewProductPrice.setText("$" + product.getPrice());
            textViewProductDiscountPrice.setVisibility(View.GONE);
        }

        String imageUrl = product.getImageurl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(imageViewProduct);
        } else {
            Picasso.get().load(R.drawable.logo).into(imageViewProduct);
        }
        if (product.getDiscountNote() != null && !product.getDiscountNote().isEmpty()) {
            textViewDiscountNote.setVisibility(View.VISIBLE);
            textViewDiscountNote.setText(product.getDiscountNote());
        } else {
            textViewDiscountNote.setVisibility(View.GONE);
        }

    }
}