package com.printxpress.app.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.printxpress.app.database.dao.AddressDao;
import com.printxpress.app.database.dao.NotificationDao;
import com.printxpress.app.database.dao.OrderDao;
import com.printxpress.app.database.dao.ProductDao;
import com.printxpress.app.database.dao.PromotionDao;
import com.printxpress.app.database.dao.SavedDesignDao;
import com.printxpress.app.database.dao.UserDao;
import com.printxpress.app.database.entities.Address;
import com.printxpress.app.database.entities.Notification;
import com.printxpress.app.database.entities.Order;
import com.printxpress.app.database.entities.Product;
import com.printxpress.app.database.entities.Promotion;
import com.printxpress.app.database.entities.SavedDesign;
import com.printxpress.app.database.entities.User;

@Database(
    entities = {
        User.class, 
        Product.class, 
        Order.class, 
        Notification.class, 
        Address.class, 
        SavedDesign.class, 
        Promotion.class
    }, 
    version = 3, 
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract NotificationDao notificationDao();
    public abstract AddressDao addressDao();
    public abstract SavedDesignDao savedDesignDao();
    public abstract PromotionDao promotionDao();

    private static final Migration M1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                "CREATE TABLE orders_new (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "userId INTEGER NOT NULL, " +
                "productId INTEGER NOT NULL, " +
                "productName TEXT, " +
                "category TEXT, " +
                "quantity INTEGER NOT NULL, " +
                "material TEXT, " +
                "size TEXT, " +
                "customText TEXT, " +
                "deliveryType TEXT, " +
                "deliveryAddress TEXT, " +
                "paymentMethod TEXT, " +
                "requiredDate INTEGER NOT NULL, " +
                "totalPrice REAL NOT NULL, " +
                "status TEXT, " +
                "orderDate INTEGER NOT NULL, " +
                "orderId TEXT)"
            );
            db.execSQL(
                "INSERT INTO orders_new (" +
                "id, userId, productId, productName, category, quantity, material, size, customText, " +
                "deliveryType, deliveryAddress, paymentMethod, requiredDate, totalPrice, status, orderDate, orderId) " +
                "SELECT id, userId, productId, productName, category, quantity, material, size, customText, " +
                "deliveryType, deliveryAddress, 'CASH', requiredDate, totalPrice, status, orderDate, orderId " +
                "FROM orders"
            );
            db.execSQL("DROP TABLE orders");
            db.execSQL("ALTER TABLE orders_new RENAME TO orders");
            db.execSQL("CREATE INDEX index_orders_userId ON orders(userId)");
        }
    };

    private static final Migration M2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE orders ADD COLUMN designUri TEXT");
            db.execSQL("ALTER TABLE orders ADD COLUMN statusUpdatedAt INTEGER NOT NULL DEFAULT 0");
            db.execSQL("UPDATE orders SET statusUpdatedAt = orderDate WHERE statusUpdatedAt = 0");
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS saved_designs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "userId INTEGER NOT NULL, " +
                "name TEXT, " +
                "fileUri TEXT, " +
                "createdAt INTEGER NOT NULL)"
            );
            db.execSQL("CREATE INDEX IF NOT EXISTS index_saved_designs_userId ON saved_designs(userId)");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_saved_designs_userId_fileUri ON saved_designs(userId, fileUri)");
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS promotions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "title TEXT, " +
                "description TEXT, " +
                "discountText TEXT, " +
                "active INTEGER NOT NULL, " +
                "createdAt INTEGER NOT NULL)"
            );
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_userId ON notifications(userId)");
            db.execSQL("CREATE INDEX IF NOT EXISTS index_addresses_userId ON addresses(userId)");
        }
    };

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(), 
                        AppDatabase.class, 
                        "printxpress.db"
                    )
                    .addMigrations(M1_2, M2_3)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
