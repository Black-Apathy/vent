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
        String name = SessionManager.INSTANCE.getUserName(requireContext());

        // 5. Provide fallback values to avoid null pointer issues in UI
        String safeRole = (role != null) ? role : "student";

        // 6. Call the Kotlin Wrapper bridge
        HomeViewWrapperKt.setupComposeHome(this, composeView, safeRole);

        return composeView;
    }
}