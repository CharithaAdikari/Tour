package com.example.tour;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final List<mainscreen.CardItem> cardItems;
    private final Context context;
    private final int layoutId;

    public MyAdapter(Context context, List<mainscreen.CardItem> cardItems, int layoutId) {
        this.context = context;
        this.cardItems = cardItems;
        this.layoutId = layoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mainscreen.CardItem item = cardItems.get(position);

        holder.textViewName.setText(item.name);
        Glide.with(holder.itemView)
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

    public void updateData(List<mainscreen.CardItem> newData) {
        cardItems.clear();
        cardItems.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewName;
        final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
