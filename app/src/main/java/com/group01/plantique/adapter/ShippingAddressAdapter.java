package com.group01.plantique.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.group01.plantique.R;
import com.group01.plantique.model.ShippingAddress;

import java.util.ArrayList;

public class ShippingAddressAdapter extends ArrayAdapter<ShippingAddress> {
    private Context mContext;
    private int mResource;
    private int defaultAddressPosition = 0;
    private String defaultAddressId = "";

    public ShippingAddressAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ShippingAddress> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }
    public int getDefaultAddressPosition() {
        return defaultAddressPosition;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            listItem = inflater.inflate(mResource, parent, false);
        }

        ShippingAddress shippingAddress = getItem(position);

        // Kiểm tra convertView và address có null không
        if (listItem != null && shippingAddress != null) {
            TextView txtFullName = listItem.findViewById(R.id.txtFullName);
            TextView txtAddress1 = listItem.findViewById(R.id.txtAddress1);
            TextView txtAddress2 = listItem.findViewById(R.id.txtAddress2);
            TextView txtPhone = listItem.findViewById(R.id.txtPhoneNumber);

            // Kiểm tra TextView có null không
            if (txtFullName != null && txtAddress1 != null && txtAddress2 != null && txtPhone != null) {
                txtFullName.setText(shippingAddress.getFullName());
                txtAddress1.setText(shippingAddress.getAddress1());
                txtAddress2.setText(shippingAddress.getWard() + ", " + shippingAddress.getDistrict() + ", " + shippingAddress.getProvince());
                txtPhone.setText(shippingAddress.getPhoneNumber());
            }
            // Kiểm tra nếu địa chỉ đang xét là địa chỉ mặc định

            if (shippingAddress.getAddressId().equals(defaultAddressId)) {
                defaultAddressPosition = position; // Cập nhật vị trí địa chỉ mặc định
            }
        }

        return listItem;
    }


}
