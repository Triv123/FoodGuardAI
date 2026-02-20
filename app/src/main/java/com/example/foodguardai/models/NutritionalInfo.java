package com.example.foodguardai.models;


public class NutritionalInfo {
    private double servingSize;
    private String servingUnit;
    private double calories;
    private double protein;
    private double carbohydrates;
    private double sugar;
    private double fat;
    private double saturatedFat;
    private double fiber;
    private double sodium;
    private String proteinLevel; // "Low", "Adequate", "High"
    private String carbLevel;
    private String sugarLevel;
    private String sodiumLevel;

    // Constructor
    public NutritionalInfo() {}

    // Getters and Setters
    public double getServingSize() { return servingSize; }
    public void setServingSize(double servingSize) { this.servingSize = servingSize; }

    public String getServingUnit() { return servingUnit; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }

    public double getSugar() { return sugar; }
    public void setSugar(double sugar) { this.sugar = sugar; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getSaturatedFat() { return saturatedFat; }
    public void setSaturatedFat(double saturatedFat) { this.saturatedFat = saturatedFat; }

    public double getFiber() { return fiber; }
    public void setFiber(double fiber) { this.fiber = fiber; }

    public double getSodium() { return sodium; }
    public void setSodium(double sodium) { this.sodium = sodium; }

    public String getProteinLevel() { return proteinLevel; }
    public void setProteinLevel(String proteinLevel) { this.proteinLevel = proteinLevel; }

    public String getCarbLevel() { return carbLevel; }
    public void setCarbLevel(String carbLevel) { this.carbLevel = carbLevel; }

    public String getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(String sugarLevel) { this.sugarLevel = sugarLevel; }

    public String getSodiumLevel() { return sodiumLevel; }
    public void setSodiumLevel(String sodiumLevel) { this.sodiumLevel = sodiumLevel; }
}