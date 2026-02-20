package com.example.foodguardai.models;

public class Product {
    private String barcode;
    private String productName;
    private String brand;
    private String category;
    private String imageUrl;
    private boolean isBanned;
    private String banReason;
    private int safetyRating; // 1-10
    private NutritionalInfo nutritionalInfo;
    private String fssaiLicense;
    private String manufacturerDetails;

    // Constructor
    public Product() {}

    // Getters and Setters
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    public String getBanReason() { return banReason; }
    public void setBanReason(String banReason) { this.banReason = banReason; }

    public int getSafetyRating() { return safetyRating; }
    public void setSafetyRating(int safetyRating) { this.safetyRating = safetyRating; }

    public NutritionalInfo getNutritionalInfo() { return nutritionalInfo; }
    public void setNutritionalInfo(NutritionalInfo nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public String getFssaiLicense() { return fssaiLicense; }
    public void setFssaiLicense(String fssaiLicense) { this.fssaiLicense = fssaiLicense; }

    public String getManufacturerDetails() { return manufacturerDetails; }
    public void setManufacturerDetails(String manufacturerDetails) {
        this.manufacturerDetails = manufacturerDetails;
    }
}