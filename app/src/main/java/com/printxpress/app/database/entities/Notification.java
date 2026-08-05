package com.printxpress.app.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications", indices = @Index("userId"))
public class Notification {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;
    public String title;
    public String message;
    public String type;
    public boolean isRead;
    public long createdAt;

    public Notification(long userId, String title, String message, String type, boolean isRead, long createdAt) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}
