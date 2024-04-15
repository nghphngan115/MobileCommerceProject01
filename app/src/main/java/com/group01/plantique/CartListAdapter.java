package com.group01.plantique;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class CartListAdapter extends ArrayAdapter<Product> {
    private Context mContext;
    private ArrayList<Product> mProducts;

    public CartListAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
        mContext = context;
        mProducts = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.product_in_cart_show, parent, false);
        }

        Product currentProduct = mProducts.get(position);

        TextView productName = listItem.findViewById(R.id.textView6);
        productName.setText(currentProduct.getName());

        TextView productPrice = listItem.findViewById(R.id.textView9);
        productPrice.setText(String.valueOf(currentProduct.getPrice()) + " đ");

        TextView productQuantity = listItem.findViewById(R.id.edtQuantityInCart);
        productQuantity.setText(String.valueOf(currentProduct.getQuantity()));

        return listItem;
    }
}
