package com.example.vent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.platform.ComposeView;
import androidx.compose.ui.platform.ViewCompositionStrategy;
import androidx.fragment.app.Fragment;

import com.example.vent.com.example.vent.utils.SessionManager;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Set the Title
        if (getActivity() != null) {
            getActivity().setTitle("Home");
        }

        // 2. Initialize ComposeView
        ComposeView composeView = new ComposeView(requireContext());

        // 3. Set the Strategy to prevent memory leaks
        composeView.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed.INSTANCE
        );

        // 4. Fetch User Data
        String role = SessionManager.INSTANCE.getUserRole(requireContext());
        String safeRole = (role != null) ? role : "student";

        // 5. Call the Kotlin Wrapper bridge with the Navigation Logic
        // We pass a lambda/callback that MainActivity will execute
        HomeViewWrapperKt.setupComposeHome(this, composeView, safeRole, route -> {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                // Sync the side menu highlight based on the route clicked in Compose
                switch (route) {
                    case "REGISTRATION":
                        activity.navigateWithSync(R.id.nav_registration);
                        break;
                    case "VIEW_DATA":
                        activity.navigateWithSync(R.id.nav_dataview);
                        break;
                    case "PENDING":
                        activity.navigateWithSync(R.id.nav_pendingUsers);
                        break;
                    default:
                        activity.navigateWithSync(R.id.nav_home);
                        break;
                }
            }
            return null; // Required for Kotlin Unit return type in Java
        });

        return composeView;
    }
}