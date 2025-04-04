package com.example.vent.utils  // Cleaned up the double package path


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vent.R
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

object AnimationUtils {
    @Composable
    fun LoadingScreen() {
        val transition = rememberInfiniteTransition()

        val alpha by transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            )
        )

        val scale by transition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.blue),
                strokeWidth = 5.dp,
                modifier = Modifier
                    .size(60.dp)
                    .scale(scale)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Fetching pending users...",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray.copy(alpha = alpha),
                textAlign = TextAlign.Center
            )
        }
    }


    @Composable
    fun AcceptButtonLoaderGlow(
        modifier: Modifier = Modifier,
        text: String = "Approving..."
    ) {
        val infiniteTransition = rememberInfiniteTransition()

        // Alpha pulsating for glow effect
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Optional: Pulsing scale for subtle bounce
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.98f,
            targetValue = 1.02f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Rainbow-like color cycle
        val colors = listOf(
            Color(0xFF7EFFDB),
            Color(0xFF40E0D0),
            Color(0xFF00BFFF),
            Color(0xFF7EFFDB)
        )

        val animatedBrush = Brush.linearGradient(colors)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = animatedAlpha
                }
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(2.dp, animatedBrush, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
    }

    @Composable
    fun RejectButtonLoaderGlow(
        modifier: Modifier = Modifier,
        text: String = "Rejecting..."
    ) {
        val infiniteTransition = rememberInfiniteTransition()

        // Alpha pulsating for glow effect
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Optional: Pulsing scale for subtle bounce
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.98f,
            targetValue = 1.02f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Rainbow-like color cycle
        val colors = listOf(
            Color(0xFFFF7F7A), // Base soft red
            Color(0xFFFF4C4C), // Bright red
            Color(0xFFFF1E56), // Reddish pink
            Color(0xFFFF7F7A)  // Back to base to loop nicely
        )


        val animatedBrush = Brush.linearGradient(colors)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = animatedAlpha
                }
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(2.dp, animatedBrush, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1
            )
        }
    }

//    @Composable
//    fun SlideOutOnAccept(
//        isVisible: Boolean,
//        onRemoved: () -> Unit,
//        content: @Composable () -> Unit
//    ) {
//        val scope = rememberCoroutineScope()
//        val animatedVisible = remember { mutableStateOf(isVisible) }
//
//        // Trigger animation when isVisible becomes false
//        LaunchedEffect(isVisible) {
//            if (!isVisible) {
//                delay(300)  // match animation duration
//                onRemoved()
//            }
//        }
//
//        AnimatedVisibility(
//            visible = animatedVisible.value,
//            exit = slideOutHorizontally(
//                targetOffsetX = { it },  // slide to right
//                animationSpec = tween(durationMillis = 300)
//            )
//        ) {
//            content()
//        }
//    }

}