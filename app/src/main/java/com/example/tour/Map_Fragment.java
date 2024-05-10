package com.example.tour;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map_Fragment extends Fragment {
    public interface OnMapClickListener {
        void onMapClick(double latitude, double longitude);
    }

    // Reference to the interface
    private OnMapClickListener mapClickListener;

    // Setter method for the interface reference
    public void setOnMapClickListener(OnMapClickListener listener) {
        this.mapClickListener = listener;
    }@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_,container,false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                // Set bounds for Sri Lanka
                LatLngBounds sriLankaBounds = new LatLngBounds(
                        new LatLng(5.9258, 79.7590),  // Southwest bound
                        new LatLng(9.8284, 81.9598)); // Northeast bound
                googleMap.setLatLngBoundsForCameraTarget(sriLankaBounds);
                // Set default zoom level
                LatLng defaultLocation = new LatLng(7.8731, 80.7718); // Default location  center of Sri Lanka)
                float defaultZoomLevel = 8; // Default zoom level
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoomLevel));
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude+" KG " + latLng.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
                        googleMap.addMarker(markerOptions);

                        // Call the interface method to pass latitude and longitude
                        if (mapClickListener != null) {
                            mapClickListener.onMapClick(latLng.latitude, latLng.longitude);
                        }
                    }
                });
            }
        });
        return view;
    }
}
