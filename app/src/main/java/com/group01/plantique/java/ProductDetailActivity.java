package com.group01.plantique.java;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.CartUtility;
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
    private TextView textViewProductCategory;
    private TextView textviewUnit;
    private TextView textViewDiscountNote;
    private TextView textViewProductDescription;
    private TextView textViewProductPrice;
    private TextView textViewProductDiscountPrice;
    private ImageButton imgbtnBack;
private ConstraintLayout btnAddToCart;
    private DatabaseReference productsRef;
    private ListView listViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private TextView txtNumberOfReviews;
    private long totalReviews;

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
        textViewProductCategory = findViewById(R.id.txtProductCategory);
        textviewUnit = findViewById(R.id.txtUnit);
        btnAddToCart=findViewById(R.id.btnAddToCart);
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
        txtNumberOfReviews = findViewById(R.id.txtNumberOfReviews);

        loadProductDetails(productId);
    }
    private void loadProductDetails(String productId) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        displayProductDetails(product);

                        // Lấy danh sách đánh giá
                        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews").child(productId);
                        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {
                                totalReviews = reviewsSnapshot.getChildrenCount();
                                txtNumberOfReviews.setText(String.valueOf(getString(R.string.strReview)+" "+ totalReviews));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Xử lý lỗi khi lấy danh sách đánh giá
                            }
                        });
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
            textViewProductPrice.setText(product.getPrice() +"đ");
            textViewProductDiscountPrice.setVisibility(View.VISIBLE);
            textViewProductDiscountPrice.setText(discountPrice +"đ");
        } else {
            textViewProductPrice.setPaintFlags(0);
            textViewProductPrice.setText(product.getPrice() +"đ");
            textViewProductDiscountPrice.setVisibility(View.GONE);
        }

        String imageUrl = product.getImageurl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(imageViewProduct);
        } else {
            Picasso.get().load(R.drawable.logo).into(imageViewProduct);
        }
        String discountNote = product.getDiscountNote();
        if (discountNote != null && !discountNote.trim().isEmpty() && !discountNote.trim().equals("0")) {
            // Hiển thị discountNote và thiết lập văn bản
            textViewDiscountNote.setVisibility(View.VISIBLE);
            textViewDiscountNote.setText(discountNote);
        } else {
            // Ẩn discountNote nếu không hợp lệ
            textViewDiscountNote.setVisibility(View.GONE);
        }

        // Hiển thị cateName dựa trên categoryId và cateId
        String categoryId = product.getCategoryId();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("categories").child(categoryId);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cateName = snapshot.child("cateName").getValue(String.class);
                    textViewProductCategory.setText(cateName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, R.string.failed_to_fetch_category_data, Toast.LENGTH_SHORT).show();
            }
        });


        // Hiển thị unit
        textviewUnit.setText(getString(R.string.strUnit) + product.getUnit());
        setupAddToCartButton(product);

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
    private void setupAddToCartButton(Product product) {
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToCartDialog(product);
            }
        });
    }

    private void showAddToCartDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_pop_up, null);
        builder.setView(dialogView);

        ImageView productImageView = dialogView.findViewById(R.id.productIv);
        TextView productTitleTextView = dialogView.findViewById(R.id.productTitleTv);
        TextView currentPriceTextView = dialogView.findViewById(R.id.currentPriceTv);
        TextView beforeDiscountTextView = dialogView.findViewById(R.id.beforeDiscountTv);
        EditText productQuantityTextView = dialogView.findViewById(R.id.productQuantityTv);
        ImageButton btnMinus = dialogView.findViewById(R.id.btnMinus);
        ImageButton btnPlus = dialogView.findViewById(R.id.btnPlus);
        Button btnAddToCartDialog = dialogView.findViewById(R.id.btnAddToCart);

        // Set the product details in the dialog
        productTitleTextView.setText(product.getProductName());
        if (!product.getImageurl().isEmpty()) {
            Picasso.get().load(product.getImageurl()).into(productImageView);
        }
        // Price setting logic as per discount
        if (product.getDiscount_price() > 0 && product.getDiscount_price() != product.getPrice()) {
            beforeDiscountTextView.setText(product.getPrice() + "đ");
            currentPriceTextView.setText(product.getDiscount_price() + "đ");
            beforeDiscountTextView.setPaintFlags(beforeDiscountTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            beforeDiscountTextView.setVisibility(View.VISIBLE);
        } else {
            currentPriceTextView.setText(product.getPrice() + "đ");
            beforeDiscountTextView.setVisibility(View.GONE);
        }

        // Quantity increment and decrement handlers
        btnMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(productQuantityTextView.getText().toString());
            if (quantity > 1) {
                quantity--;
                productQuantityTextView.setText(String.valueOf(quantity));
            }
        });
        btnPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(productQuantityTextView.getText().toString());
            quantity++;
            productQuantityTextView.setText(String.valueOf(quantity));
        });
        AlertDialog dialog = builder.create();
        // Adding to cart
        btnAddToCartDialog.setOnClickListener(v -> {
            int quantity = Integer.parseInt(productQuantityTextView.getText().toString());
            addProductToCart(product, quantity);
            dialog.dismiss();
        });


        dialog.show();
    }

    private void addProductToCart(Product product, int quantity) {
        ArrayList<Product> cartProducts = CartUtility.getCartProducts(ProductDetailActivity.this);
        boolean found = false;

        for (Product p : cartProducts) {
            if (p.getProductId().equals(product.getProductId())) {
                p.setCartQuantity(p.getCartQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            product.setCartQuantity(quantity);
            cartProducts.add(product);
        }

        CartUtility.saveCartProducts(ProductDetailActivity.this, cartProducts);
        Toast.makeText(ProductDetailActivity.this, getString(R.string.product_added_to_cart), Toast.LENGTH_SHORT).show();
    }

}