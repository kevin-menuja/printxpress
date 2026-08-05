package com.printxpress.app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.printxpress.app.database.entities.Notification;
import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    long insert(Notification notification);

    @Insert
    void insertAll(List<Notification> notifications);

    @Query("SELECT * FROM notifications WHERE userId = :id ORDER BY createdAt DESC")
    List<Notification> getByUserId(long id);

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :id")
    void markAllRead(long id);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    void markAsRead(long id);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :id")
    int count(long id);
}
