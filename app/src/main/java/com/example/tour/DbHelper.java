package com.example.tour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "DB";
    private static final int DB_VERSION = 1;

    //tables
    private static final String TABLE_USER = "user";
    public static final String TABLE_VISIT_PLACE = "visitplace";

    //user table columns
    private static final String ID = "id";
    private static final String FNAME = "fname";
    private static final String LNAME = "lname";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String CONFIRM_PASSWORD = "confirm_password";

    //visitplace table columns
    public static final String PLACE_NAME = "placename";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    private final Context mContext;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;

        // Check if the database exists, if not, copy it from assets
        try {
            createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DbHelper", "Error creating database: " + e.getMessage());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables if they don't exist
        db.execSQL(createUserTableQuery());
        db.execSQL(createVisitPlaceTableQuery());
    }

    // Method to create the database by copying from assets
    private void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();

        if (!dbExist) {
            // Open the prepopulated database file in the assets folder
            InputStream inputStream = mContext.getAssets().open(DB_NAME);

            // Create a new database file in the app's internal storage directory
            String outFileName = mContext.getDatabasePath(DB_NAME).getPath();
            OutputStream outputStream = new FileOutputStream(outFileName);

            // Copy the contents of the input stream to the output stream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Close streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }
    }


    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String path = mContext.getDatabasePath(DB_NAME).getPath();
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            // Database doesn't exist yet
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = mContext.getAssets().open(DB_NAME);
        String outFileName = mContext.getDatabasePath(DB_NAME).getPath();
        OutputStream outputStream = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    // Method to register a user
    public void registerUser(String fname, String lname, String email, String password, String confirmPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FNAME, fname);
        values.put(LNAME, lname);
        values.put(EMAIL, email);
        values.put(PASSWORD, password);
        values.put(CONFIRM_PASSWORD, confirmPassword);
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // Method to check if a user can log in
    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isLoggedIn = false;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE "
                    + EMAIL + " = ? AND " + PASSWORD + " = ?";
            cursor = db.rawQuery(selectQuery, new String[]{email, password});
            if (cursor != null && cursor.getCount() > 0) {
                isLoggedIn = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return isLoggedIn;
    }

    // Method to add a visit place
    public void addVisitPlace(String placeName, String description, byte[] image, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLACE_NAME, placeName);
        values.put(DESCRIPTION, description);
        values.put(IMAGE, image);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        db.insert(TABLE_VISIT_PLACE, null, values);
        db.close();
    }

    public void updateVisitPlace(String oldPlaceName, String newPlaceName, String description, byte[] image, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLACE_NAME, newPlaceName); // Update placeName
        values.put(DESCRIPTION, description); // Update description
        values.put(IMAGE, image); // Update image
        values.put(LATITUDE, latitude); // Update latitude
        values.put(LONGITUDE, longitude); // Update longitude
        String whereClause = PLACE_NAME + "=?";
        String[] whereArgs = {oldPlaceName};
        db.update(TABLE_VISIT_PLACE, values, whereClause, whereArgs);
        db.close();
    }




    public Cursor getAllVisitPlaces() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_VISIT_PLACE;
        return db.rawQuery(selectQuery, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISIT_PLACE);
        onCreate(db);
    }

    public Cursor getLocationByPlaceName(String placeName) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] projection = {
                LATITUDE,
                LONGITUDE
        };

        // Define the selection criteria
        String selection = PLACE_NAME + " = ?";
        String[] selectionArgs = { placeName };

        // Query the database
        return db.query(TABLE_VISIT_PLACE, projection, selection, selectionArgs, null, null, null);
    }

    public Cursor getImageByPlaceName(String placeName){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {IMAGE};
        String selection = PLACE_NAME+"=?";
        String[] selectionArgs = {placeName};

        return db.query(TABLE_VISIT_PLACE, projection, selection, selectionArgs, null, null, null);
    }

    // Query to create user table
    private String createUserTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FNAME + " TEXT,"
                + LNAME + " TEXT,"
                + EMAIL + " TEXT,"
                + PASSWORD + " TEXT,"
                + CONFIRM_PASSWORD + " TEXT"
                + ")";
    }

    // Query to create visitplace table
    private String createVisitPlaceTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_VISIT_PLACE + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PLACE_NAME + " TEXT,"
                + DESCRIPTION + " TEXT,"
                + IMAGE + " BLOB,"
                + LATITUDE + " REAL,"
                + LONGITUDE + " REAL"
                + ")";
    }

    public Cursor getVisitPlaceByName(String placeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {PLACE_NAME, DESCRIPTION, IMAGE, LATITUDE, LONGITUDE};
        String selection = "LOWER(" + PLACE_NAME + ") LIKE ?";
        String[] selectionArgs = {"%" + placeName.toLowerCase() + "%"};
        return db.query(TABLE_VISIT_PLACE, projection, selection, selectionArgs, null, null, null);
    }


    public Cursor searchPlacesByName(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {PLACE_NAME, IMAGE}; // Assuming PLACE_NAME and IMAGE are column names in your table
        String selection = PLACE_NAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"};
        return db.query(TABLE_VISIT_PLACE, columns, selection, selectionArgs, null, null, null);
    }




}
