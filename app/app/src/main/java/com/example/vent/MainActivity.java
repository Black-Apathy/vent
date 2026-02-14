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


    /**
     * Public method to navigate and sync the side menu highlight.
     * This can be called from any Fragment.
     */
    public void navigateWithSync(int menuItemId) {
        // 1. Update the Fragment
        androidx.fragment.app.Fragment fragment = null;

        if (menuItemId == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (menuItemId == R.id.nav_registration) {
            fragment = new EventRegisterationFragment();
        } else if (menuItemId == R.id.nav_pendingUsers) {
            fragment = new PendingUsersFragment();
        } else if (menuItemId == R.id.nav_dataview) {
            fragment = new ViewDataFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Allows the back button to work correctly
                    .commit();
        }

        // 2. Sync the Navigation Drawer
        navigationView.setCheckedItem(menuItemId);
    }

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

        android.view.Menu menu = navigationView.getMenu();

        String role = SessionManager.INSTANCE.getUserRole(this);
        if (role == null) role = "student";

        // Visibility Logic
        if (role.equals("student")) {
            MenuItem logEvent = menu.findItem(R.id.nav_registration);
            if (logEvent != null) logEvent.setVisible(false);
            MenuItem manageUsers = menu.findItem(R.id.nav_pendingUsers);
            if (manageUsers != null) manageUsers.setVisible(false);
        }
        else if (role.equals("teacher")) {
            MenuItem manageUsers = menu.findItem(R.id.nav_pendingUsers);
            if (manageUsers != null) manageUsers.setVisible(false);
        }

        // Adding Toggle for Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Inside onCreate in MainActivity.java
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 1. Check if Drawer is open first - if so, close it
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }

                // 2. Check which fragment is currently visible
                androidx.fragment.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                // 3. If we are on the Home screen (or the backstack is empty), show the exit confirmation
                if (currentFragment instanceof HomeFragment || getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    if (backPressedOnce) {
                        // If back pressed twice on Home, exit
                        MainActivity.super.getOnBackPressedDispatcher().onBackPressed();
                    } else {
                        Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                        backPressedOnce = true;
                        new android.os.Handler().postDelayed(() -> backPressedOnce = false, 2000);
                    }
                } else {
                    // 4. If we are on ANY other screen (Log Event, View Data, etc.), just go back
                    getSupportFragmentManager().popBackStack();

                    // Optional: Update the Navigation Drawer selection to match "Home" if we just went back to it
                    navigationView.setCheckedItem(R.id.nav_home);
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            showLogoutConfirmation(); // Call a confirmation dialog
        } else {
            navigateWithSync(id);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // 1. Clear Tokens
                    SessionManager.INSTANCE.logout(this);

                    // 2. Redirect to Login
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}
