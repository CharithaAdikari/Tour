package com.example.tour;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class gallery extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<CardItem> cardItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recyclerView3);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);


        cardItemList = new ArrayList<>();
        adapter = new MyAdapter(this, cardItemList);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapters();
    }

    private void refreshAdapters() {
        cardItemList.clear();

        DbHelper dbHelper = new DbHelper(this);
        Cursor cursor = dbHelper.getAllVisitPlaces();

        if (cursor != null && cursor.moveToFirst()) {
            int placeNameIndex = cursor.getColumnIndex(DbHelper.PLACE_NAME);
            int imageIndex = cursor.getColumnIndex(DbHelper.IMAGE);

            do {
                String name = cursor.getString(placeNameIndex);
                byte[] imageBytes = cursor.getBlob(imageIndex);
                cardItemList.add(new CardItem(name, imageBytes));
            } while (cursor.moveToNext());
            cursor.close();
        }
        dbHelper.close();

        adapter.notifyDataSetChanged();
    }

    private static class CardItem {
        String name;
        byte[] imageBytes;

        CardItem(String name, byte[] imageBytes) {
            this.name = name;
            this.imageBytes = imageBytes;
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private final List<CardItem> cardItems;
        private final Context context;

        MyAdapter(Context context, List<CardItem> cardItems) {
            this.context = context;
            this.cardItems = cardItems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_view3, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CardItem item = cardItems.get(position);

            holder.textViewName.setText(item.name);

            Glide.with(context)
                    .load(item.imageBytes)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Details.class);
                intent.putExtra("placeName", item.name);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return cardItems.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView textViewName;
            final ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
}
