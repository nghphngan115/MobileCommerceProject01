package com.group01.plantique.adapter;

import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
        void onQuantityChanged();
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
            viewHolder.txtPriceBFDiscount = convertView.findViewById(R.id.txtPriceBFDiscount);
            viewHolder.edtProductQuantity = convertView.findViewById(R.id.edtProductQuantity);
            viewHolder.btnMinus = convertView.findViewById(R.id.btnMinus);
            viewHolder.btnPlus = convertView.findViewById(R.id.btnPlus);
            viewHolder.imgProductShow = convertView.findViewById(R.id.imgProductShow);
            viewHolder.imgBin = convertView.findViewById(R.id.imgBin);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product product = cartProducts.get(position);
        viewHolder.txtProductName.setText(product.getProductName());
        if (product.getDiscount_price() > 0) {
            SpannableString spannable = new SpannableString(String.valueOf(product.getPrice() + "đ"));
            spannable.setSpan(new StrikethroughSpan(), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.txtPriceBFDiscount.setText(spannable);
            viewHolder.txtPriceBFDiscount.setVisibility(View.VISIBLE);
            viewHolder.txtProductPrice.setText(String.valueOf(product.getDiscount_price() + "đ"));
        } else {
            viewHolder.txtProductPrice.setText(String.valueOf(product.getPrice() + "đ"));
            viewHolder.txtPriceBFDiscount.setVisibility(View.GONE);
        }
        viewHolder.edtProductQuantity.setText(String.valueOf(product.getCartQuantity()));
        viewHolder.edtProductQuantity.setFilters(new InputFilter[]{new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("0") && dest.toString().equals("")) {
                    return "";
                }
                return null;
            }
        }});

        viewHolder.btnMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(viewHolder.edtProductQuantity.getText().toString());
            if (quantity > 1) {
                viewHolder.edtProductQuantity.setText(String.valueOf(--quantity));
                product.setCartQuantity(quantity);
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged();
                }
            }
        });

        viewHolder.btnPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(viewHolder.edtProductQuantity.getText().toString());
            viewHolder.edtProductQuantity.setText(String.valueOf(++quantity));
            product.setCartQuantity(quantity);
            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityChanged();
            }
        });

        viewHolder.edtProductQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String quantityStr = viewHolder.edtProductQuantity.getText().toString();
                if (!TextUtils.isEmpty(quantityStr)) {
                    int quantity = Integer.parseInt(quantityStr);
                    product.setCartQuantity(quantity);
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged();
                    }
                } else {
                    viewHolder.edtProductQuantity.setError(context.getString(R.string.quantity_blank_error_message));
                }
            }
        });

        viewHolder.imgBin.setOnClickListener(v -> {
            cartProducts.remove(position);
            notifyDataSetChanged();
            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityChanged();
            }
        });

        String imageUrl = product.getImageurl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(viewHolder.imgProductShow);
        } else {
            Picasso.get().load(R.drawable.logo).into(viewHolder.imgProductShow);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtProductName;
        TextView txtProductPrice;
        TextView txtPriceBFDiscount;
        EditText edtProductQuantity;
        ImageButton btnMinus;
        ImageButton btnPlus;
        ImageView imgProductShow;
        ImageView imgBin;
    }
}
