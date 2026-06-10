package com.example.vestly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vestly.R;
import com.example.vestly.helper.SharedPrefManager;
import com.example.vestly.helper.ThemeManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply tema sebelum setContentView
        ThemeManager.applyTheme(SharedPrefManager.getInstance(this).isDarkTheme());

        setContentView(R.layout.activity_splash);

        // Tunggu 2 detik lalu langsung ke MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 2000);
    }
}