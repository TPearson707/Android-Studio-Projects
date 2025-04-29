package com.example.nordiccal;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Handles fetching and caching food item nutrition data.
 * First checks Firebase Realtime Database for a food entry by name.
 * If missing, fetches nutrition data from the FoodData Central API (Foundation or SR Legacy only).
 * Nutrition data includes macro and micronutrients per 100g.
 */
public class FoodDataFetcher {
    private static final String TAG = "FoodDataFetcher";
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = BuildConfig.FDC_API_KEY;
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/search?dataType=Foundation,SR%20Legacy&query=";

    /**
     * Checks if a food item exists in Firebase under /food_items/{foodName}.
     * If it exists, it logs the meal using unit quantity of 1.
     * If it doesn't exist, it fetches data from FoodData Central,
     * stores the result in /food_items, and logs the meal using the fetched data.
     *
     * @param foodName the name of the food to check and log (e.g., "banana")
     */
    public static void checkAndLogFoodItem(String foodName, @Nullable Runnable onComplete) {
        String normalized = foodName.trim().toLowerCase();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        dbRef.child("food_items").child(normalized).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Already in global DB — log to user meals
                FoodItem item = snapshot.getValue(FoodItem.class);
                long timestamp = System.currentTimeMillis();
                if (item != null) {
                    LoggedMeal meal = new LoggedMeal(item.name, 1, timestamp,
                            item.calories, item.protein, item.carbs, item.fat, item.fiber,
                            item.sodium, item.potassium, item.calcium, item.iron, item.magnesium,
                            item.vitaminC, item.vitaminA, item.vitaminD, item.zinc,
                            item.cholesterol, item.totalSugars, item.addedSugars);
                    MealLogger.logMealToFirebase(meal, onComplete);
                }
            } else {
                // Not in DB — fetch from FoodData Central
                fetchFromFoodDataCentral(normalized, (item) -> {
                    dbRef.child("food_items").child(normalized).setValue(item);
                    long timestamp = System.currentTimeMillis();
                    LoggedMeal meal = new LoggedMeal(item.name, 1, timestamp,
                            item.calories, item.protein, item.carbs, item.fat, item.fiber,
                            item.sodium, item.potassium, item.calcium, item.iron, item.magnesium,
                            item.vitaminC, item.vitaminA, item.vitaminD, item.zinc,
                            item.cholesterol, item.totalSugars, item.addedSugars);
                    MealLogger.logMealToFirebase(meal, onComplete);
                });
            }
        });
    }


    /**
     * Callback interface for returning a parsed FoodItem object.
     */
    public interface FoodCallback {
        void onSuccess(FoodItem item);
    }

    /**
     * Fetches the first matching food item from the FoodData Central API and
     * returns a FoodItem object via callback.
     *
     * @param query the food name to search (e.g., "raw banana")
     * @param callback the callback to invoke with the fetched FoodItem
     */
    private static void fetchFromFoodDataCentral(String query, @NonNull FoodCallback callback) {
        String url = BASE_URL + query + "&api_key=" + API_KEY;

        new Thread(() -> {
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string());
                JSONArray foods = json.getJSONArray("foods");
                if (foods.length() == 0) return;

                JSONObject food = foods.getJSONObject(0);

                double calories = getNutrientValue(food, 1008);
                double protein = getNutrientValue(food, 1003);
                double carbs = getNutrientValue(food, 1005);
                double fat = getNutrientValue(food, 1004);
                double fiber = getNutrientValue(food, 1079);
                double sodium = getNutrientValue(food, 1093);
                double potassium = getNutrientValue(food, 1092);
                double calcium = getNutrientValue(food, 1087);
                double iron = getNutrientValue(food, 1089);
                double magnesium = getNutrientValue(food, 1090);
                double vitaminC = getNutrientValue(food, 1162);
                double vitaminA = getNutrientValue(food, 1106);
                double vitaminD = getNutrientValue(food, 1110);
                double zinc = getNutrientValue(food, 1095);
                double cholesterol = getNutrientValue(food, 1253);
                double totalSugars = getNutrientValue(food, 2000);
                double addedSugars = getNutrientValue(food, 1235);

                FoodItem item = new FoodItem(
                        food.getString("description"),
                        calories, protein, carbs, fat, fiber,
                        sodium, potassium, calcium, iron,
                        magnesium, vitaminC, vitaminA,
                        vitaminD, zinc, cholesterol,
                        totalSugars, addedSugars,
                        "FoodDataCentral"
                );

                callback.onSuccess(item);

            } catch (Exception e) {
                Log.e(TAG, "API fetch failed", e);
            }
        }).start();
    }

    /**
     * Extracts a nutrient value from a food object using the given nutrientId.
     *
     * @param food JSON object representing the food.
     * @param nutrientId Numeric nutrient ID defined by FoodData Central.
     * @return nutrient value as a double, or 0.0 if not found.
     */
    private static double getNutrientValue(JSONObject food, int nutrientId) {
        try {
            JSONArray nutrients = food.getJSONArray("foodNutrients");
            for (int i = 0; i < nutrients.length(); i++) {
                JSONObject n = nutrients.getJSONObject(i);
                if (n.getInt("nutrientId") == nutrientId) {
                    return n.getDouble("value");
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Missing nutrient " + nutrientId);
        }
        return 0.0;
    }
}
