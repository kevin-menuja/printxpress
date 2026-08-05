package com.printxpress.app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.printxpress.app.database.entities.Product;
import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    long insert(Product product);

    @Insert
    void insertAll(List<Product> products);

    @Query("SELECT * FROM products ORDER BY category, name")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id")
    List<Product> getByCategory(String category);

    @Query("SELECT * FROM products WHERE id = :id")
    Product getById(long id);

    @Query("SELECT * FROM products WHERE name = :name LIMIT 1")
    Product getByName(String name);

    @Query("SELECT COUNT(*) FROM products")
    int count();
}
