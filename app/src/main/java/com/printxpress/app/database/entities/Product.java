package com.printxpress.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String category;
    public String description;
    public String availableSizes;
    public String availableMaterials;
    public double basePrice;

    public Product(String name, String category, String description, double basePrice,
                   String availableSizes, String availableMaterials) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.basePrice = basePrice;
        this.availableSizes = availableSizes;
        this.availableMaterials = availableMaterials;
    }
}
