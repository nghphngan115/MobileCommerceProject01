package com.group01.plantique.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.group01.plantique.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View window;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        this.window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }

    private void renderWindowText(Marker marker, View view) {
        TextView titleView = view.findViewById(R.id.title);
        TextView addressView = view.findViewById(R.id.address);
        ImageView imageView = view.findViewById(R.id.image);

        titleView.setText(marker.getTitle());
        addressView.setText(marker.getSnippet());

        String imageUrl = (String) marker.getTag();
        if (imageUrl != null) {
            // Using Glide to load image
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.logo)) // Placeholder image while loading
                    .into(imageView);
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;  // Returning null uses default info window background
    }
}
