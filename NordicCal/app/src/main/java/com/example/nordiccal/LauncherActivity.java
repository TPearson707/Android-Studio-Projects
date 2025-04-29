package com.example.nordiccal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_NordicCal_Splash); // <-- Switch back to normal app theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity); // using your launcher_activity.xml

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if (currentUser != null) {
                    // User is already signed in, go to Dashboard
                    intent = new Intent(LauncherActivity.this, MainActivity.class);
                } else {
                    // No user is signed in, go to Login
                    intent = new Intent(LauncherActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 1000); // 2000 is the delayed time in milliseconds.

    }
}

