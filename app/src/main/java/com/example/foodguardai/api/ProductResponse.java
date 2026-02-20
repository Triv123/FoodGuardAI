package com.example.foodguardai.api;


import com.google.gson.annotations.SerializedName;

public class ProductResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("product")
    private ProductData product;

    public int getStatus() { return status; }
    public ProductData getProduct() { return product; }

    public static class ProductData {
        @SerializedName("product_name")
        private String productName;

        @SerializedName("brands")
        private String brands;

        @SerializedName("categories")
        private String categories;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("nutriments")
        private Nutriments nutriments;

        @SerializedName("ingredients_text")
        private String ingredients;

        // Getters
        public String getProductName() { return productName; }
        public String getBrands() { return brands; }
        public String getCategories() { return categories; }
        public String getImageUrl() { return imageUrl; }
        public Nutriments getNutriments() { return nutriments; }
        public String getIngredients() { return ingredients; }
    }

    public static class Nutriments {
        @SerializedName("energy-kcal_100g")
        private double calories;

        @SerializedName("proteins_100g")
        private double proteins;

        @SerializedName("carbohydrates_100g")
        private double carbohydrates;

        @SerializedName("sugars_100g")
        private double sugars;

        @SerializedName("fat_100g")
        private double fat;

        @SerializedName("saturated-fat_100g")
        private double saturatedFat;

        @SerializedName("fiber_100g")
        private double fiber;

        @SerializedName("sodium_100g")
        private double sodium;

        // Getters
        public double getCalories() { return calories; }
        public double getProteins() { return proteins; }
        public double getCarbohydrates() { return carbohydrates; }
        public double getSugars() { return sugars; }
        public double getFat() { return fat; }
        public double getSaturatedFat() { return saturatedFat; }
        public double getFiber() { return fiber; }
        public double getSodium() { return sodium; }
    }
}