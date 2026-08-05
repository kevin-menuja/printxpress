package com.printxpress.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "promotions")
public class Promotion {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public String description;
    public String discountText;
    public boolean active;
    public long createdAt;

    public Promotion(String title, String description, String discountText, boolean active, long createdAt) {
        this.title = title;
        this.description = description;
        this.discountText = discountText;
        this.active = active;
        this.createdAt = createdAt;
    }
}
