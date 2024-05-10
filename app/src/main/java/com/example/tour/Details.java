package com.example.tour;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Details extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mMapView;
    private TextView txtTitle;
    private TextView txtDetails;

    private Button btnDirections;
    private ImageView imageView;

    private String placeName;
    private String description;
    private double latitude;
    private double longitude;

    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initializeViews();

        dbHelper = new DbHelper(this);

        retrieveExtrasFromIntent();

        retrieveDataFromDatabase();

        setupMapView(savedInstanceState);


        btnDirections.setOnClickListener(v -> showDirections());

        retrieveImageFromDatabase();
    }

    private void initializeViews() {
        txtTitle = findViewById(R.id.txttitle);
        txtDetails = findViewById(R.id.txtdetails);

        btnDirections = findViewById(R.id.btnDirections);
        mMapView = findViewById(R.id.mapView);
        imageView = findViewById(R.id.imageView2);
    }

    private void retrieveExtrasFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            placeName = extras.getString("placeName");
            description = extras.getString("description");
        }
    }

    private void retrieveDataFromDatabase() {
        Cursor cursor = dbHelper.getVisitPlaceByName(placeName);
        if (cursor != null && cursor.moveToFirst()) {
            int descriptionIndex = cursor.getColumnIndex(DbHelper.DESCRIPTION);
            description = cursor.getString(descriptionIndex);
            cursor.close();
        }
        txtTitle.setText(placeName);
        txtDetails.setText(description);
        retrieveLocationFromDatabase(placeName);
    }

    private void setupMapView(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    private void retrieveImageFromDatabase() {
        Cursor cursor = dbHelper.getImageByPlaceName(placeName);
        if (cursor != null && cursor.moveToFirst()) {
            int imageIndex = cursor.getColumnIndex(DbHelper.IMAGE);
            do {
                byte[] imageBytes = cursor.getBlob(imageIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(bitmap);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(location).title(placeName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    private void retrieveLocationFromDatabase(String placeName) {
        if (placeName != null) {
            Cursor cursor = dbHelper.getLocationByPlaceName(placeName);
            if (cursor != null && cursor.moveToFirst()) {
                int latitudeIndex = cursor.getColumnIndex(DbHelper.LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(DbHelper.LONGITUDE);
                latitude = cursor.getDouble(latitudeIndex);
                longitude = cursor.getDouble(longitudeIndex);
                cursor.close();
            } else {
                latitude = 0.0;
                longitude = 0.0;
            }
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }
    }

    private void showLocationOnMap(double latitude, double longitude) {
        Uri geoLocation = Uri.parse("geo:" + latitude + "," + longitude + "?z=16");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoLocation);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private void showDirections() {
        Uri directionsUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, directionsUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        dbHelper.close();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
