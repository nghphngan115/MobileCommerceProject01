package com.group01.plantique.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> cartProducts;
    private OnQuantityChangeListener quantityChangeListener;

    public CartListAdapter(Context context, ArrayList<Product> cartProducts) {
        this.context = context;
        this.cartProducts = cartProducts;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange();
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    @Override
    public int getCount() {
        return cartProducts != null ? cartProducts.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return cartProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_in_cart_show, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtProductName = convertView.findViewById(R.id.txtProductName);
            viewHolder.txtProductPrice = convertView.findViewById(R.id.txtProductPrice);
            viewHolder.edtProductQuantity = convertView.findViewById(R.id.edtProductQuantity);
            viewHolder.imgProductShow = convertView.findViewById(R.id.imgProductShow);
            viewHolder.imgBin = convertView.findViewById(R.id.imgBin);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product product = cartProducts.get(position);
        viewHolder.txtProductName.setText(product.getProductName());
        viewHolder.txtProductPrice.setText(String.format("%d đ", product.getPrice()));

        viewHolder.edtProductQuantity.removeTextChangedListener(viewHolder.textWatcher);
        viewHolder.edtProductQuantity.setText(String.valueOf(product.getQuantity()));
        viewHolder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int newQuantity = Integer.parseInt(s.toString());
                    if (newQuantity != product.getQuantity() && newQuantity > 0 && newQuantity <= product.getStock()) {
                        product.setQuantity(newQuantity);
                        if (quantityChangeListener != null) {
                            quantityChangeListener.onQuantityChange();
                        }
                    } else {
                        viewHolder.edtProductQuantity.setText(String.valueOf(product.getQuantity()));
                    }
                } catch (NumberFormatException e) {
                    viewHolder.edtProductQuantity.setText(String.valueOf(product.getQuantity()));
                }
            }
        };
        viewHolder.edtProductQuantity.addTextChangedListener(viewHolder.textWatcher);

        viewHolder.imgBin.setOnClickListener(v -> {
            cartProducts.remove(position);
            notifyDataSetChanged();
            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityChange();
            }
        });

        Picasso.get().load(product.getImageUrl()).into(viewHolder.imgProductShow);

        return convertView;
    }

    static class ViewHolder {
        TextView txtProductName;
        TextView txtProductPrice;
        EditText edtProductQuantity;
        ImageView imgProductShow;
        ImageView imgBin;
        TextWatcher textWatcher;
    }
}
