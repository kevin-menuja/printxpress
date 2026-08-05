package com.printxpress.app.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "saved_designs",
    indices = {
        @Index("userId"),
        @Index(value = {"userId", "fileUri"}, unique = true)
    }
)
public class SavedDesign {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;
    public String name;
    public String fileUri;
    public long createdAt;

    public SavedDesign(long userId, String name, String fileUri, long createdAt) {
        this.userId = userId;
        this.name = name;
        this.fileUri = fileUri;
        this.createdAt = createdAt;
    }
}
