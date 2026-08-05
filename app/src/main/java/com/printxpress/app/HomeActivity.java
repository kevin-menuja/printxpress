package com.printxpress.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.printxpress.app.adapters.BannerAdapter;
import com.printxpress.app.adapters.PromotionAdapter;
import com.printxpress.app.adapters.ServiceCategoryAdapter;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DatabaseSeeder;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.entities.Promotion;
import com.printxpress.app.database.entities.User;
import com.printxpress.app.models.Banner;
import com.printxpress.app.models.ServiceCategory;
import com.printxpress.app.utils.SessionManager;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private ViewPager2 banners;

    private final Runnable advanceBanner = new Runnable() {
        @Override
        public void run() {
            if (banners != null && banners.getAdapter() != null) {
                int count = banners.getAdapter().getItemCount();
                banners.setCurrentItem((banners.getCurrentItem() + 1) % count, true);
                carouselHandler.postDelayed(this, 4200);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        AppDatabase db = AppDatabase.get(this);
        TextView avatar = findViewById(R.id.avatar);
        avatar.setText("PX");
        avatar.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        DbExecutor.io().execute(() -> {
            DatabaseSeeder.seedProducts(db);
            DatabaseSeeder.seedPromotions(db);
            User user = db.userDao().findById(session.getUserId());
            List<Promotion> offers = db.promotionDao().getActive();

            runOnUiThread(() -> {
                String name = (user == null || user.fullName == null || user.fullName.trim().isEmpty())
                    ? "Customer"
                    : user.fullName.trim();

                ((TextView) findViewById(R.id.greeting)).setText("Hello, " + name.split("\\s+")[0] + "!");

                StringBuilder initials = new StringBuilder();
                for (String word : name.split("\\s+")) {
                    if (!word.isEmpty()) {
                        initials.append(word.charAt(0));
                    }
                }
                avatar.setText(initials.length() == 0
                    ? "PX"
                    : initials.substring(0, Math.min(2, initials.length())).toUpperCase()
                );

                RecyclerView promotions = findViewById(R.id.promotions);
                promotions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                promotions.setAdapter(new PromotionAdapter(offers));
            });
        });

        banners = findViewById(R.id.banners);
        banners.setAdapter(new BannerAdapter(Arrays.asList(
            new Banner("20% Festive Offer", "Make every celebration memorable", Color.rgb(26, 35, 126)),
            new Banner("Express Printing", "Selected products in 24 hours", Color.rgb(255, 111, 0)),
            new Banner("Island-wide Delivery", "We deliver to all 25 districts", Color.rgb(0, 105, 92))
        )));
        banners.setOffscreenPageLimit(2);
        banners.setPageTransformer((page, position) -> {
            float p = Math.abs(position);
            page.setAlpha(0.75f + (1f - p) * 0.25f);
            page.setScaleY(0.92f + (1f - p) * 0.08f);
            page.setTranslationX(-position * 24f);
        });

        RecyclerView services = findViewById(R.id.services);
        services.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        services.setHasFixedSize(true);
        services.setItemViewCacheSize(7);

        List<ServiceCategory> categories = Arrays.asList(
            new ServiceCategory("Business Cards", R.drawable.service_business_cards),
            new ServiceCategory("Flyers", R.drawable.service_flyers),
            new ServiceCategory("Banners", R.drawable.service_banners),
            new ServiceCategory("Posters", R.drawable.service_posters),
            new ServiceCategory("Stickers", R.drawable.service_stickers),
            new ServiceCategory("T-Shirts", R.drawable.service_tshirts),
            new ServiceCategory("Mugs", R.drawable.service_mugs)
        );
        services.setAdapter(new ServiceCategoryAdapter(categories, c -> 
            startActivity(new Intent(this, ProductListActivity.class).putExtra("category", c))
        ));

        findViewById(R.id.openGallery).setOnClickListener(v -> startActivity(new Intent(this, GalleryActivity.class)));
        findViewById(R.id.fab).setOnClickListener(v -> startActivity(new Intent(this, NewOrderActivity.class)));

        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            }
            if (id == R.id.nav_orders) {
                startActivity(new Intent(this, OrdersActivity.class));
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carouselHandler.removeCallbacks(advanceBanner);
        carouselHandler.postDelayed(advanceBanner, 4200);
    }

    @Override
    protected void onPause() {
        carouselHandler.removeCallbacks(advanceBanner);
        super.onPause();
    }
}
