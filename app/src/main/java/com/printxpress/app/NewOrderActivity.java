package com.printxpress.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DatabaseSeeder;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.OrderEvents;
import com.printxpress.app.database.entities.Order;
import com.printxpress.app.database.entities.Product;
import com.printxpress.app.database.entities.SavedDesign;
import com.printxpress.app.utils.SessionManager;
import com.printxpress.app.utils.ValidationUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewOrderActivity extends BaseActivity {
    private AppDatabase db;
    private Spinner category;
    private Spinner product;
    private Spinner material;
    private Spinner size;

    private TextInputEditText quantity;
    private TextInputEditText instructions;
    private TextInputEditText address;
    private TextInputEditText cardName;
    private TextInputEditText cardNumber;
    private TextInputEditText cardExpiry;
    private TextInputEditText cardCvv;

    private TextInputLayout quantityLayout;
    private TextInputLayout addressLayout;
    private TextInputLayout cardNameLayout;
    private TextInputLayout cardNumberLayout;
    private TextInputLayout cardExpiryLayout;
    private TextInputLayout cardCvvLayout;

    private View cardDetails;
    private List<Product> products;
    private Product selected;
    private long requiredDate;
    private long requestedProductId = -1;
    private Uri designUri;
    private String designName;

    private final ActivityResultLauncher<String[]> artworkPicker = registerForActivityResult(
        new ActivityResultContracts.OpenDocument(), 
        this::setArtwork
    );

    private final ActivityResultLauncher<Intent> savedDesignPicker = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), 
        r -> {
            if (r.getResultCode() == RESULT_OK && r.getData() != null) {
                designName = r.getData().getStringExtra("name");
                setArtwork(Uri.parse(r.getData().getStringExtra("uri")));
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        db = AppDatabase.get(this);

        category = findViewById(R.id.category);
        product = findViewById(R.id.product);
        material = findViewById(R.id.material);
        size = findViewById(R.id.size);

        quantity = findViewById(R.id.quantity);
        instructions = findViewById(R.id.instructions);
        address = findViewById(R.id.address);

        quantityLayout = findViewById(R.id.quantityLayout);
        addressLayout = findViewById(R.id.addressLayout);

        cardDetails = findViewById(R.id.cardDetails);
        cardName = findViewById(R.id.cardName);
        cardNumber = findViewById(R.id.cardNumber);
        cardExpiry = findViewById(R.id.cardExpiry);
        cardCvv = findViewById(R.id.cardCvv);

        cardNameLayout = findViewById(R.id.cardNameLayout);
        cardNumberLayout = findViewById(R.id.cardNumberLayout);
        cardExpiryLayout = findViewById(R.id.cardExpiryLayout);
        cardCvvLayout = findViewById(R.id.cardCvvLayout);

        setupCardFormatters();

        List<String> categories = Arrays.asList(
            "Business Cards", "Flyers", "Banners", "Posters", "Stickers", "T-Shirts", "Mugs"
        );
        category.setAdapter(adapter(categories));
        material.setAdapter(adapter(Arrays.asList("Glossy", "Matte", "Silk", "Standard")));

        category.setOnItemSelectedListener(new Selection() {
            @Override
            void selected(int p) {
                loadProducts(categories.get(p));
            }
        });

        product.setOnItemSelectedListener(new Selection() {
            @Override
            void selected(int p) {
                if (products != null && !products.isEmpty()) {
                    selected = products.get(p);
                    size.setAdapter(adapter(Arrays.asList(selected.availableSizes.split(","))));
                    ((ImageView) findViewById(R.id.orderProductImage)).setImageResource(productImage(selected.name));
                    updateTotal();
                }
            }
        });

        requestedProductId = getIntent().getLongExtra("productId", -1);
        if (requestedProductId > 0) {
            DbExecutor.io().execute(() -> {
                DatabaseSeeder.seedProducts(db);
                Product p = db.productDao().getById(requestedProductId);
                runOnUiThread(() -> {
                    if (p != null) {
                        category.setSelection(categories.indexOf(p.category));
                    }
                });
            });
        }

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ((RadioGroup) findViewById(R.id.deliveryType)).setOnCheckedChangeListener((g, id) -> 
            addressLayout.setVisibility(id == R.id.delivery ? View.VISIBLE : View.GONE)
        );

        ((RadioGroup) findViewById(R.id.paymentMethod)).setOnCheckedChangeListener((g, id) -> 
            showCardDetails(id == R.id.paymentCard)
        );

        findViewById(R.id.date).setOnClickListener(v -> pickDate());
        findViewById(R.id.upload).setOnClickListener(v -> chooseArtwork());
        findViewById(R.id.placeOrder).setOnClickListener(v -> placeOrder());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private ArrayAdapter<String> adapter(List<String> values) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
    }

    private void loadProducts(String c) {
        DbExecutor.io().execute(() -> {
            DatabaseSeeder.seedProducts(db);
            List<Product> loaded = db.productDao().getByCategory(c);
            List<String> names = new ArrayList<>();
            int index = 0;
            for (int i = 0; i < loaded.size(); i++) {
                Product p = loaded.get(i);
                names.add(p.name);
                if (p.id == requestedProductId) {
                    index = i;
                }
            }
            int requestedIndex = index;
            runOnUiThread(() -> {
                products = loaded;
                product.setAdapter(adapter(names));
                if (requestedProductId > 0) {
                    product.setSelection(requestedIndex);
                }
            });
        });
    }

    private void updateTotal() {
        int q = 1;
        try {
            q = Integer.parseInt(quantity.getText().toString());
        } catch (Exception ignored) {}
        
        double total = (selected == null) ? 0 : ValidationUtils.orderTotal(selected.basePrice, q);
        ((TextView) findViewById(R.id.total)).setText(String.format(Locale.US, "LKR %,.0f", total));
    }

    private void chooseArtwork() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Add print artwork")
            .setItems(new String[]{"Choose a new file", "Use a saved design", "Continue without artwork"}, 
                (dialog, which) -> {
                    if (which == 0) {
                        artworkPicker.launch(new String[]{"application/pdf", "image/*", "application/postscript"});
                    } else if (which == 1) {
                        savedDesignPicker.launch(new Intent(this, SavedDesignsActivity.class).putExtra("pick", true));
                    } else {
                        designUri = null;
                        designName = null;
                        ((TextView) findViewById(R.id.fileName)).setText("No file selected");
                    }
                }
            ).show();
    }

    private void setArtwork(Uri uri) {
        if (uri == null) {
            return;
        }
        designUri = uri;
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) {}
        
        if (designName == null || designName.trim().isEmpty()) {
            designName = "print_artwork";
            try (Cursor cursor = getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    designName = cursor.getString(0);
                }
            } catch (Exception ignored) {}
        }
        ((TextView) findViewById(R.id.fileName)).setText(
            "Selected: " + designName + "\nSaved to your design library when ordered"
        );
    }

    private int productImage(String name) {
        switch (name) {
            case "Standard Business Cards":
                return R.drawable.product_business_standard;
            case "Premium Business Cards":
                return R.drawable.product_business_premium;
            case "Square Business Cards":
                return R.drawable.product_business_square;
            case "Folded Business Cards":
                return R.drawable.product_business_folded;
            case "Flyers A5":
                return R.drawable.product_flyer_a5;
            case "Flyers A4":
                return R.drawable.product_flyer_a4;
            case "DL Tri-fold Flyers":
                return R.drawable.product_flyer_dl;
            case "Flyers A6":
                return R.drawable.product_flyer_a6;
            case "Banner 2x4ft":
                return R.drawable.product_banner_2x4;
            case "Banner 3x6ft":
                return R.drawable.product_banner_3x6;
            case "Pull-up Banner":
                return R.drawable.product_banner_pullup;
            case "X-Stand Banner":
                return R.drawable.product_banner_xstand;
            case "Poster A3":
                return R.drawable.product_poster_a3;
            case "Poster A2":
                return R.drawable.product_poster_a2;
            case "Poster A1":
                return R.drawable.product_poster_a1;
            case "Stickers Sheet":
                return R.drawable.product_stickers_sheet;
            case "Vinyl Sticker Pack":
                return R.drawable.product_stickers_vinyl;
            case "Die-cut Stickers":
                return R.drawable.product_stickers_diecut;
            case "T-Shirt Print":
                return R.drawable.product_tshirt_print;
            case "Premium T-Shirt Print":
                return R.drawable.product_tshirt_premium;
            case "Front & Back T-Shirt":
                return R.drawable.product_tshirt_frontback;
            case "Magic Mug":
                return R.drawable.product_mug_magic;
            case "Panoramic Photo Mug":
                return R.drawable.product_mug_photo;
            default:
                return R.drawable.product_mug_print;
        }
    }

    private void showCardDetails(boolean show) {
        ((TextView) findViewById(R.id.paymentNote)).setText(show
            ? "Enter your card details below. Sensitive details are not stored."
            : "Pay with cash when collecting or receiving your order."
        );
        if (show) {
            cardDetails.setVisibility(View.VISIBLE);
            cardDetails.setAlpha(0f);
            cardDetails.setTranslationY(-12f);
            cardDetails.animate().alpha(1f).translationY(0f).setDuration(220).start();
        } else {
            cardDetails.setVisibility(View.GONE);
            clearCardErrors();
        }
    }

    private void setupCardFormatters() {
        cardNumber.addTextChangedListener(new TextWatcher() {
            boolean changing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (changing) {
                    return;
                }
                String digits = editable.toString().replaceAll("\\D", "");
                if (digits.length() > 16) {
                    digits = digits.substring(0, 16);
                }
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(' ');
                    }
                    formatted.append(digits.charAt(i));
                }
                if (!formatted.toString().equals(editable.toString())) {
                    changing = true;
                    editable.replace(0, editable.length(), formatted);
                    changing = false;
                }
            }
        });

        cardExpiry.addTextChangedListener(new TextWatcher() {
            boolean changing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (changing) {
                    return;
                }
                String digits = editable.toString().replaceAll("\\D", "");
                if (digits.length() > 4) {
                    digits = digits.substring(0, 4);
                }
                String formatted = (digits.length() > 2) 
                    ? digits.substring(0, 2) + "/" + digits.substring(2) 
                    : digits;
                if (!formatted.equals(editable.toString())) {
                    changing = true;
                    editable.replace(0, editable.length(), formatted);
                    changing = false;
                }
            }
        });
    }

    private void clearCardErrors() {
        cardNameLayout.setError(null);
        cardNumberLayout.setError(null);
        cardExpiryLayout.setError(null);
        cardCvvLayout.setError(null);
    }

    private boolean validateCard() {
        clearCardErrors();
        String name = cardName.getText().toString().trim();
        String number = cardNumber.getText().toString().replaceAll("\\D", "");
        String expiry = cardExpiry.getText().toString().trim();
        String cvv = cardCvv.getText().toString();

        if (name.length() < 2) {
            cardNameLayout.setError("Enter the name shown on the card");
            cardName.requestFocus();
            return false;
        }
        if (number.length() < 13 || number.length() > 16 || !passesLuhn(number)) {
            cardNumberLayout.setError("Enter a valid card number");
            cardNumber.requestFocus();
            return false;
        }
        if (!validExpiry(expiry)) {
            cardExpiryLayout.setError("Enter a valid future date (MM/YY)");
            cardExpiry.requestFocus();
            return false;
        }
        if (!cvv.matches("\\d{3,4}")) {
            cardCvvLayout.setError("Enter a valid 3 or 4 digit CVV");
            cardCvv.requestFocus();
            return false;
        }
        return true;
    }

    private boolean passesLuhn(String number) {
        return ValidationUtils.passesLuhn(number);
    }

    private boolean validExpiry(String expiry) {
        return ValidationUtils.validExpiry(expiry);
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            c.set(year, month, dayOfMonth, 0, 0, 0);
            requiredDate = c.getTimeInMillis();
            ((Button) findViewById(R.id.date)).setText(
                new SimpleDateFormat("dd MMM yyyy", Locale.US).format(requiredDate)
            );
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void placeOrder() {
        quantityLayout.setError(null);
        addressLayout.setError(null);
        
        int parsed;
        try {
            parsed = Integer.parseInt(quantity.getText().toString());
        } catch (Exception e) {
            parsed = 0;
        }
        
        if (parsed < 1) {
            quantityLayout.setError("Quantity must be at least 1");
            return;
        }
        
        boolean delivery = ((RadioButton) findViewById(R.id.delivery)).isChecked();
        String addr = (address.getText() == null) ? "" : address.getText().toString().trim();
        if (delivery && addr.isEmpty()) {
            addressLayout.setError("Delivery address is required");
            return;
        }
        if (requiredDate == 0) {
            Toast.makeText(this, "Select a required date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selected == null) {
            Toast.makeText(this, "Select a product", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean card = ((RadioButton) findViewById(R.id.paymentCard)).isChecked();
        if (card && !validateCard()) {
            return;
        }
        
        int q = parsed;
        String payment = card ? "CARD" : "CASH";
        String digits = card ? cardNumber.getText().toString().replaceAll("\\D", "") : "";
        String paymentSummary = card ? "Card ending •••• " + digits.substring(digits.length() - 4) : "Cash";
        long now = System.currentTimeMillis();
        String orderId = String.format(Locale.US, "PX-%05d", now % 100000);
        long uid = new SessionManager(this).getUserId();
        
        Order order = new Order(
            uid, 
            selected.id, 
            selected.name, 
            selected.category, 
            q, 
            material.getSelectedItem().toString(), 
            size.getSelectedItem().toString(), 
            instructions.getText().toString(), 
            delivery ? "DELIVERY" : "PICKUP", 
            addr, 
            payment, 
            requiredDate, 
            selected.basePrice * q, 
            "PROCESSING", 
            now, 
            orderId, 
            designUri == null ? null : designUri.toString()
        );
        
        findViewById(R.id.placeOrder).setEnabled(false);
        
        DbExecutor.io().execute(() -> {
            db.orderDao().insert(order);
            if (designUri != null) {
                db.savedDesignDao().insert(new SavedDesign(uid, designName, designUri.toString(), now));
            }
            OrderEvents.placed(db, order);
            runOnUiThread(() -> {
                cardNumber.setText("");
                cardCvv.setText("");
                findViewById(R.id.placeOrder).setEnabled(true);
                new MaterialAlertDialogBuilder(this)
                    .setTitle("Order confirmed")
                    .setMessage("Your order #" + orderId + " has been placed.\nPayment: " + paymentSummary + 
                        (designUri == null ? "" : "\nArtwork saved to your design library."))
                    .setPositiveButton("View orders", (dialog, which) -> {
                        startActivity(new Intent(this, OrdersActivity.class));
                        finish();
                    })
                    .setCancelable(false)
                    .show();
            });
        });
    }

    abstract static class Selection implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selected(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}

        abstract void selected(int position);
    }
}
