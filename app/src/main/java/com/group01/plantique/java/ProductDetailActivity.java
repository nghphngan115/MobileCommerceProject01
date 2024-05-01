package com.group01.plantique.java;


import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.group01.plantique.adapter.ReviewAdapter;
import com.group01.plantique.model.Product;
import com.group01.plantique.model.Review;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;


public class ProductDetailActivity extends AppCompatActivity {


    private ImageView imageViewProduct;
    private TextView textViewProductName;
    private TextView textViewDiscountNote;
    private TextView textViewProductDescription;
    private TextView textViewProductPrice;
    private TextView textViewProductDiscountPrice;
    private ImageButton imgbtnBack;


    private DatabaseReference productsRef;


    private ListView listViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;




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


        listViewReviews = findViewById(R.id.listViewReviews);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        listViewReviews.setAdapter(reviewAdapter);


        String productId = getIntent().getStringExtra("productId");
        if (productId != null) {
            productsRef = FirebaseDatabase.getInstance().getReference().child("products").child(productId);
            loadProductDetails(productId);
            loadReviews(productId);
        } else {
            Toast.makeText(this, getString(R.string.failed_to_fetch_data), Toast.LENGTH_SHORT).show();
        }
    }


    private void loadProductDetails(String productId) {
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


    private void loadReviews(String productId) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(productId);
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        reviewList.add(review);
                    }
                }
                reviewAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, getString(R.string.failed_to_fetch_data), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
