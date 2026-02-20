package com.example.foodguardai;


import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvAbout = findViewById(R.id.tvAboutContent);
        tvAbout.setText(getAboutText());
    }

    private String getAboutText() {
        return "Food Label Scanner\n\n" +
                "Know What You Eat. Stay Healthy.\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "📱 HOW TO USE:\n\n" +
                "1. Scan product barcode\n" +
                "2. View nutritional information\n" +
                "3. Check safety rating (1-10)\n" +
                "4. Identify banned products\n" +
                "5. Ask AI for health advice\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "🎯 UNDERSTANDING RATINGS:\n\n" +
                "9-10: Excellent - Very Safe\n" +
                "7-8: Good - Generally Safe\n" +
                "5-6: Moderate - Caution\n" +
                "3-4: Poor - Limit Use\n" +
                "1-2: Very Poor - Avoid\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "⚠️ RED FLAGS:\n\n" +
                "• High Sugar (>15g/100g)\n" +
                "• High Sodium (>500mg/100g)\n" +
                "• High Saturated Fat (>5g/100g)\n" +
                "• Banned ingredients\n" +
                "• Artificial additives\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "✅ GOOD SIGNS:\n\n" +
                "• High Protein (>10g/100g)\n" +
                "• High Fiber (>5g/100g)\n" +
                "• Low Sugar (<5g/100g)\n" +
                "• Low Sodium (<120mg/100g)\n" +
                "• Natural ingredients\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "📚 DATA SOURCES:\n\n" +
                "• Open Food Facts Database\n" +
                "• FSSAI Guidelines\n" +
                "• WHO Nutrition Standards\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "💡 TIPS:\n\n" +
                "• Always read complete labels\n" +
                "• Check serving sizes\n" +
                "• Look for certifications\n" +
                "• Prefer whole foods\n" +
                "• Watch portion sizes\n\n" +
                "━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Version 1.0.0\n" +
                "Made with ❤️ for health-conscious consumers";
    }
}