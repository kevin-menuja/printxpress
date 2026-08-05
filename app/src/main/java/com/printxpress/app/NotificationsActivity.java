package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.printxpress.app.adapters.NotificationAdapter;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DatabaseSeeder;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.OrderProgressor;
import com.printxpress.app.database.entities.Notification;
import com.printxpress.app.database.entities.Order;
import com.printxpress.app.utils.SessionManager;
import java.util.List;

public class NotificationsActivity extends BaseActivity {
    private AppDatabase db;
    private long uid;
    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db = AppDatabase.get(this);
        uid = new SessionManager(this).getUserId();
        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(true);

        load();

        findViewById(R.id.readAll).setOnClickListener(v -> DbExecutor.io().execute(() -> {
            db.notificationDao().markAllRead(uid);
            runOnUiThread(this::load);
        }));

        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.nav_notifications);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_notifications) {
                return true;
            }
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(this, OrdersActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return true;
        });
    }

    private void load() {
        DbExecutor.io().execute(() -> {
            DatabaseSeeder.seedNotifications(db, uid);
            List<Order> orders = db.orderDao().getByUserId(uid);
            OrderProgressor.refreshAll(db, orders);
            List<Notification> data = db.notificationDao().getByUserId(uid);
            runOnUiThread(() -> list.setAdapter(new NotificationAdapter(data)));
        });
    }
}
