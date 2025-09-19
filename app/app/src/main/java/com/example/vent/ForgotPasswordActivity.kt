package com.example.vent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreen()
        }
    }

    @Composable
    private fun ForgotPasswordScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFF9C46))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    // You should use a new string resource here for "Forgot Password?"
                    text = "Forgot Password?",
                    fontSize = 32.sp,
                    // Note: 'interFontFamily' is not defined in this file,
                    // you'll need to define it or import it from LoginActivity.kt
                    // fontFamily = interFontFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
                )
                ResetPasswordCard()
            }
        }
    }

    @Composable
    private fun ResetPasswordCard() {
        Box(
            modifier = Modifier
                .width(350.dp)
                .height(420.dp)
        ) {
            // Card with shadow
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xD9FFF5E1),
                )
            ) {
            }

            // Overlay to cancel bevels
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xD9FFF5E1), RoundedCornerShape(20.dp))
            ) {
            }
        }
    }

    @Preview
    @Composable
    private fun PreviewForgotPasswordScreen() {
        ForgotPasswordScreen()
    }
}