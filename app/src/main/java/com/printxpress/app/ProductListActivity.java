package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.printxpress.app.adapters.ProductAdapter;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DatabaseSeeder;
import com.printxpress.app.database.entities.Product;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductListActivity extends BaseActivity {
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        String category = getIntent().getStringExtra("category");
        setTitle(category == null ? "Products" : category);

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(this, 2));
        list.setHasFixedSize(true);
        list.setItemViewCacheSize(6);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(220);
        animator.setChangeDuration(160);
        list.setItemAnimator(animator);

        TextInputEditText search = findViewById(R.id.search);
        search.setEnabled(false);

        ioExecutor.execute(() -> {
            AppDatabase db = AppDatabase.get(this);
            DatabaseSeeder.seedProducts(db);
            List<Product> products = (category == null)
                ? db.productDao().getAll()
                : db.productDao().getByCategory(category);

            runOnUiThread(() -> {
                if (isFinishing()) {
                    return;
                }
                adapter = new ProductAdapter(products, p -> 
                    startActivity(new Intent(this, NewOrderActivity.class).putExtra("productId", p.id))
                );
                list.setAdapter(adapter);
                search.setEnabled(true);
            });
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onDestroy() {
        ioExecutor.shutdownNow();
        super.onDestroy();
    }
}
