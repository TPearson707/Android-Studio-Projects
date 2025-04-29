package com.example.nordiccal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class FoodSuggestionAdapter extends ArrayAdapter<FoodSuggestion> {
    public FoodSuggestionAdapter(Context context, List<FoodSuggestion> suggestions) {
        super(context, 0, suggestions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        FoodSuggestion food = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_suggestion_item, parent, false);
        }

        TextView foodNameText = convertView.findViewById(R.id.foodName);
        TextView foodInfoText = convertView.findViewById(R.id.foodInfo);

        String foodName = " ";

        if (food != null) {
            foodName = food.name;
        }

        String foodInfo = String.format("%.0f kcal | P: %.0fg C: %.0fg F: %.0fg, F %.0fg", food.calories, food.protein, food.carbs, food.fat, food.fiber);

        foodNameText.setText(foodName);
        foodInfoText.setText(foodInfo);

        return convertView;
    }
}

