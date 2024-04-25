
package com.group01.plantique.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CartListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> cartProducts;

    public CartListAdapter(Context context, ArrayList<Product> cartProducts) {
        this.context = context;
        this.cartProducts = cartProducts;
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

        // Kiểm tra xem danh sách sản phẩm có trống không
        if (cartProducts.isEmpty()) {
            // Xử lý trường hợp giỏ hàng trống
            // Ví dụ: ẩn các thành phần hoặc hiển thị một thông báo
            return convertView;
        }

        // Tiếp tục xử lý như thông thường nếu giỏ hàng không trống
        Product product = cartProducts.get(position);
        viewHolder.txtProductName.setText(product.getProductName());
        viewHolder.txtProductPrice.setText(String.valueOf(product.getPrice()));
        viewHolder.edtProductQuantity.setText(String.valueOf(product.getStock()));
        Picasso.get().load(product.getImageurl()).into(viewHolder.imgProductShow);

        viewHolder.imgBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProducts.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtProductName;
        TextView txtProductPrice;
        EditText edtProductQuantity;
        ImageView imgProductShow;
        ImageView imgBin;
    }
}
