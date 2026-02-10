package com.example.vent

import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

fun Fragment.setupComposeHome(composeView: ComposeView, role: String) {
    composeView.setContent {
        // Updated to match your new HomeScreen signature (no userName)
        HomeScreen(userRole = role) { destination ->
            // Match the destination string to the correct Fragment
            val nextFragment = when (destination) {
                "REGISTRATION" -> EventRegisterationFragment()
                "VIEW_DATA" -> ViewDataFragment()
                "PENDING" -> PendingUsersFragment()
                "REPORTS" -> AboutUsFragment()
                else -> null
            }

            // Perform the Fragment Transaction
            nextFragment?.let {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}