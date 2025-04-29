package com.example.nordiccal;

public class FoodSuggestion {
    public String name;
    public double calories;
    public double protein;
    public double carbs;
    public double fat;
    public double fiber;

    public FoodSuggestion(String name, double calories, double protein, double carbs, double fat, double fiber) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " - " + calories + " calories, " + protein + "g protein, " + carbs + "g carbs, " + fat + "g fat + " + fiber + "g fiber";
    }
}

