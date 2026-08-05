package com.printxpress.app.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "addresses", indices = @Index("userId"))
public class Address {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;
    public String label;
    public String fullAddress;
    public boolean isDefault;

    public Address(long userId, String label, String fullAddress, boolean isDefault) {
        this.userId = userId;
        this.label = label;
        this.fullAddress = fullAddress;
        this.isDefault = isDefault;
    }
}
