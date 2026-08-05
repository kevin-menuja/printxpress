package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DatabaseSeeder;
import com.printxpress.app.database.entities.User;
import com.printxpress.app.utils.PasswordUtils;
import com.printxpress.app.utils.SessionManager;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private TextInputLayout identifierLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText identifier;
    private TextInputEditText password;
    private MaterialButton loginButton;

    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        identifierLayout = findViewById(R.id.identifierLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        identifier = findViewById(R.id.identifier);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        identifier.setText(getIntent().getStringExtra("email"));

        findViewById(R.id.register).setOnClickListener(v -> 
            startActivity(new Intent(this, RegisterActivity.class))
        );

        findViewById(R.id.forgot).setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
            .setTitle("Forgot password")
            .setMessage("Please contact support@printxpress.lk to reset your password.")
            .setPositiveButton("OK", null)
            .show()
        );

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        String idStr = identifier.getText().toString().trim();
        String passwordStr = password.getText().toString();

        identifierLayout.setError(null);
        passwordLayout.setError(null);

        if (idStr.isEmpty()) {
            identifierLayout.setError("Email or phone is required");
            identifier.requestFocus();
            return;
        }

        boolean isPhone = idStr.matches("\\d+");
        if (isPhone && !idStr.matches("07\\d{8}")) {
            identifierLayout.setError("Phone must start with 07 and contain 10 digits");
            return;
        }

        if (!isPhone) {
            idStr = idStr.toLowerCase(Locale.ROOT);
            if (!Patterns.EMAIL_ADDRESS.matcher(idStr).matches()) {
                identifierLayout.setError("Enter a valid email address");
                return;
            }
        }

        if (passwordStr.isEmpty()) {
            passwordLayout.setError("Password is required");
            return;
        }

        final String finalIdStr = idStr;
        loginButton.setEnabled(false);
        loginButton.setText("Signing in…");

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && password.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
        }

        ioExecutor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.get(this);
                User user = isPhone
                    ? db.userDao().findByPhone(finalIdStr)
                    : db.userDao().findByEmail(finalIdStr);

                if (user == null) {
                    runOnUiThread(() -> {
                        identifierLayout.setError("No account found. Please register first.");
                        resetButton();
                    });
                    return;
                }

                if (!PasswordUtils.verify(passwordStr, user.passwordHash)) {
                    runOnUiThread(() -> {
                        passwordLayout.setError("Incorrect password");
                        resetButton();
                    });
                    return;
                }

                try {
                    if (PasswordUtils.needsUpgrade(user.passwordHash)) {
                        user.passwordHash = PasswordUtils.hash(passwordStr);
                        db.userDao().update(user);
                    }
                    DatabaseSeeder.seedProducts(db);
                    DatabaseSeeder.seedNotifications(db, user.id);
                } catch (Exception seedError) {
                    Log.w(TAG, "Post-login data setup failed", seedError);
                }

                new SessionManager(this).saveUser(user.id);

                runOnUiThread(() -> {
                    startActivity(new Intent(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "Authentication failed", e);
                runOnUiThread(() -> {
                    passwordLayout.setError("Unable to sign in. Please try again.");
                    resetButton();
                });
            }
        });
    }

    private void resetButton() {
        loginButton.setEnabled(true);
        loginButton.setText(R.string.login);
    }

    @Override
    protected void onDestroy() {
        ioExecutor.shutdownNow();
        super.onDestroy();
    }
}
