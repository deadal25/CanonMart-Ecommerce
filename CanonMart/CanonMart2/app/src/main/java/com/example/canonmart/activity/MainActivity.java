package com.example.canonmart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.canonmart.fragment.CartFragment;
import com.example.canonmart.fragment.ExpensiveFragment;
import com.example.canonmart.fragment.FavoritesFragment;
import com.example.canonmart.fragment.HomeFragment;
import com.example.canonmart.fragment.SettingsFragment;
import com.example.canonmart.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton favoritesButton;
    private ImageButton cartButton;
    private RelativeLayout navHome, navExpensive, navSettings;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        favoritesButton = findViewById(R.id.favorites_button);
        cartButton = findViewById(R.id.cart_button);
        navHome = findViewById(R.id.nav_home);
        navExpensive = findViewById(R.id.nav_expensive);
        navSettings = findViewById(R.id.nav_settings);

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new FavoritesFragment();
                String tag = "FavoritesFragment";
                switchFragment(selectedFragment, tag);
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new CartFragment();
                String tag = "CartFragment";
                switchFragment(selectedFragment, tag);
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new HomeFragment();
                String tag = "HomeFragment";
                switchFragment(selectedFragment, tag);
                navHome.setBackgroundResource(R.drawable.background_clicked);
                navExpensive.setBackgroundResource(0);
                navSettings.setBackgroundResource(0);
            }
        });

        navExpensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new ExpensiveFragment();
                String tag = "ExpensiveFragment";
                switchFragment(selectedFragment, tag);
                navExpensive.setBackgroundResource(R.drawable.background_clicked);
                navHome.setBackgroundResource(0);
                navSettings.setBackgroundResource(0);
            }
        });

        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new SettingsFragment();
                String tag = "SettingsFragment";
                switchFragment(selectedFragment, tag);
                navSettings.setBackgroundResource(R.drawable.background_clicked);
                navHome.setBackgroundResource(0);
                navExpensive.setBackgroundResource(0);
            }
        });

        if (savedInstanceState == null) {
            String lastOpenedFragment = sharedPreferences.getString("last_opened_fragment", "HomeFragment");
            switch (lastOpenedFragment) {
                case "FavoritesFragment":
                    switchFragment(new FavoritesFragment(), "FavoritesFragment");
                    break;
                case "CartFragment":
                    switchFragment(new CartFragment(), "CartFragment");
                    break;
                case "ExpensiveFragment":
                    switchFragment(new ExpensiveFragment(), "ExpensiveFragment");
                    break;
                case "SettingsFragment":
                    switchFragment(new SettingsFragment(), "SettingsFragment");
                    break;
                case "HomeFragment":
                default:
                    switchFragment(new HomeFragment(), "HomeFragment");
                    break;
            }
        }
    }

    private void applyTheme() {
        if (sharedPreferences != null) {
            boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    private void switchFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
        updateButtonIcons(tag);

        navHome.setBackgroundResource(0);
        navExpensive.setBackgroundResource(0);
        navSettings.setBackgroundResource(0);

        if (tag.equals("FavoritesFragment")) {
            favoritesButton.setBackgroundResource(R.drawable.baseline_favorite_24);
            cartButton.setBackgroundResource(0);
        } else if (tag.equals("CartFragment")) {
            cartButton.setBackgroundResource(R.drawable.ic_cart_dark);
            favoritesButton.setBackgroundResource(0);
        } else {
            favoritesButton.setBackgroundResource(0);
            cartButton.setBackgroundResource(0);
        }

        sharedPreferences.edit().putString("last_opened_fragment", tag).apply();
    }

    public void updateButtonIcons(String tag) {
        if (tag.equals("FavoritesFragment")) {
            favoritesButton.setImageResource(R.drawable.ic_favorites_dark);
            cartButton.setImageResource(R.drawable.ic_cart);
        } else if (tag.equals("CartFragment")) {
            cartButton.setImageResource(R.drawable.ic_cart_dark);
            favoritesButton.setImageResource(R.drawable.ic_favorites);
        } else {
            favoritesButton.setImageResource(R.drawable.ic_favorites);
            cartButton.setImageResource(R.drawable.ic_cart);
        }
    }

    public void resetButtonIcons() {
        favoritesButton.setImageResource(R.drawable.ic_favorites);
        cartButton.setImageResource(R.drawable.ic_cart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lastOpenedFragment = sharedPreferences.getString("last_opened_fragment", "HomeFragment");
        switch (lastOpenedFragment) {
            case "FavoritesFragment":
                switchFragment(new FavoritesFragment(), "FavoritesFragment");
                navHome.setBackgroundResource(0);
                navExpensive.setBackgroundResource(0);
                navSettings.setBackgroundResource(0);
                favoritesButton.setBackgroundResource(R.drawable.baseline_favorite_24);
                cartButton.setBackgroundResource(0);
                break;
            case "CartFragment":
                switchFragment(new CartFragment(), "CartFragment");
                navHome.setBackgroundResource(0);
                navExpensive.setBackgroundResource(0);
                navSettings.setBackgroundResource(0);
                cartButton.setBackgroundResource(R.drawable.ic_cart_dark);
                favoritesButton.setBackgroundResource(0);
                break;
            case "ExpensiveFragment":
                switchFragment(new ExpensiveFragment(), "ExpensiveFragment");
                navHome.setBackgroundResource(0);
                navExpensive.setBackgroundResource(R.drawable.background_clicked);
                navSettings.setBackgroundResource(0);
                favoritesButton.setBackgroundResource(0);
                cartButton.setBackgroundResource(0
                );
                break;
            case "SettingsFragment":
                switchFragment(new SettingsFragment(), "SettingsFragment");
                navHome.setBackgroundResource(0);
                navExpensive.setBackgroundResource(0);
                navSettings.setBackgroundResource(R.drawable.background_clicked);
                favoritesButton.setBackgroundResource(0);
                cartButton.setBackgroundResource(0);
                break;
            case "HomeFragment":
            default:
                switchFragment(new HomeFragment(), "HomeFragment");
                navHome.setBackgroundResource(R.drawable.background_clicked);
                navExpensive.setBackgroundResource(0);
                navSettings.setBackgroundResource(0);
                favoritesButton.setBackgroundResource(0);
                cartButton.setBackgroundResource(0);
                break;
        }
    }

//    @Override
//    public void onCheckoutSuccess(double totalPrice) {
//        // Panggil method updateSaldo di SettingsFragment
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("SettingsFragment");
//        if (fragment != null && fragment.isVisible()) {
//            ((SettingsFragment) fragment).onCheckoutSuccess(totalPrice);
//        }
//    }
}
