package com.example.vestly.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.vestly.R;
import com.example.vestly.databinding.ActivityMainBinding;
import com.example.vestly.helper.SharedPrefManager;
import com.example.vestly.helper.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LinearLayout splashOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(SharedPrefManager.getInstance(this).isDarkTheme());
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        splashOverlay = findViewById(R.id.splash_overlay);

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Tampilkan splash selama 2 detik, lalu hilang
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (splashOverlay != null) {
                splashOverlay.setVisibility(View.GONE);
            }
        }, 2000);
    }
}