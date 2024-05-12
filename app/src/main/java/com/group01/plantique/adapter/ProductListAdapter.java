package com.group01.plantique.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.java.CartActivity;
import com.group01.plantique.java.ProductDetailActivity;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {
    private List<Product> productList;

    public ProductListAdapter(List<Product> productList) {
        this.productList = productList;
    }

    // Constructor không tham số
    public ProductListAdapter() {
        productList = new ArrayList<>(); // Khởi tạo danh sách sản phẩm trống
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product != null) {
            holder.setProductDetails(product);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setProductDetails(Product product) {
            if (product != null) {
                TextView textViewName = view.findViewById(R.id.textViewProductName);
                textViewName.setText(product.getProductName());

                TextView textViewPrice = view.findViewById(R.id.textViewProductPrice);
                TextView txtDiscountPrice = view.findViewById(R.id.textViewProductDiscount);

                // Convert int discountPrice to String
                String discountPrice = String.valueOf(product.getDiscount_price());

                if (!discountPrice.isEmpty() && !discountPrice.equals("0")) {
                    // If discount_price is not empty and not zero, strike through textViewProductPrice and show discountPrice
                    textViewPrice.setPaintFlags(textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    textViewPrice.setText(product.getPrice()+"đ");
                    txtDiscountPrice.setVisibility(View.VISIBLE);
                    txtDiscountPrice.setText(discountPrice+"đ");
                } else {
                    // If discount_price is empty, zero, or null, show regular price and hide discount_price
                    textViewPrice.setPaintFlags(0); // Remove strike through if present
                    textViewPrice.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.main_green));
                    textViewPrice.setText(product.getPrice()+"đ");
                    txtDiscountPrice.setVisibility(View.GONE);
                }

                ImageView imageView = view.findViewById(R.id.imageViewProduct);
                String imageUrl = product.getImageurl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imageView);
                } else {
                    // Xử lý khi URL trống, ví dụ load ảnh mặc định
                    Picasso.get().load(R.drawable.logo).into(imageView);
                }

                TextView txtDiscountNote = view.findViewById(R.id.txtDiscountNote);
                String discountNote = product.getDiscountNote();
                if (discountNote != null && !discountNote.trim().isEmpty() && !discountNote.trim().equals("0") && !discountNote.trim().equals("0%")) {
                    // Hiển thị discountNote và thiết lập văn bản
                    txtDiscountNote.setVisibility(View.VISIBLE);
                    txtDiscountNote.setText(discountNote);
                } else {
                    // Ẩn discountNote nếu không hợp lệ
                    txtDiscountNote.setVisibility(View.GONE);
                }

                TextView txtUnit = view.findViewById(R.id.txtUnit);
                txtUnit.setText(product.getUnit());
            } else {
                Log.e("ProductListActivity", "Product object is null");
            }

            // Set click listener for product detail
            view.findViewById(R.id.layoutProductDetail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openProductDetailActivity(product);
                }
            });

            // Set click listener for add to cart area
            view.findViewById(R.id.constraintLayoutAddToCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCartActivity(product);
                }
            });
            view.findViewById(R.id.imageButtonCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCartActivity(product);
                }
            });
        }

        private void openProductDetailActivity(Product product) {
            Context context = view.getContext();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            context.startActivity(intent);
        }

        private void openCartActivity(Product product) {
            Context context = view.getContext();
            Intent intent = new Intent(context, CartActivity.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("productName", product.getProductName());
            intent.putExtra("price", product.getPrice());
            context.startActivity(intent);
        }
    }
}
