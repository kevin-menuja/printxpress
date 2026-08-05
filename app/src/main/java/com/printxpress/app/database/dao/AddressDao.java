package com.printxpress.app.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.printxpress.app.database.entities.Address;
import java.util.List;

@Dao
public interface AddressDao {

    @Insert
    long insert(Address address);

    @Query("SELECT * FROM addresses WHERE userId = :id ORDER BY isDefault DESC, id DESC")
    List<Address> getByUserId(long id);

    @Delete
    void delete(Address address);
}
