package com.printxpress.app;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.printxpress.app.adapters.FaqAdapter;
import java.util.Arrays;
import java.util.List;

public class FaqActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        List<String[]> faqs = Arrays.asList(
            new String[]{"How long does printing take?", "2–5 business days standard; 24-hour express is available."},
            new String[]{"What file formats do you accept?", "PDF, AI, PSD, PNG and JPEG."},
            new String[]{"Can I get a proof before printing?", "Yes. A digital proof is sent within 24 hours."},
            new String[]{"What is your refund policy?", "A refund is available within 24 hours if production has not started."},
            new String[]{"Do you offer bulk discounts?", "Yes: 10% off 100+, 15% off 500+, and 20% off 1000+."},
            new String[]{"How do I track my order?", "Use real-time progress tracking in the Orders section."},
            new String[]{"What payment methods do you accept?", "Bank transfer, cash on delivery, and online payment."},
            new String[]{"Do you deliver island-wide?", "Yes, we deliver to all 25 districts in Sri Lanka."}
        );

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FaqAdapter(faqs));

        findViewById(R.id.contact).setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
            .setTitle("Contact PrintXpress")
            .setMessage("Phone: 011 234 5678\nEmail: support@printxpress.lk")
            .setNegativeButton("Close", null)
            .setPositiveButton("Call", null)
            .show()
        );
    }
}
