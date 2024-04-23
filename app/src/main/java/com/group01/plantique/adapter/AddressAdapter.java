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
import com.group01.plantique.model.Address;

import java.util.ArrayList;

public class AddressAdapter extends ArrayAdapter<Address> {
    private Context mContext;
    private int mResource;

    public AddressAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Address> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            listItem = inflater.inflate(mResource, parent, false);
        }

        Address address = getItem(position);

        // Kiểm tra convertView và address có null không
        if (listItem != null && address != null) {
            TextView txtFullName = listItem.findViewById(R.id.txtFullName);
            TextView txtAddress1 = listItem.findViewById(R.id.txtAddress1);
            TextView txtAddress2 = listItem.findViewById(R.id.txtAddress2);
            TextView txtPhone = listItem.findViewById(R.id.txtPhone);

            // Kiểm tra TextView có null không
            if (txtFullName != null && txtAddress1 != null && txtAddress2 != null && txtPhone != null) {
                txtFullName.setText(address.getFullName());
                txtAddress1.setText(address.getAddress1());
                txtAddress2.setText(address.getDistrict() + ", " + address.getProvince());
                txtPhone.setText(address.getPhoneNumber());
            }
        }

        return listItem;
    }

}
