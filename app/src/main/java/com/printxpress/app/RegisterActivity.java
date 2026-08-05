package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.printxpress.app.database.AppDatabase;
import com.printxpress.app.database.DatabaseSeeder;
import com.printxpress.app.database.DbExecutor;
import com.printxpress.app.database.entities.User;
import com.printxpress.app.utils.PasswordUtils;
import java.util.Locale;

public class RegisterActivity extends BaseActivity {
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirm);

        tilName = findViewById(R.id.nameLayout);
        tilEmail = findViewById(R.id.emailLayout);
        tilPhone = findViewById(R.id.phoneLayout);
        tilPassword = findViewById(R.id.passwordLayout);
        tilConfirmPassword = findViewById(R.id.confirmLayout);

        findViewById(R.id.register).setOnClickListener(v -> saveUser());
    }

    private void saveUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Reset errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validation
        if (name.isEmpty()) {
            tilName.setError("Full name is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            return;
        }
        if (!phone.matches("07\\d{8}")) {
            tilPhone.setError("Phone must start with 07 and contain 10 digits");
            return;
        }
        if (password.length() < 6) {
            tilPassword.setError("Use at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            return;
        }

        MaterialCheckBox cbTerms = findViewById(R.id.terms);
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.register).setEnabled(false);
        AppDatabase db = AppDatabase.get(this);
        
        DbExecutor.io().execute(() -> {
            User byEmail = db.userDao().findByEmail(email);
            User byPhone = db.userDao().findByPhone(phone);
            
            if (byEmail == null && byPhone == null) {
                db.userDao().insert(new User(
                    name, 
                    email, 
                    phone, 
                    PasswordUtils.hash(password), 
                    System.currentTimeMillis()
                ));
                DatabaseSeeder.seedProducts(db);
            }

            runOnUiThread(() -> {
                findViewById(R.id.register).setEnabled(true);
                if (byEmail != null) {
                    tilEmail.setError("Email is already registered");
                } else if (byPhone != null) {
                    tilPhone.setError("Phone is already registered");
                } else {
                    Toast.makeText(this, "Registration successful. Please log in.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class).putExtra("email", email));
                    finish();
                }
            });
        });
    }
}
