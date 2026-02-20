package com.example.foodguardai;


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView tvMessage = findViewById(R.id.tvHistoryMessage);
        tvMessage.setText("Scan History\n\n" +
                "This feature will show your previously scanned products.\n\n" +
                "Coming soon:\n" +
                "• View all scanned products\n" +
                "• Quick access to product details\n" +
                "• Filter by date\n" +
                "• Search history");
    }
}