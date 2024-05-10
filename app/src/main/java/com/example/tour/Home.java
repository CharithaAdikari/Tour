package com.example.tour;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Home extends AppCompatActivity implements Map_Fragment.OnMapClickListener {

    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText editTextVisitPlaceName, editTextVisitPlaceDescription;
    private ImageView imageViewSelected;
    private ImageView imageView;
    private Button btnSubmit, btnUpdate;
    private double latitude;
    private double longitude;
    private ImageButton btnSelectImage;
    private Bitmap selectedImageBitmap;
    private Button btnDelete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DbHelper dbHelper = new DbHelper(this);

        Button Search = findViewById(R.id.btnsearch);

        btnDelete = findViewById(R.id.btndelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to delete the visit place
                deleteVisitPlace();
            }
        });


        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the home.java activity
                searchVisitPlace(v);
            }
        });

        Button home = findViewById(R.id.btnhomefromhome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, mainscreen.class);
                startActivity(intent);
            }
        });

        // Set click listener for update button
        btnUpdate = findViewById(R.id.btnupdate);
        // Set click listener for update button
        // Set click listener for update button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String oldPlaceName = editTextVisitPlaceName.getText().toString().trim(); // Get the existing place name
                String newPlaceName = editTextVisitPlaceName.getText().toString().trim(); // Get the new place name
                String description = editTextVisitPlaceDescription.getText().toString().trim();

                // Check if all fields are filled
                if (!oldPlaceName.isEmpty() && !newPlaceName.isEmpty() && !description.isEmpty() && selectedImageBitmap != null) {
                    // Convert Bitmap to byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    // Update the visit place details in the database
                    dbHelper.updateVisitPlace(oldPlaceName, newPlaceName, description, byteArray, latitude, longitude);
                } else {
                    // Show toast message if any field is empty
                    Toast.makeText(Home.this, "Please search all fields and select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });




        Fragment fragment = new Map_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapcontainer, fragment).commit();

        // Initialize views
        editTextVisitPlaceName = findViewById(R.id.editTextVisitPlaceName);
        editTextVisitPlaceDescription = findViewById(R.id.editTextVisitPlaceDescription);
        imageViewSelected = findViewById(R.id.imageView);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        ((Map_Fragment) fragment).setOnMapClickListener(this);

        // Set click listener for image selection button
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open device storage to select an image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        // Set click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String placeName = editTextVisitPlaceName.getText().toString().trim();
                String description = editTextVisitPlaceDescription.getText().toString().trim();

                if (!placeName.isEmpty() && !description.isEmpty() && selectedImageBitmap != null && latitude != 0.0 && longitude != 0.0) {
                    // Save the details in the database
                    saveVisitPlace(placeName, description, selectedImageBitmap, latitude, longitude);
                } else {
                    // Show toast message if any field is empty or latitude/longitude are not available
                    Toast.makeText(Home.this, "Please fill all fields, select an image, and choose a location on the map", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            // Get selected image URI
            Uri imageUri = data.getData();
            try {
                // Convert URI to Bitmap
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);

                // Display selected image
                imageViewSelected.setImageBitmap(selectedImageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to save visit place details in the database
    private void saveVisitPlace(String placeName, String description, Bitmap imageBitmap, double latitude, double longitude) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Convert Bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Save details in the database
        dbHelper.addVisitPlace(placeName, description, byteArray, latitude, longitude);

        // Close the database
        db.close();

        // Show success message
        Toast.makeText(this, "Visit place details saved successfully", Toast.LENGTH_SHORT).show();

        // Clear input fields and image
        editTextVisitPlaceName.setText("");
        editTextVisitPlaceDescription.setText("");
        imageViewSelected.setImageBitmap(null);
    }

    @Override
    public void onMapClick(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void searchVisitPlace(View view) {
        // Get the search query
        SearchView searchView = findViewById(R.id.search);
        imageView = findViewById(R.id.imageView);
        String placeName = searchView.getQuery().toString().trim();

        DbHelper dbHelper = new DbHelper(this);

        if (!placeName.isEmpty()) {
            // Search for the visit place in the database
            Cursor cursor = dbHelper.getVisitPlaceByName(placeName);

            if (cursor != null && cursor.moveToFirst()) {
                // Populate data into EditText fields4

                editTextVisitPlaceName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.PLACE_NAME)));

                editTextVisitPlaceDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.DESCRIPTION)));


                // You may need to handle loading the image here
                int imageIndex = cursor.getColumnIndex(DbHelper.IMAGE);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                imageView.setImageBitmap(bitmap);
                // Get latitude and longitude if needed
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.LATITUDE));
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.LONGITUDE));
            } else {
                // No visit place found with the given name
                Toast.makeText(this, "Visit place not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Empty search query
            Toast.makeText(this, "Please enter a visit place name to search", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteVisitPlace() {
        String placeName = editTextVisitPlaceName.getText().toString().trim();

        // Check if the place name is not empty
        if (!placeName.isEmpty()) {
            DbHelper dbHelper = new DbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Define the where clause
            String whereClause = DbHelper.PLACE_NAME + "=?";
            String[] whereArgs = {placeName};

            // Delete the visit place from the database
            int rowsDeleted = db.delete(DbHelper.TABLE_VISIT_PLACE, whereClause, whereArgs);

            // Check if deletion was successful
            if (rowsDeleted > 0) {
                // Show success message
                Toast.makeText(this, "Visit place deleted successfully", Toast.LENGTH_SHORT).show();

                // Clear input fields and image
                editTextVisitPlaceName.setText("");
                editTextVisitPlaceDescription.setText("");
                imageViewSelected.setImageBitmap(null);
            } else {
                // Show failure message
                Toast.makeText(this, "Failed to delete visit place", Toast.LENGTH_SHORT).show();
            }

            // Close the database
            db.close();
        } else {
            // Show message if place name is empty
            Toast.makeText(this, "Please enter a visit place name", Toast.LENGTH_SHORT).show();
        }
    }














}
