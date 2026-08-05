package com.printxpress.app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.printxpress.app.database.entities.User;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    User findByPhone(String phone);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User findById(long id);

    @Update
    void update(User user);
}
