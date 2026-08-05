package com.printxpress.app.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "users",
    indices = {
        @Index(value = "email", unique = true),
        @Index(value = "phone", unique = true)
    }
)
public class User {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String fullName;
    public String email;
    public String phone;
    public String passwordHash;
    public long createdAt;

    public User(String fullName, String email, String phone, String passwordHash, long createdAt) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }
}
