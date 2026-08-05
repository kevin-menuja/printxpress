package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.printxpress.app.adapters.ProductAdapter;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DatabaseSeeder;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.entities.Product;
import java.util.List;

public class GalleryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView list = findViewById(R.id.galleryList);
        list.setLayoutManager(new GridLayoutManager(this, 2));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        DbExecutor.io().execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            DatabaseSeeder.seedProducts(db);
            List<Product> products = db.productDao().getAll();

            runOnUiThread(() -> list.setAdapter(new ProductAdapter(products, p -> 
                startActivity(new Intent(this, NewOrderActivity.class).putExtra("productId", p.id))
            )));
        });
    }
}
