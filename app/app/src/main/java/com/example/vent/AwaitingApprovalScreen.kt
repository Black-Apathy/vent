package com.example.vent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import com.example.vent.utils.AnimationUtils


class AwaitingApprovalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AwaitingApprovalScreen()
        }
    }
}

@Preview
@Composable
fun AwaitingApprovalScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5E1)),
        contentAlignment = Alignment.Center
    ) {
        AnimationUtils.SlideInCard(
            delayMillis = 200, // Optional: Stagger it if you want
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp) // Remove vertical padding to avoid double spacing
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White, shape = MaterialTheme.shapes.large) // Keep shape consistency
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HourglassAnimation(
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Please wait...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your registration is under review.",
                    fontSize = 18.sp,
                    color = Color(0xFF4C5C68),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You will be notified once the admin approves your request.",
                    fontSize = 14.sp,
                    color = Color(0xFF999999),
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Composable
fun HourglassAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.hourglass)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}