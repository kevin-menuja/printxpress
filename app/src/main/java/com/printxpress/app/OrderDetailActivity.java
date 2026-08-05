package com.printxpress.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.OrderEvents;
import com.printxpress.app.database.OrderProgressor;
import com.printxpress.app.database.entities.Order;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class OrderDetailActivity extends BaseActivity {
    private AppDatabase db;
    private Order o;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            if (o != null) {
                DbExecutor.io().execute(() -> {
                    boolean changed = OrderProgressor.refresh(db, o);
                    if (changed) {
                        runOnUiThread(() -> render());
                    }
                });
            }
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        db = AppDatabase.get(this);
        long id = getIntent().getLongExtra("id", -1);

        DbExecutor.io().execute(() -> {
            o = db.orderDao().getById(id);
            if (o != null) {
                OrderProgressor.refresh(db, o);
            }
            runOnUiThread(() -> {
                if (o == null) {
                    finish();
                    return;
                }
                render();
            });
        });

        findViewById(R.id.cancel).setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
            .setTitle("Cancel order?")
            .setMessage("This action cannot be undone.")
            .setNegativeButton("Keep order", null)
            .setPositiveButton("Cancel order", (dialog, which) -> DbExecutor.io().execute(() -> {
                db.orderDao().updateStatusWithTime(o.id, "CANCELLED", System.currentTimeMillis());
                o.status = "CANCELLED";
                OrderEvents.cancelled(db, o);
                runOnUiThread(() -> render());
            }))
            .show()
        );

        findViewById(R.id.reschedule).setOnClickListener(v -> pickDate());
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refresh);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(refresh);
        super.onPause();
    }

    private void render() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        
        String artworkText = (o.designUri == null) ? "" : "\nArtwork attached";
        String payment = (o.paymentMethod == null) ? "CASH" : o.paymentMethod;

        ((TextView) findViewById(R.id.summary)).setText(
            "#" + o.orderId + "\n\n" +
            o.productName + " • " + o.category + "\n" +
            "Quantity: " + o.quantity + "\n" +
            "Material: " + o.material + "\n" +
            "Size: " + o.size + artworkText + "\n" +
            "Delivery: " + o.deliveryType + "\n" +
            "Payment: " + payment + "\n" +
            "Required: " + dateFormat.format(o.requiredDate) + "\n\n" +
            "Total: " + String.format(Locale.US, "LKR %,.0f", o.totalPrice) + "\n" +
            "Status: " + o.status
        );

        LinearLayout layout = findViewById(R.id.timeline);
        layout.removeAllViews();

        String[] steps = {"PROCESSING", "PRINTING", "READY", "DELIVERED"};
        int currentIndex = Arrays.asList(steps).indexOf(o.status);

        for (int i = 0; i < steps.length; i++) {
            TextView textView = new TextView(this);
            String prefix = (i <= currentIndex) ? "●  " : "○  ";
            String stepName = steps[i];
            
            if (stepName.equals("READY")) {
                stepName = o.deliveryType.equals("DELIVERY") ? "OUT FOR DELIVERY" : "READY FOR PICKUP";
            }
            
            textView.setText(prefix + stepName);
            textView.setTextSize(17);
            textView.setPadding(8, 16, 8, 16);
            textView.setTextColor(ContextCompat.getColor(
                this, 
                (i == currentIndex) ? R.color.secondary : R.color.text_secondary
            ));
            layout.addView(textView);
        }

        boolean canCancel = o.status.equals("PROCESSING");
        boolean canReschedule = !o.status.equals("DELIVERED") && !o.status.equals("CANCELLED");
        
        findViewById(R.id.cancel).setVisibility(canCancel ? View.VISIBLE : View.GONE);
        findViewById(R.id.reschedule).setVisibility(canReschedule ? View.VISIBLE : View.GONE);
    }

    private void pickDate() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            if (calendar.getTimeInMillis() < System.currentTimeMillis() - 86400000) {
                Toast.makeText(this, "Date cannot be in the past", Toast.LENGTH_SHORT).show();
                return;
            }
            long date = calendar.getTimeInMillis();
            DbExecutor.io().execute(() -> {
                db.orderDao().reschedule(o.id, date, System.currentTimeMillis());
                o.requiredDate = date;
                OrderEvents.rescheduled(db, o);
                runOnUiThread(() -> render());
            });
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
