package com.example.nordiccal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.List;

public class LogMealDialogFragment extends DialogFragment {

    private List<FoodSuggestion> suggestions;
    private FoodSuggestionAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_meal_modal, container, false);

        initializeDummySuggestions();
        setupCancelButton(view);
        setupFoodSearch(view);
        setupAddButton(view);

        return view;
    }

    @Override
    public int getTheme() {
        return R.style.DialogTheme;
    }

    private void initializeDummySuggestions() {
        suggestions = Arrays.asList(
                new FoodSuggestion("Raw Banana", 89, 1.1, 23, 0.3, 2.6),
                new FoodSuggestion("Grilled Chicken Breast", 165, 31, 0, 3.6, 0),
                new FoodSuggestion("Oatmeal", 150, 5, 27, 2.5, 4)
        );
    }

    private void setupCancelButton(View view) {
        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void setupFoodSearch(View view) {
        AutoCompleteTextView foodSearchInput = view.findViewById(R.id.foodSearchInput);

        adapter = new FoodSuggestionAdapter(requireContext(), suggestions);
        foodSearchInput.setAdapter(adapter);

        foodSearchInput.setOnItemClickListener((parent, view1, position, id) -> {
            FoodSuggestion selected = adapter.getItem(position);
            if (selected != null) {
                foodSearchInput.setText(selected.name);
                updateNutrientSummary(selected, view);
            }
        });
    }

    private void setupAddButton(View view) {
        AutoCompleteTextView foodSearchInput = view.findViewById(R.id.foodSearchInput);
        Button addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String selectedFoodName = foodSearchInput.getText().toString().trim();
            if (!selectedFoodName.isEmpty()) {
                FoodDataFetcher.checkAndLogFoodItem(selectedFoodName, () -> {
                    requireActivity().runOnUiThread(() -> {
                        foodSearchInput.setText("");
                        clearNutrientSummary(view);
                        Toast.makeText(requireContext(), "Meal logged successfully!", Toast.LENGTH_SHORT).show();

                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).refreshDashboard();
                        }

                        dismiss();
                    });
                });
            } else {
                Toast.makeText(requireContext(), "Please enter or select a food item.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNutrientSummary(FoodSuggestion food, View view) {
        TextView caloriesText = view.findViewById(R.id.caloriesText);
        TextView proteinText = view.findViewById(R.id.proteinText);
        TextView carbsText = view.findViewById(R.id.carbsText);
        TextView fatText = view.findViewById(R.id.fatText);
        TextView fiberText = view.findViewById(R.id.fiberText);

        caloriesText.setText((int) food.calories + " kcal");
        proteinText.setText((int) food.protein + " g");
        carbsText.setText((int) food.carbs + " g");
        fatText.setText((int) food.fat + " g");
        fiberText.setText((int) food.fiber + " g");
    }

    private void clearNutrientSummary(View view) {
        TextView caloriesText = view.findViewById(R.id.caloriesText);
        TextView proteinText = view.findViewById(R.id.proteinText);
        TextView carbsText = view.findViewById(R.id.carbsText);
        TextView fatText = view.findViewById(R.id.fatText);
        TextView fiberText = view.findViewById(R.id.fiberText);

        caloriesText.setText("0 kcal");
        proteinText.setText("0 g");
        carbsText.setText("0 g");
        fatText.setText("0 g");
        fiberText.setText("0 g");
    }
}
