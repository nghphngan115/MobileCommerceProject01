package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CustomInfoWindowAdapter;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        String imageUrl = getIntent().getStringExtra("image");

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set up Custom InfoWindow Adapter
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        // Create and add a marker for the location
        LatLng shopLocation = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(shopLocation)
                .title(name)
                .snippet(address);
        Marker marker = mMap.addMarker(markerOptions);

        // Set image URL as a tag for the marker to be used in the info window
        marker.setTag(imageUrl);

        // Move the camera to the location and optionally show the info window immediately
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLocation, 15));
        // Uncomment the following line if you want the info window to show immediately when the map is opened
        // marker.showInfoWindow();
    }
}