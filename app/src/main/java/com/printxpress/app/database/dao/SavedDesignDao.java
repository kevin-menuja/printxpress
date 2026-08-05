package com.printxpress.app.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.printxpress.app.database.entities.SavedDesign;
import java.util.List;

@Dao
public interface SavedDesignDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SavedDesign design);

    @Query("SELECT * FROM saved_designs WHERE userId = :userId ORDER BY createdAt DESC")
    List<SavedDesign> getByUserId(long userId);

    @Delete
    void delete(SavedDesign design);
}
