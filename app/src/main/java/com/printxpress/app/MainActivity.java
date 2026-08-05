package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import com.printxpress.app.utils.SessionManager;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SessionManager sessionManager = new SessionManager(this);
        Class<?> targetClass = sessionManager.isLoggedIn() ? HomeActivity.class : LoginActivity.class;
        
        startActivity(new Intent(this, targetClass));
        finish();
    }
}
