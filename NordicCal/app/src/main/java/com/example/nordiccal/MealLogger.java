package com.example.nordiccal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MealLogger handles writing and retrieving user meal logs from Firebase Realtime Database.
 * Each meal is stored under a daily path organized by date (yyyy-MM-dd).
 */
public class MealLogger {

    /**
     * Logs a meal to Firebase under the path: /users/{uid}/meals/{yyyy-MM-dd}/{timestamp}
     * Uses the Unix timestamp for sorting, and groups meals by log date.
     *
     * @param meal The LoggedMeal object containing nutritional info and timestamp.
     */
    public static void logMealToFirebase(LoggedMeal meal) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Format date as yyyy-MM-dd for use as subpath
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date(meal.loggedAt));

        // Define Firebase path
        DatabaseReference mealRef = dbRef.child("users")
                .child(uid)
                .child("meals")
                .child(date)
                .child(String.valueOf(meal.loggedAt));

        mealRef.setValue(meal).addOnSuccessListener(aVoid -> {
            Log.d("MealLogger", "Meal logged successfully on " + date);
        }).addOnFailureListener(e -> {
            Log.e("MealLogger", "Failed to log meal", e);
        });
    }

    /**
     * Callback interface for retrieving all meals for a specific date.
     */
    public interface MealCallback {
        void onMealsFetched(List<LoggedMeal> meals);
        void onError(Exception e);
    }

    /**
     * Fetches all meals logged on a given date for the currently authenticated user.
     * Uses Firebase path: /users/{uid}/meals/{yyyy-MM-dd}
     *
     * @param date     A date string formatted as yyyy-MM-dd (e.g., "2025-04-24").
     * @param callback Callback returning a list of LoggedMeal objects or an error.
     */
    public static void getMealsForDate(String date, MealCallback callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uid)
                .child("meals")
                .child(date);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LoggedMeal> meals = new ArrayList<>();
                for (DataSnapshot mealSnap : snapshot.getChildren()) {
                    LoggedMeal meal = mealSnap.getValue(LoggedMeal.class);
                    if (meal != null) {
                        meals.add(meal);
                    }
                }
                callback.onMealsFetched(meals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }
}
