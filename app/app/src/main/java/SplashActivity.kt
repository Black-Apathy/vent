package com.example.vent

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vent.com.example.vent.utils.AuthTokenProvider
import com.example.vent.com.example.vent.utils.SessionManager
import com.example.vent.network.UserApiService
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Call the actual composable here
            SplashScreenContent()
        }
    }

    // This is the Composable that will be displayed
    @Composable
    fun SplashScreenContent() {
        val emojis = listOf("🎉", "🎈", "🥳", "✨")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.cream)),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            EmojiWaveLoader(emojis)
        }

        // Handle the token logic using LaunchedEffect
        LaunchedEffect(Unit) {
            // The rest of your token logic here...
            if (SessionManager.shouldForceLogout(this@SplashActivity)) {
                SessionManager.logout(this@SplashActivity)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            } else if (AuthTokenProvider.isAccessTokenExpired(this@SplashActivity)) {
                val refreshToken = AuthTokenProvider.getRefreshToken(this@SplashActivity)
                if (refreshToken.isNullOrEmpty()) {
                    SessionManager.logout(this@SplashActivity)
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                } else {
                    UserApiService.refreshToken(this@SplashActivity, refreshToken) { success ->
                        if (success) {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        } else {
                            SessionManager.logout(this@SplashActivity)
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        }
                        finish()
                    }
                }
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    // Keep the preview function separate
    @Preview
    @Composable
    fun PreviewSplashScreen() {
        SplashScreenContent()
    }

    @Composable
    fun EmojiWaveLoader(emojis: List<String>) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emojis.forEachIndexed { index, emoji ->
                val infiniteTransition = rememberInfiniteTransition(label = "emojiWaveTransition")
                val offset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = -20f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 600,
                            easing = LinearEasing,
                            delayMillis = index * 100
                        ),
                        repeatMode = RepeatMode.Reverse
                    ), label = "emojiOffsetAnimation"
                )
                Box(modifier = Modifier.offset(y = offset.dp)) {
                    Text(text = emoji, fontSize = 32.sp)
                }
            }
        }
    }
}