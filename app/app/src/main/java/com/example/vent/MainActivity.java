package com.example.vent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vent.com.example.vent.utils.AuthTokenProvider;
import com.example.vent.com.example.vent.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean backPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting up Drawer Layout and Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Adding Toggle for Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PendingUsersFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Override back press to show confirmation
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedOnce) {
                    // If back pressed twice, exit the app
                    MainActivity.super.getOnBackPressedDispatcher().onBackPressed();  // Corrected this line
                } else {
                    // Show toast and set flag to true
                    Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                    backPressedOnce = true;

                    // Reset the flag after 2 seconds
                    new android.os.Handler().postDelayed(() -> backPressedOnce = false, 2000);
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PendingUsersFragment())
                    .commit();
        } else if (itemId == R.id.nav_registration) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventRegisterationFragment())
                    .commit();
        } else if (itemId == R.id.nav_dataview) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ViewDataFragment())
                    .commit();
        } else if (itemId == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AboutUsFragment())
                    .commit();
        } else if (itemId == R.id.nav_logout) {
            SessionManager.INSTANCE.logout(this);

            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

            // Redirect to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
