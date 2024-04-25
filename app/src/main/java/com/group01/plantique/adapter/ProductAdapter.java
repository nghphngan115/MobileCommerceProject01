package com.group01.plantique.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private List<Product> filteredList; // Danh sách sản phẩm sau khi lọc

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
        this.filteredList = new ArrayList<>(productList); // Khởi tạo filteredList ban đầu
    }

    // Phương thức filterList để cập nhật danh sách sản phẩm sau khi lọc
    public void filterList(List<Product> filteredList) {
        this.filteredList = new ArrayList<>(filteredList);
        notifyDataSetChanged(); // Cập nhật RecyclerView sau khi thay đổi danh sách sản phẩm
    }
    public void updateProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged(); // Cập nhật giao diện sau khi thay đổi dữ liệu
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_horizontal, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
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

        public void bind(Product product) {
            TextView textViewName = view.findViewById(R.id.textViewProductName);
            textViewName.setText(product.getProductName());

            TextView textViewPrice = view.findViewById(R.id.textViewProductPrice);
            TextView txtDiscountPrice = view.findViewById(R.id.textViewProductDiscount);

            String discountPrice = String.valueOf(product.getDiscount_price());

            if (!discountPrice.isEmpty() && !discountPrice.equals("0")) {
                textViewPrice.setPaintFlags(textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textViewPrice.setText("$" + product.getPrice());
                txtDiscountPrice.setVisibility(View.VISIBLE);
                txtDiscountPrice.setText("$" + discountPrice);
            } else {
                textViewPrice.setPaintFlags(0);
                textViewPrice.setText("$" + product.getPrice());
                txtDiscountPrice.setVisibility(View.GONE);
            }

            ImageView imageView = view.findViewById(R.id.imageViewProduct);
            Picasso.get().load(product.getImageurl()).into(imageView);

            TextView txtDiscountNote = view.findViewById(R.id.txtDiscountNote);
            txtDiscountNote.setText(product.getDiscountNote());

            TextView txtUnit = view.findViewById(R.id.txtUnit);
            txtUnit.setText(product.getUnit());
        }
    }
}

