package com.example.tourismmy.ui.tourist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourismmy.R;
import com.example.tourismmy.model.ItineraryItem;
import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {

    private List<ItineraryItem> itineraryList;

    public ItineraryAdapter(List<ItineraryItem> itineraryList) {
        this.itineraryList = itineraryList;
    }

    public void setItineraryList(List<ItineraryItem> newList) {
        this.itineraryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itinerary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItineraryItem item = itineraryList.get(position);
        holder.tvTime.setText(item.getTime());
        holder.tvActivity.setText(item.getActivity());
        holder.tvLocation.setText(item.getLocation());
    }

    @Override
    public int getItemCount() {
        return itineraryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvActivity, tvLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvActivity = itemView.findViewById(R.id.tvActivity);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}