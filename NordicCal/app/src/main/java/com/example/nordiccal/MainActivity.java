package com.example.nordiccal;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;

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

        // Ensure UI respects system insets (status/nav bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupListeners();

    }

    /** Wires up button listeners (currently just logout) */
    private void setupListeners() {
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> handleLogout());

        Button testButton = findViewById(R.id.testLogButton);
        testButton.setOnClickListener(view -> {
            FoodDataFetcher.checkAndLogFoodItem("raw banana");
        });

    }

    /** Logs the user out and redirects to LoginActivity */
    private void handleLogout() {
        AuthManager.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


}
