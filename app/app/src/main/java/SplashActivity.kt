package com.example.vent

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.vent.com.example.vent.utils.AuthTokenProvider
import com.example.vent.com.example.vent.utils.SessionManager
import com.example.vent.network.UserApiService

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Using the XML layout we created

        // 1. Find Views
        val logo = findViewById<ImageView>(R.id.img_logo)
        val text = findViewById<TextView>(R.id.tv_app_name)

        // 2. Set Initial Positions (Push them down so they can slide up)
        logo.translationY = 100f
        text.translationY = 100f

        // 3. Animate Logo (Fade In + Slide Up)
        logo.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1000)
            .setStartDelay(200)
            .start()

        // 4. Animate Text & Trigger Session Check
        text.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1000)
            .setStartDelay(500) // Starts slightly after logo
            .withEndAction {
                // 5. ANIMATION FINISHED -> NOW CHECK LOGIN STATUS
                checkUserSession()
            }
            .start()
    }

    /**
     * Your original Token/Session Logic, moved from Compose to a standard function.
     */
    private fun checkUserSession() {
        if (SessionManager.shouldForceLogout(this)) {
            navigateToLogin()
        } else if (AuthTokenProvider.isAccessTokenExpired(this)) {
            val refreshToken = AuthTokenProvider.getRefreshToken(this)
            if (refreshToken.isNullOrEmpty()) {
                navigateToLogin()
            } else {
                // Attempt to refresh the token
                UserApiService.refreshToken(this, refreshToken) { success ->
                    if (success) {
                        navigateToMain()
                    } else {
                        navigateToLogin()
                    }
                }
            }
        } else {
            // Token is valid
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun navigateToLogin() {
        SessionManager.logout(this)
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}