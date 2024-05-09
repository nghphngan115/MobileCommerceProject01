package com.group01.plantique.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductOrderAdapter extends ArrayAdapter<Product> {

    public ProductOrderAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
    }

    static class ViewHolder {
        TextView txtProductName;
        TextView txtProductPrice;
        TextView txtBeforePrice;
        TextView txtProductQuantity;
        ImageView imgProductOrder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product_order_confirm, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtProductName = convertView.findViewById(R.id.txtProductName);
            viewHolder.txtProductPrice = convertView.findViewById(R.id.txtProductPrice);
            viewHolder.txtBeforePrice = convertView.findViewById(R.id.txtPriceBFDiscount);
            viewHolder.txtProductQuantity = convertView.findViewById(R.id.txtProductQuantity);
            viewHolder.imgProductOrder = convertView.findViewById(R.id.imgProductOrder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product product = getItem(position);
        if (product != null) {
            Log.d("ProductOrderAdapter", "Binding product: " + product.getProductName() + ", Quantity: " + product.getCartQuantity());

            viewHolder.txtProductName.setText(product.getProductName());
            if (product.getDiscount_price() > 0) {
                SpannableString spannable = new SpannableString(String.valueOf(product.getPrice() + "đ"));
                spannable.setSpan(new StrikethroughSpan(), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.txtBeforePrice.setText(spannable);
                viewHolder.txtBeforePrice.setVisibility(View.VISIBLE);
                viewHolder.txtProductPrice.setText(String.valueOf(product.getDiscount_price() + "đ"));
            } else {
                viewHolder.txtProductPrice.setText(String.valueOf(product.getPrice() + "đ"));
                viewHolder.txtBeforePrice.setVisibility(View.GONE);
            }
            viewHolder.txtProductQuantity.setText(String.format(getContext().getString(R.string.quantity), product.getCartQuantity()));

            Log.d("AdapterDebug", "Product Name: " + product.getProductName() + ", Quantity Display: " + product.getCartQuantity());

            if (product.getImageurl() != null && !product.getImageurl().isEmpty()) {
                Picasso.get().load(product.getImageurl()).into(viewHolder.imgProductOrder);
            } else {
                Picasso.get().load(R.drawable.ic_arrow_right).into(viewHolder.imgProductOrder);
            }
        }

        return convertView;
    }

}
