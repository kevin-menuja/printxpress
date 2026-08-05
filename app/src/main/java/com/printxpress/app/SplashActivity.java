package com.printxpress.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logo = findViewById(R.id.logo);
        logo.setScaleX(0.3f);
        logo.setScaleY(0.3f);
        logo.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .rotationBy(360.0f)
            .setDuration(900)
            .start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 2500);
    }
}
