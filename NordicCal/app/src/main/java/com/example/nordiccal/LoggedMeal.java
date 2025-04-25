package com.example.nordiccal;

/**
 * Represents a meal logged by a user, including nutrient data scaled to quantity (unit-based).
 */
public class LoggedMeal {
    public String name;
    public int quantity;           // Number of units (e.g., 1 banana, 2 apples)
    public long loggedAt;          // Unix timestamp

    // Macronutrients (scaled per quantity)
    public double calories;
    public double protein;
    public double carbs;
    public double fat;
    public double fiber;

    // Micronutrients (scaled per quantity)
    public double sodium;
    public double potassium;
    public double calcium;
    public double iron;
    public double magnesium;
    public double vitaminC;
    public double vitaminA;
    public double vitaminD;
    public double zinc;
    public double cholesterol;
    public double totalSugars;
    public double addedSugars;

    // Required default constructor for Firebase
    public LoggedMeal() {}

    /**
     * Constructs a LoggedMeal object with full nutrient tracking, scaled per unit quantity.
     */
    public LoggedMeal(String name, int quantity, long loggedAt,
                      double calories, double protein, double carbs, double fat, double fiber,
                      double sodium, double potassium, double calcium, double iron, double magnesium,
                      double vitaminC, double vitaminA, double vitaminD, double zinc,
                      double cholesterol, double totalSugars, double addedSugars) {

        this.name = name;
        this.quantity = quantity;
        this.loggedAt = loggedAt;

        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;

        this.sodium = sodium;
        this.potassium = potassium;
        this.calcium = calcium;
        this.iron = iron;
        this.magnesium = magnesium;
        this.vitaminC = vitaminC;
        this.vitaminA = vitaminA;
        this.vitaminD = vitaminD;
        this.zinc = zinc;
        this.cholesterol = cholesterol;
        this.totalSugars = totalSugars;
        this.addedSugars = addedSugars;
    }
}
