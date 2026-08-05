package com.printxpress.app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.printxpress.app.database.entities.Order;
import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long insert(Order order);

    @Query("SELECT * FROM orders WHERE userId = :id ORDER BY orderDate DESC")
    List<Order> getByUserId(long id);

    @Query("SELECT * FROM orders WHERE id = :id")
    Order getById(long id);

    @Query("UPDATE orders SET status = :status, statusUpdatedAt = :updatedAt WHERE id = :id")
    void updateStatusWithTime(long id, String status, long updatedAt);

    @Query("SELECT * FROM orders WHERE userId = :id AND status = :status ORDER BY orderDate DESC")
    List<Order> getByUserIdAndStatus(long id, String status);

    @Query("UPDATE orders SET requiredDate = :date, statusUpdatedAt = :updatedAt WHERE id = :id")
    void reschedule(long id, long date, long updatedAt);
}
