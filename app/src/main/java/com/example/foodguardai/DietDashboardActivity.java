package com.example.foodguardai;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.example.foodguardai.adapters.DietEntryAdapter;
import com.example.foodguardai.database.FirebaseManager;
import com.example.foodguardai.models.DietEntry;

import java.util.ArrayList;
import java.util.List;

public class DietDashboardActivity extends AppCompatActivity {

    private TextView tvTotalCalories, tvTotalProtein, tvTotalCarbs, tvTotalFats;
    private RecyclerView recyclerViewEntries;
    private CircularProgressIndicator progressBar;
    private DietEntryAdapter adapter;

    private FirebaseManager firebaseManager;
    private List<DietEntry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_dashboard);

        firebaseManager = FirebaseManager.getInstance();
        entries = new ArrayList<>();

        initViews();
        setupRecyclerView();
        loadTodaysEntries();
    }

    private void initViews() {
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvTotalProtein = findViewById(R.id.tvTotalProtein);
        tvTotalCarbs = findViewById(R.id.tvTotalCarbs);
        tvTotalFats = findViewById(R.id.tvTotalFats);
        recyclerViewEntries = findViewById(R.id.recyclerViewEntries);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        adapter = new DietEntryAdapter(entries);
        recyclerViewEntries.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEntries.setAdapter(adapter);
    }

    private void loadTodaysEntries() {
        if (!firebaseManager.isUserLoggedIn()) {
            Toast.makeText(this, "Please login to view diet", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseManager.getTodaysDietEntries(new FirebaseManager.DietEntriesCallback() {
            @Override
            public void onSuccess(List<DietEntry> dietEntries) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    entries.clear();
                    entries.addAll(dietEntries);
                    adapter.notifyDataSetChanged();

                    calculateTotals(dietEntries);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DietDashboardActivity.this,
                            "Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void calculateTotals(List<DietEntry> entries) {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFats = 0;

        for (DietEntry entry : entries) {
            totalCalories += entry.getCalories();
            totalProtein += entry.getProtein();
            totalCarbs += entry.getCarbohydrates();
            totalFats += entry.getFats();
        }

        tvTotalCalories.setText(String.format("%.0f kcal", totalCalories));
        tvTotalProtein.setText(String.format("%.1f g", totalProtein));
        tvTotalCarbs.setText(String.format("%.1f g", totalCarbs));
        tvTotalFats.setText(String.format("%.1f g", totalFats));
    }
}