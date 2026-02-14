package com.example.vent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vent.network.UserApiService
import com.example.vent.utils.AnimationUtils
import com.itextpdf.layout.property.TextAlignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf

// Color Palette from colors.xml
val BlueMain = Color(0xFF003366)
val CreamBG = Color(0xFFFFF5E1)
val OrangeAccent = Color(0xFFFF6600)

@Composable
fun HomeScreen(
    userRole: String = "admin",
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var pendingCount by remember { mutableIntStateOf(0) }

    if (userRole == "admin") {
        LaunchedEffect(Unit) {
            UserApiService.getPendingCount(context) { count ->
                pendingCount = count
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CreamBG
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Welcome back!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BlueMain,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 1. LOG EVENT: Only for Admin and Teacher
                if (userRole == "admin" || userRole == "teacher") {
                    item {
                        MenuCard("Log Event", Icons.Default.AddCircle, 100) {
                            onNavigate("REGISTRATION")
                        }
                    }
                }

                // 2. VIEW EVENTS: Visible to Everyone (including Students)
                item {
                    MenuCard("View Events", Icons.AutoMirrored.Filled.List, 200) {
                        onNavigate("VIEW_DATA")
                    }
                }

                // 3. ADMIN ONLY: Pending Users
                if (userRole == "admin") {
                    item {
                        MenuCard(
                            title = "User Management",
                            icon = Icons.Default.Person,
                            delay = 300,
                            // SMART LOGIC: Show dot ONLY if pendingCount > 0
                            hasNotification = pendingCount > 0
                        ) {
                            onNavigate("PENDING")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    delay: Int,
    hasNotification: Boolean = false,
    onClick: () -> Unit
) {
    AnimationUtils.SlideInCard(delayMillis = delay) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            // 1. SHADOW LAYER (exactly like your LoginActivity technique)
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CreamBG) // Matches background
            ) { }

            // 2. CRISP OVERLAY (eliminates bevels/artifacts)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .clickable { onClick() }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(45.dp),
                        tint = BlueMain
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = BlueMain,
                        fontSize = 16.sp
                    )
                }

                if (hasNotification) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .offset(x = (-8).dp, y = 8.dp)
                            .background(OrangeAccent, shape = RoundedCornerShape(50))
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreviewStudent() {
    HomeScreen(userRole = "student")
}

@Preview(showBackground = true)
@Composable
fun HomePreviewTeacher() {
    HomeScreen(userRole = "teacher")
}

@Preview(showBackground = true)
@Composable
fun HomePreviewAdmin() {
    HomeScreen(userRole = "admin")
}