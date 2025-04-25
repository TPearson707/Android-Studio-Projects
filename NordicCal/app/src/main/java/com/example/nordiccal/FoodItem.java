package com.example.nordiccal;

/**
 * Represents a food item with detailed nutritional data.
 * Pulled from either the FoodData Central API or entered manually by users.
 * Values are standardized per 100 grams.
 */
public class FoodItem {

    // --- Basic food identification ---
    /** Name of the food item (e.g., "Banana"). */
    public String name;

    // --- Macronutrients (per 100g) ---
    public double calories;
    public double protein;
    public double carbs;
    public double fat;
    public double fiber;

    // --- Micronutrients (per 100g) ---
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

    /** Source of the data: "FoodDataCentral" or "User". */
    public String source;

    /**
     * Default constructor required for Firebase deserialization.
     */
    public FoodItem() {}

    /**
     * Constructs a full FoodItem object with both macro and micronutrient data.
     *
     * @param name          Name of the food (e.g., "Banana").
     * @param calories      Calories per 100g.
     * @param protein       Protein in grams per 100g.
     * @param carbs         Carbohydrates in grams per 100g.
     * @param fat           Fat in grams per 100g.
     * @param fiber         Dietary fiber in grams per 100g.
     * @param sodium        Sodium in mg per 100g.
     * @param potassium     Potassium in mg per 100g.
     * @param calcium       Calcium in mg per 100g.
     * @param iron          Iron in mg per 100g.
     * @param magnesium     Magnesium in mg per 100g.
     * @param vitaminC      Vitamin C in mg per 100g.
     * @param vitaminA      Vitamin A in mcg RAE per 100g.
     * @param vitaminD      Vitamin D in mcg per 100g.
     * @param zinc          Zinc in mg per 100g.
     * @param cholesterol   Cholesterol in mg per 100g.
     * @param totalSugars   Total sugars in grams per 100g.
     * @param addedSugars   Added sugars in grams per 100g.
     * @param source        Data origin ("FoodDataCentral" or "User").
     */
    public FoodItem(String name, double calories, double protein, double carbs, double fat, double fiber,
                    double sodium, double potassium, double calcium, double iron, double magnesium,
                    double vitaminC, double vitaminA, double vitaminD, double zinc,
                    double cholesterol, double totalSugars, double addedSugars, String source) {

        this.name = name;
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
        this.source = source;
    }
}
