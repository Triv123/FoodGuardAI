//package com.example.foodguardai;
//
//
//import android.os.Bundle;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class HistoryActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//
//        TextView tvMessage = findViewById(R.id.tvHistoryMessage);
//        tvMessage.setText("Scan History\n\n" +
//                "This feature will show your previously scanned products.\n\n" +
//                "Coming soon:\n" +
//                "• View all scanned products\n" +
//                "• Quick access to product details\n" +
//                "• Filter by date\n" +
//                "• Search history");
//    }
//}

package com.example.foodguardai;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodguardai.adapters.ScanHistoryAdapter;
import com.example.foodguardai.database.FirebaseManager;
import com.example.foodguardai.models.ScanHistory;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CircularProgressIndicator progressBar;
    private TextView tvEmpty;
    private ScanHistoryAdapter adapter;
    private List<ScanHistory> historyList;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        firebaseManager = FirebaseManager.getInstance();
        historyList = new ArrayList<>();

        initViews();
        setupRecyclerView();
        loadHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewHistory);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new ScanHistoryAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadHistory() {
        if (!firebaseManager.isUserLoggedIn()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Please login to view your scan history.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getScanHistory(50, new FirebaseManager.ScanHistoryCallback() {
            @Override
            public void onSuccess(List<ScanHistory> scans) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    historyList.clear();
                    historyList.addAll(scans);
                    adapter.notifyDataSetChanged();

                    if (scans.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("No scan history yet.\nScan a product to get started!");
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HistoryActivity.this,
                            "Error loading history: " + error, Toast.LENGTH_LONG).show();
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Failed to load history.\nPlease try again.");
                });
            }
        });
    }
}