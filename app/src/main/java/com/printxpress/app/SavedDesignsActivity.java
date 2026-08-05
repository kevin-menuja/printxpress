package com.printxpress.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.printxpress.app.adapters.SavedDesignAdapter;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.entities.SavedDesign;
import com.printxpress.app.utils.SessionManager;
import java.util.List;

public class SavedDesignsActivity extends BaseActivity {
    private AppDatabase db;
    private RecyclerView list;
    private long uid;
    private boolean picking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_designs);

        db = AppDatabase.get(this);
        uid = new SessionManager(this).getUserId();
        picking = getIntent().getBooleanExtra("pick", false);

        list = findViewById(R.id.designList);
        list.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        loadSavedDesigns();
    }

    private void loadSavedDesigns() {
        DbExecutor.io().execute(() -> {
            List<SavedDesign> data = db.savedDesignDao().getByUserId(uid);
            runOnUiThread(() -> {
                findViewById(R.id.emptyState).setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                
                list.setAdapter(new SavedDesignAdapter(data, new SavedDesignAdapter.Actions() {
                    @Override
                    public void select(SavedDesign d) {
                        if (picking) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("uri", d.fileUri);
                            resultIntent.putExtra("name", d.name);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    }

                    @Override
                    public void delete(SavedDesign d) {
                        DbExecutor.io().execute(() -> {
                            db.savedDesignDao().delete(d);
                            runOnUiThread(() -> loadSavedDesigns());
                        });
                    }
                }));
            });
        });
    }
}
