package com.example.tourismmy.ui.tourist;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourismmy.R;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    // 1. Updated Data Model to include Price
    public static class Attraction {
        String name, desc, geoUri, price;

        public Attraction(String name, String desc, String price, String geoUri) {
            this.name = name;
            this.desc = desc;
            this.price = price; // Store price
            this.geoUri = geoUri;
        }
    }

    private List<Attraction> list;

    public AttractionAdapter(List<Attraction> list) { this.list = list; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itinerary, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction item = list.get(position);
        holder.tvName.setText(item.name);
        holder.tvDesc.setText(item.desc);
        holder.tvLocation.setText("Tap to view options");

        holder.itemView.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle(item.name)
                    .setMessage("Price: RM " + item.price + "\nWhat would you like to do?") // Show price
                    .setPositiveButton("View Map", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.geoUri));
                        v.getContext().startActivity(intent);
                    })
                    .setNegativeButton("Book Ticket", (dialog, which) -> {
                        Intent intent = new Intent(v.getContext(), PaymentActivity.class);
                        intent.putExtra("SERVICE_NAME", item.name);
                        intent.putExtra("SERVICE_PRICE", item.price); // PASS PRICE
                        v.getContext().startActivity(intent);
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvLocation;
        public ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvActivity);
            tvDesc = v.findViewById(R.id.tvTime);
            tvLocation = v.findViewById(R.id.tvLocation);
        }
    }
}