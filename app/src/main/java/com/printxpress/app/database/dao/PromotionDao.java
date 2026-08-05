package com.printxpress.app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.printxpress.app.database.entities.Promotion;
import java.util.List;

@Dao
public interface PromotionDao {

    @Insert
    void insertAll(List<Promotion> promotions);

    @Query("SELECT * FROM promotions WHERE active = 1 ORDER BY createdAt DESC")
    List<Promotion> getActive();

    @Query("SELECT COUNT(*) FROM promotions")
    int count();
}
