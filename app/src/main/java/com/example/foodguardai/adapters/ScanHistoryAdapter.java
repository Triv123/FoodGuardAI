package com.example.foodguardai.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodguardai.R;
import com.example.foodguardai.models.ScanHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder> {

    private final List<ScanHistory> scans;

    public ScanHistoryAdapter(List<ScanHistory> scans) {
        this.scans = scans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanHistory scan = scans.get(position);

        holder.tvProductName.setText(scan.getProductName() != null
                ? scan.getProductName() : "Unknown Product");

        holder.tvBrand.setText(scan.getBrand() != null
                ? scan.getBrand() : "Unknown Brand");

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy  h:mm a", Locale.US);
        holder.tvDate.setText(sdf.format(new Date(scan.getTimestamp())));

        // Safety rating with color
        int rating = scan.getSafetyRating();
        holder.tvRating.setText(rating + "/10");

        if (rating >= 8) holder.tvRating.setTextColor(Color.parseColor("#4CAF50"));
        else if (rating >= 6) holder.tvRating.setTextColor(Color.parseColor("#8BC34A"));
        else if (rating >= 4) holder.tvRating.setTextColor(Color.parseColor("#FFC107"));
        else holder.tvRating.setTextColor(Color.parseColor("#F44336"));

        // Scan type badge
        holder.tvScanType.setText("barcode".equals(scan.getScanType()) ? "📊 Barcode" : "📄 Label");
    }

    @Override
    public int getItemCount() {
        return scans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName, tvBrand, tvDate, tvRating, tvScanType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvScanType = itemView.findViewById(R.id.tvScanType);
        }
    }
}
