package com.group01.plantique.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.group01.plantique.R;
import com.group01.plantique.model.Product;

import java.util.ArrayList;

public class ProductOrderAdapter extends ArrayAdapter<Product> {
    public ProductOrderAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product_order_confirm, parent, false);
        }
        TextView txtProductName = convertView.findViewById(R.id.txtProductName);
        TextView txtProductPrice = convertView.findViewById(R.id.txtProductPrice);
        TextView txtProductQuantity = convertView.findViewById(R.id.txtProductQuantity);

        txtProductName.setText(product.getProductName());
        txtProductPrice.setText(String.format("%s đ", product.getPrice()));
        txtProductQuantity.setText(String.format("Qty: %d", product.getCartQuantity()));

        return convertView;
    }
}

