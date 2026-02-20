package com.example.foodguardai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodguardai.R;
import com.example.foodguardai.models.DietEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DietEntryAdapter extends RecyclerView.Adapter<DietEntryAdapter.ViewHolder> {

    private List<DietEntry> entries;

    public DietEntryAdapter(List<DietEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diet_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DietEntry entry = entries.get(position);

        holder.tvProductName.setText(entry.getProductName());

        // Format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
        String time = timeFormat.format(new Date(entry.getTimestamp()));
        holder.tvTime.setText(time);

        // Display nutrition
        holder.tvCalories.setText(String.format("%.0f kcal", entry.getCalories()));
        holder.tvProtein.setText(String.format("P: %.1fg", entry.getProtein()));
        holder.tvCarbs.setText(String.format("C: %.1fg", entry.getCarbohydrates()));
        holder.tvFats.setText(String.format("F: %.1fg", entry.getFats()));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvTime;
        TextView tvCalories;
        TextView tvProtein;
        TextView tvCarbs;
        TextView tvFats;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvProtein = itemView.findViewById(R.id.tvProtein);
            tvCarbs = itemView.findViewById(R.id.tvCarbs);
            tvFats = itemView.findViewById(R.id.tvFats);
        }
    }
}