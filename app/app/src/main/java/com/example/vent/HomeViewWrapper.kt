package com.example.vent

import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

/**
 * Standard function to bridge Java Fragment and Kotlin Compose
 */
fun setupComposeHome(
    fragment: Fragment,
    composeView: ComposeView,
    role: String,
    onNavigate: (String) -> Unit // The callback for MainActivity sync
) {
    composeView.setContent {
        HomeScreen(
            userRole = role,
            onNavigate = onNavigate // Passes the clicked route back to the Fragment
        )
    }
}