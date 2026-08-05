package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.printxpress.app.adapters.OrderAdapter;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.OrderProgressor;
import com.printxpress.app.database.entities.Order;
import com.printxpress.app.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersActivity extends BaseActivity {
    private AppDatabase db;
    private RecyclerView list;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        db = AppDatabase.get(this);
        userId = new SessionManager(this).getUserId();

        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(true);
        list.setItemViewCacheSize(8);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(220);
        list.setItemAnimator(animator);

        updateSummary();
        showOrdersForTab(0);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showOrdersForTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                list.smoothScrollToPosition(0);
            }
        });

        findViewById(R.id.fab).setOnClickListener(v -> startActivity(new Intent(this, NewOrderActivity.class)));

        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.nav_orders);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_orders) {
                return true;
            }
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return true;
        });
    }

    private void updateSummary() {
        DbExecutor.io().execute(() -> {
            List<Order> all = db.orderDao().getByUserId(userId);
            OrderProgressor.refreshAll(db, all);

            int activeCount = 0;
            int completedCount = 0;
            double totalSpendValue = 0;

            for (Order o : all) {
                if ("DELIVERED".equals(o.status)) {
                    completedCount++;
                } else if (!"CANCELLED".equals(o.status)) {
                    activeCount++;
                }
                if (!"CANCELLED".equals(o.status)) {
                    totalSpendValue += o.totalPrice;
                }
            }

            final int finalActiveCount = activeCount;
            final int finalCompletedCount = completedCount;
            final double finalTotalSpendValue = totalSpendValue;

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.activeCount)).setText(String.valueOf(finalActiveCount));
                ((TextView) findViewById(R.id.completedCount)).setText(String.valueOf(finalCompletedCount));
                
                TextView spendTextView = findViewById(R.id.totalSpend);
                if (finalTotalSpendValue >= 100000) {
                    spendTextView.setText(String.format(Locale.US, "LKR %.1fk", finalTotalSpendValue / 1000));
                } else {
                    spendTextView.setText(String.format(Locale.US, "LKR %,.0f", finalTotalSpendValue));
                }
            });
        });
    }

    private void showOrdersForTab(int tab) {
        DbExecutor.io().execute(() -> {
            List<Order> filtered = new ArrayList<>();
            List<Order> all = db.orderDao().getByUserId(userId);
            OrderProgressor.refreshAll(db, all);

            for (Order o : all) {
                boolean include = (tab == 0 && !"DELIVERED".equals(o.status) && !"CANCELLED".equals(o.status))
                    || (tab == 1 && "DELIVERED".equals(o.status))
                    || (tab == 2 && "CANCELLED".equals(o.status));
                if (include) {
                    filtered.add(o);
                }
            }

            runOnUiThread(() -> {
                list.setAdapter(new OrderAdapter(filtered, o -> 
                    startActivity(new Intent(this, OrderDetailActivity.class).putExtra("id", o.id))
                ));
                
                boolean isEmpty = filtered.isEmpty();
                findViewById(R.id.emptyState).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                list.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (list != null) {
            updateSummary();
            TabLayout tabLayout = findViewById(R.id.tabs);
            showOrdersForTab(tabLayout.getSelectedTabPosition());
        }
    }
}
