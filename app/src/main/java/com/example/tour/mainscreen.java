package com.example.tour;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class mainscreen extends AppCompatActivity {

    private MyAdapter adapter1, adapter2, adapter3, adapter4;
    private List<CardItem> cardItemList1, cardItemList2, cardItemList3, cardItemList4;
    private RecyclerView recyclerView, recyclerView2, recyclerView3, recyclerView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainscreen);


        recyclerView = findViewById(R.id.recycler_view1);
        recyclerView2 = findViewById(R.id.recycler_view2);
        recyclerView3 = findViewById(R.id.recycler_view3);
        recyclerView4 = findViewById(R.id.recycler_view4);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, true);
        recyclerView3.setLayoutManager(layoutManager2);
        recyclerView4.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        cardItemList1 = new ArrayList<>();
        cardItemList2 = new ArrayList<>();
        cardItemList3 = new ArrayList<>();
        cardItemList4 = new ArrayList<>();

        adapter1 = new MyAdapter(this, cardItemList1, R.layout.item_card_view);
        adapter2 = new MyAdapter(this, cardItemList2, R.layout.item_card_view2);
        adapter3 = new MyAdapter(this, cardItemList3, R.layout.item_card_view3);
        adapter4 = new MyAdapter(this, cardItemList4, R.layout.item_card_view4);

        recyclerView.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);
        recyclerView4.setAdapter(adapter4);

        Button btnAdd = findViewById(R.id.btnadd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainscreen.this, Home.class);
                startActivity(intent);
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        LinearLayout layoutSearch = findViewById(R.id.layoutsearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed for dynamic search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types
                performSearch(newText);
                return true;
            }
        });

        Button btnSearchHome = findViewById(R.id.btnsearchhome);
        btnSearchHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchView.isIconified()) {
                    // Set focus to the search view
                    searchView.requestFocus();

                    // Open the search view
                    searchView.setIconified(false);

                    // Show the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    // Clear focus from the search view
                    searchView.clearFocus();

                    // Close the search view
                    searchView.setIconified(true);

                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
            }
        });

        TextView seeAll = findViewById(R.id.textView19);
        TextView see = findViewById(R.id.textViewsee);

        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainscreen.this, gallery.class);
                startActivity(intent);
            }
        });

        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainscreen.this, gallery.class);
                startActivity(intent);
            }
        });

        Button home = findViewById(R.id.btnhome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainscreen.this, mainscreen.class);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ViewGroup.LayoutParams layoutParams = layoutSearch.getLayoutParams();
                if (hasFocus) {
                    // Set the height to WRAP_CONTENT when SearchView gains focus
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    // Set the height to 46dp when SearchView loses focus
                    layoutParams.height = (int) getResources().getDimension(R.dimen.default_layout_height);
                }
                layoutSearch.setLayoutParams(layoutParams);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void performSearch(String query) {
        // Query your database with the entered search query
        // For example:
        DbHelper dbHelper = new DbHelper(this);
        Cursor cursor = dbHelper.searchPlacesByName(query);

        // Create a list to hold the search results
        List<CardItem> searchResults = new ArrayList<>();

        // Iterate through the cursor and add items to the search results list
        if (cursor != null && cursor.moveToFirst()) {
            int placeNameIndex = cursor.getColumnIndex(DbHelper.PLACE_NAME);
            int imageIndex = cursor.getColumnIndex(DbHelper.IMAGE);

            do {
                String name = cursor.getString(placeNameIndex);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                searchResults.add(new CardItem(name, imageBytes));
            } while (cursor.moveToNext());

            cursor.close();
        }
        dbHelper.close();

        // Update the RecyclerView adapter with the search results
        adapter4.updateData(searchResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapters();
        refreshAdapter3Reverse();
    }

    private void refreshAdapters() {
        cardItemList1.clear();
        cardItemList2.clear();
        cardItemList4.clear();

        DbHelper dbHelper = new DbHelper(this);
        Cursor cursor = dbHelper.getAllVisitPlaces();

        if (cursor != null && cursor.moveToFirst()) {
            int placeNameIndex = cursor.getColumnIndex(DbHelper.PLACE_NAME);
            int imageIndex = cursor.getColumnIndex(DbHelper.IMAGE);

            do {
                String name = cursor.getString(placeNameIndex);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                cardItemList1.add(new CardItem(name, imageBytes));
                cardItemList2.add(new CardItem(name, imageBytes));
                cardItemList4.add(new CardItem(name, imageBytes));
            } while (cursor.moveToNext());

            cursor.close();
        }
        dbHelper.close();

        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        adapter4.notifyDataSetChanged();

        startAutoScroll();
    }

    private void refreshAdapter3Reverse() {
        cardItemList3.clear();

        DbHelper dbHelper = new DbHelper(this);
        Cursor cursor = dbHelper.getAllVisitPlaces();

        if (cursor != null && cursor.moveToLast()) {
            int placeNameIndex = cursor.getColumnIndex(DbHelper.PLACE_NAME);
            int imageIndex = cursor.getColumnIndex(DbHelper.IMAGE);

            do {
                String name = cursor.getString(placeNameIndex);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                cardItemList3.add(new CardItem(name, imageBytes));
            } while (cursor.moveToPrevious());

            cursor.close();
        }
        dbHelper.close();

        adapter3.notifyDataSetChanged();
    }

    private void startAutoScroll() {
        final int speedScroll = 5000;
        final Handler handler = new Handler();
        final int itemCount = adapter1.getItemCount();

        final Runnable runnable = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                if (count < itemCount) {
                    recyclerView.smoothScrollToPosition(++count);
                } else {
                    count = 0;
                    recyclerView.scrollToPosition(0); // Scroll back to the beginning
                }
                handler.postDelayed(this, speedScroll);
            }
        };
        handler.postDelayed(runnable, speedScroll);
    }

    public static class CardItem {
        String name;
        byte[] imageBytes;

        CardItem(String name, byte[] imageBytes) {
            this.name = name;
            this.imageBytes = imageBytes;
        }
    }
}
