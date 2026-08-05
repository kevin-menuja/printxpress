package com.printxpress.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.entities.Address;
import com.printxpress.app.database.entities.User;
import com.printxpress.app.utils.SessionManager;
import java.util.List;

public class ProfileActivity extends BaseActivity {
    private AppDatabase db;
    private SessionManager session;
    private User user;
    private LinearLayout addresses;
    private ShapeableImageView profileImage;
    private TextView avatar;

    private final ActivityResultLauncher<String[]> imagePicker = registerForActivityResult(
        new ActivityResultContracts.OpenDocument(), 
        this::saveProfileImage
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = AppDatabase.get(this);
        session = new SessionManager(this);
        addresses = findViewById(R.id.addresses);
        profileImage = findViewById(R.id.profileImage);
        avatar = findViewById(R.id.avatar);

        findViewById(R.id.changePhoto).setOnClickListener(v -> {
            if (user != null) {
                imagePicker.launch(new String[]{"image/*"});
            }
        });

        findViewById(R.id.edit).setOnClickListener(v -> {
            if (user != null) {
                editProfile();
            }
        });

        findViewById(R.id.addAddress).setOnClickListener(v -> {
            if (user != null) {
                addAddress();
            }
        });

        findViewById(R.id.history).setOnClickListener(v -> 
            startActivity(new Intent(this, OrdersActivity.class))
        );

        findViewById(R.id.savedDesigns).setOnClickListener(v -> 
            startActivity(new Intent(this, SavedDesignsActivity.class))
        );

        findViewById(R.id.guidelines).setOnClickListener(v -> 
            startActivity(new Intent(this, DesignGuidelinesActivity.class))
        );

        findViewById(R.id.faq).setOnClickListener(v -> 
            startActivity(new Intent(this, FaqActivity.class))
        );

        findViewById(R.id.logout).setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
            .setTitle("Log out?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Logout", (dialog, which) -> {
                session.logout();
                startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            })
            .show()
        );

        setupNavigation();
        loadProfile();
    }

    private void loadProfile() {
        DbExecutor.io().execute(() -> {
            User loaded = db.userDao().findById(session.getUserId());
            runOnUiThread(() -> {
                user = loaded;
                if (user == null) {
                    session.logout();
                    startActivity(new Intent(this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                    return;
                }
                render();
            });
        });
    }

    private void setupNavigation() {
        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.nav_profile);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                return true;
            }
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(this, OrdersActivity.class));
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationsActivity.class));
            }
            return true;
        });
    }

    private void saveProfileImage(Uri uri) {
        if (uri == null || user == null) {
            return;
        }
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {}
        
        getSharedPreferences("profile_images", MODE_PRIVATE)
            .edit()
            .putString("user_" + user.id, uri.toString())
            .apply();
        showProfileImage(uri);
    }

    private void showProfileImage(Uri uri) {
        profileImage.setImageURI(uri);
        profileImage.setVisibility(View.VISIBLE);
        avatar.setVisibility(View.GONE);
    }

    private void render() {
        ((TextView) findViewById(R.id.name)).setText(user.fullName);
        ((TextView) findViewById(R.id.email)).setText(user.email);
        ((TextView) findViewById(R.id.phone)).setText(user.phone);

        String safeName = (user.fullName == null || user.fullName.trim().isEmpty()) 
            ? "PrintXpress User" 
            : user.fullName.trim();

        StringBuilder initials = new StringBuilder();
        for (String word : safeName.split("\\s+")) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));
            }
        }
        avatar.setText(initials.length() == 0 
            ? "PX" 
            : initials.substring(0, Math.min(2, initials.length())).toUpperCase()
        );

        String saved = getSharedPreferences("profile_images", MODE_PRIVATE).getString("user_" + user.id, null);
        if (saved != null) {
            try {
                showProfileImage(Uri.parse(saved));
            } catch (Exception ignored) {
                profileImage.setVisibility(View.GONE);
                avatar.setVisibility(View.VISIBLE);
            }
        }
        renderAddresses();
    }

    private void renderAddresses() {
        DbExecutor.io().execute(() -> {
            List<Address> data = db.addressDao().getByUserId(user.id);
            runOnUiThread(() -> {
                addresses.removeAllViews();
                for (Address a : data) {
                    TextView textView = new TextView(this);
                    textView.setText(a.label + "\n" + a.fullAddress + "\nTap to delete");
                    textView.setPadding(16, 16, 16, 16);
                    textView.setBackgroundResource(R.drawable.card_background);
                    textView.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                        .setTitle("Delete address?")
                        .setPositiveButton("Delete", (dialog, which) -> DbExecutor.io().execute(() -> {
                            db.addressDao().delete(a);
                            runOnUiThread(this::renderAddresses);
                        }))
                        .setNegativeButton("Cancel", null)
                        .show()
                    );
                    addresses.addView(textView);
                }
            });
        });
    }

    private void editProfile() {
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(32, 0, 32, 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextInputEditText nInput = new TextInputEditText(this);
        TextInputEditText eInput = new TextInputEditText(this);
        TextInputEditText pInput = new TextInputEditText(this);

        nInput.setHint("Name");
        eInput.setHint("Email");
        pInput.setHint("Phone");

        nInput.setText(user.fullName);
        eInput.setText(user.email);
        pInput.setText(user.phone);

        layout.addView(nInput);
        layout.addView(eInput);
        layout.addView(pInput);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Edit profile")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save", (dialog, which) -> {
                String name = nInput.getText().toString().trim();
                String email = eInput.getText().toString().trim();
                String phone = pInput.getText().toString().trim();

                if (name.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !phone.matches("07\\d{8}")) {
                    Toast.makeText(this, "Enter a valid name, email and 10-digit 07 phone number", Toast.LENGTH_LONG).show();
                    return;
                }

                DbExecutor.io().execute(() -> {
                    User byEmail = db.userDao().findByEmail(email);
                    User byPhone = db.userDao().findByPhone(phone);
                    
                    boolean duplicate = (byEmail != null && byEmail.id != user.id) 
                        || (byPhone != null && byPhone.id != user.id);

                    if (!duplicate) {
                        user.fullName = name;
                        user.email = email;
                        user.phone = phone;
                        db.userDao().update(user);
                    }

                    runOnUiThread(() -> {
                        if (duplicate) {
                            Toast.makeText(this, "Email or phone is already used", Toast.LENGTH_LONG).show();
                        } else {
                            render();
                            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            })
            .show();
    }

    private void addAddress() {
        TextInputEditText addressInput = new TextInputEditText(this);
        addressInput.setHint("Full delivery address");
        addressInput.setPadding(32, 0, 32, 0);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Add address")
            .setView(addressInput)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Add", (dialog, which) -> {
                String addressStr = addressInput.getText().toString().trim();
                if (!addressStr.isEmpty()) {
                    DbExecutor.io().execute(() -> {
                        db.addressDao().insert(new Address(user.id, "Home", addressStr, false));
                        runOnUiThread(this::renderAddresses);
                    });
                }
            })
            .show();
    }
}
