package com.example.nordiccal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity serves as the authenticated landing screen (dashboard).
 * Displays user's nutrition data and handles logout.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

//        // Set fallback status & nav bar color early to avoid flicker (for when bars show briefly)
//        Window window = getWindow();
//
//        // clear FLAG_TRANSLUCENT_STATUS flag:
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.navy_blue));
//        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.navy_blue));

        // Handle padding for system bars (e.g., keyboard, insets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Apply immersive mode to hide status and navigation bars
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//        );

        setupListeners();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_IMMERSIVE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            );
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndDisplayTodayMeals();
    }

    /** Sets up click listeners */
    private void setupListeners() {
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    /** Called from XML button to open log meal modal */
    public void onClickOpenLogMeal(View view) {
        LogMealDialogFragment dialog = new LogMealDialogFragment();
        dialog.show(getSupportFragmentManager(), "LogMealDialog");
    }

    /** Logs the user out and redirects to LoginActivity */
    private void handleLogout() {
        AuthManager.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /** Fetches today's meals and updates UI */
    private void fetchAndDisplayTodayMeals() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        MealLogger.getMealsForDate(today, new MealLogger.MealCallback() {
            @Override
            public void onMealsFetched(List<LoggedMeal> meals) {
                displayMeals(meals);
                updateMicroSummary(meals);
                updateMacroSummary(meals);
            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Failed to fetch meals", e);
            }
        });
    }

    /** Displays meal items dynamically inside the meal list */
    private void displayMeals(List<LoggedMeal> meals) {
        LinearLayout mealContainer = findViewById(R.id.mealItemsContainer);
        mealContainer.removeAllViews(); // Clear previous meals

        for (LoggedMeal meal : meals) {
            // Create horizontal container for one meal
            LinearLayout mealLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 10); // 10dp bottom margin
            mealLayout.setLayoutParams(layoutParams);
            mealLayout.setOrientation(LinearLayout.HORIZONTAL);
            mealLayout.setBackgroundResource(R.drawable.logged_meal_background);
            mealLayout.setPadding(8, 8, 8, 8);

            // Food Name TextView
            TextView nameTextView = new TextView(this);
            nameTextView.setText(meal.name);
            nameTextView.setTextSize(14f);
            nameTextView.setTextColor(getResources().getColor(R.color.black));
            nameTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // Calories TextView
            TextView caloriesTextView = new TextView(this);
            caloriesTextView.setText((int) meal.calories + " kcal");
            caloriesTextView.setTextSize(14f);
            caloriesTextView.setTextColor(getResources().getColor(R.color.black));
            caloriesTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // Time TextView
            TextView timeTextView = new TextView(this);
            String time = formatLoggedTime(meal.loggedAt);
            timeTextView.setText(time);
            timeTextView.setTextSize(14f);
            timeTextView.setTextColor(getResources().getColor(R.color.black));
            timeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // Add the TextViews into the meal row
            mealLayout.addView(nameTextView);
            mealLayout.addView(caloriesTextView);
            mealLayout.addView(timeTextView);

            // Add meal row into ScrollView container
            mealContainer.addView(mealLayout);
        }
    }


    /** Helper method to format timestamp into readable time */
    private String formatLoggedTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // e.g., 02:15 PM
        return sdf.format(date);
    }

    /** Updates total macros summary bar */
    private void updateMacroSummary(List<LoggedMeal> meals) {
        double totalProtein = 0, totalCarbs = 0, totalFats = 0, totalFiber = 0;

        for (LoggedMeal meal : meals) {
            totalProtein += meal.protein;
            totalCarbs += meal.carbs;
            totalFats += meal.fat;
            totalFiber += meal.fiber;
        }

        TextView proteinMacro = findViewById(R.id.proteinMacro);
        TextView carbsMacro = findViewById(R.id.carbsMacro);
        TextView fatsMacro = findViewById(R.id.fatsMacro);
        TextView fiberMacro = findViewById(R.id.fiberMacro);

        proteinMacro.setText("Protein\n" + (int) totalProtein + "g");
        carbsMacro.setText("Carbs\n" + (int) totalCarbs + "g");
        fatsMacro.setText("Fats\n" + (int) totalFats + "g");
        fiberMacro.setText("Fiber\n" + (int) totalFiber + "g");
    }

    private void updateMicroSummary(List<LoggedMeal> meals) {
        double totalSodium = 0, totalPotassium = 0, totalCalcium = 0, totalIron = 0;
        double totalMagnesium = 0, totalVitaminC = 0, totalVitaminA = 0, totalVitaminD = 0;
        double totalZinc = 0, totalCholesterol = 0, totalTotalSugars = 0, totalAddedSugars = 0;

        for (LoggedMeal meal : meals) {
            totalSodium += meal.sodium;
            totalPotassium += meal.potassium;
            totalCalcium += meal.calcium;
            totalIron += meal.iron;
            totalMagnesium += meal.magnesium;
            totalVitaminC += meal.vitaminC;
            totalVitaminA += meal.vitaminA;
            totalVitaminD += meal.vitaminD;
            totalZinc += meal.zinc;
            totalCholesterol += meal.cholesterol;
            totalTotalSugars += meal.totalSugars;
            totalAddedSugars += meal.addedSugars;
        }

        displayMicros(new String[][]{
                {"Sodium", totalSodium + "mg"},
                {"Potassium", totalPotassium + "mg"},
                {"Calcium", totalCalcium + "mg"},
                {"Iron", totalIron + "mg"},
                {"Magnesium", totalMagnesium + "mg"},
                {"Vitamin C", totalVitaminC + "mg"},
                {"Vitamin A", totalVitaminA + "mcg"},
                {"Vitamin D", totalVitaminD + "mcg"},
                {"Zinc", totalZinc + "mg"},
                {"Cholesterol", totalCholesterol + "mg"},
                {"Total Sugars", totalTotalSugars + "g"},
                {"Added Sugars", totalAddedSugars + "g"}
        });
    }

    private String formatMicroLabel(String label, String rawValueWithUnit) {
        // Extract number and unit
        String numericPart = rawValueWithUnit.replaceAll("[^\\d.]+", "");
        String unitPart = rawValueWithUnit.replaceAll("[\\d.]", "");

        // Safely parse and format number
        double value = 0;
        try {
            value = Double.parseDouble(numericPart);
        } catch (NumberFormatException e) {
            Log.w("MicroFormat", "Could not parse value: " + rawValueWithUnit);
        }

        String formattedValue = formatMicro(value);

        return label + "\n" + formattedValue + unitPart;
    }

    // Helper function to format double to string
    private String formatMicro(double value) {
        if (value == (long) value) {
            return String.format(Locale.getDefault(), "%d", (long) value);
        } else {
            return String.format(Locale.getDefault(), "%.2f", value);
        }
    }


    private void displayMicros(String[][] micros) {
        LinearLayout container = findViewById(R.id.microCardContainer);
        container.removeAllViews();

        float scale = getResources().getDisplayMetrics().density;
        int cardHeightPx = (int) (90 * scale); // 100dp height

        for (int i = 0; i < micros.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setPadding(0, 8, 0, 8);

            for (int j = i; j < i + 2 && j < micros.length; j++) {
                RelativeLayout card = new RelativeLayout(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        0,
                        cardHeightPx,
                        1f
                );
                cardParams.setMargins(8, 0, 8, 0);
                card.setLayoutParams(cardParams);

                int bgRes = (j % 2 == 0) ? R.drawable.rounded_green : R.drawable.rounded_pink;
                card.setBackgroundResource(bgRes);

                TextView label = new TextView(this);
                label.setText(formatMicroLabel(micros[j][0], micros[j][1]));
                int tColor = (j % 2 == 0) ? R.color.sage_green : R.color.dusty_rose;
                label.setTextColor(ContextCompat.getColor(this, tColor));
                label.setTextSize(16f);
                label.setGravity(Gravity.CENTER); // still useful for internal alignment
                label.setPadding(16, 16, 16, 16);

                // Center label inside card
                RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                labelParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                label.setLayoutParams(labelParams);

                card.addView(label);

                row.addView(card);
            }

            container.addView(row);
        }

    }

    public void refreshDashboard() {
        fetchAndDisplayTodayMeals();
    }

}
