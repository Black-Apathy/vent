package com.example.vent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.vent.network.UserApiService
import com.example.vent.utils.AnimationUtils

// 1. Updated Data Class (Safe defaults for fields that might be missing)
data class User(
    val id: Int,
    val email: String,
    val role: String = "student",
    val status: String = "pending",
    val password: String = "",
    val createdAt: String = ""
)

class PendingUsersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // 2. Pointing to the NEW Screen
                UserManagementScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen() {
    val context = LocalContext.current

    // 0 = Pending, 1 = All Users
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending Requests", "All Users")

    // State for Lists
    var pendingUsers by remember { mutableStateOf(emptyList<User>()) }
    var allUsers by remember { mutableStateOf(emptyList<User>()) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Unified data loader
    fun loadData() {
        isLoading = true
        errorMessage = null

        // --- FIX: Cast context to Activity to set title ---
        val activity = context as? android.app.Activity

        if (selectedTabIndex == 0) {
            activity?.title = "Pending Users" // Safe call

            UserApiService.fetchPendingUsers(context,
                onSuccess = { users ->
                    pendingUsers = users
                    isLoading = false
                },
                onError = { error ->
                    isLoading = false
                    errorMessage = error
                }
            )
        } else {
            activity?.title = "User Directory" // Safe call

            UserApiService.fetchAllUsers(context,
                onSuccess = { users ->
                    allUsers = users
                    isLoading = false
                },
                onError = { error ->
                    isLoading = false
                    errorMessage = error
                }
            )
        }
    }

    // Trigger load when tab changes
    LaunchedEffect(selectedTabIndex) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.cream))
    ) {
        // --- THE TABS ---
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = colorResource(R.color.blue),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = colorResource(R.color.orange)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                )
            }
        }

        // --- THE CONTENT ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> AnimationUtils.LoadingScreen()
                errorMessage != null -> {
                    Text(text = errorMessage ?: "Error", color = Color.Red)
                }
                else -> {
                    val currentList = if (selectedTabIndex == 0) pendingUsers else allUsers

                    if (currentList.isEmpty()) {
                        Text(
                            text = if (selectedTabIndex == 0) "No pending requests." else "No users found.",
                            color = Color.Gray
                        )
                    } else {
                        PullToRefreshBox(
                            isRefreshing = isLoading,
                            onRefresh = { loadData() }
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(currentList.size) { index ->
                                    if (selectedTabIndex == 0) {
                                        // TAB 1: Actionable Card
                                        PendingUserCard(
                                            user = currentList[index],
                                            onActionComplete = {
                                                // Remove locally instantly
                                                pendingUsers = pendingUsers - currentList[index]
                                            }
                                        )
                                    } else {
                                        // TAB 2: Read-Only Card
                                        AllUsersCard(user = currentList[index])
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. UPDATED Pending User Card (Accepts Callback now)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingUserCard(
    user: User,
    onActionComplete: () -> Unit // <--- NEW PARAMETER
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var isAcceptButtonLoading by remember { mutableStateOf(false) }
    var isRejectButtonLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(350.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF5386E4), Color(0xFF003366))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_pic),
                        contentDescription = "Profile Pic"
                    )
                    Text(color = Color.White, text = user.email)

                    // Role Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        Box(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                                .width(150.dp)
                                .padding(top = 20.dp)
                                .background(Color(0xFFFFF5EE), RoundedCornerShape(30.dp))
                                .padding(16.dp)
                                .clickable { expanded = true }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = selectedRole ?: "Set Role", color = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                            }
                        }
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFFFFF5EE))
                        ) {
                            listOf("Admin", "Teacher", "Student").forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(text = role) },
                                    onClick = { selectedRole = role; expanded = false }
                                )
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .padding(top = 25.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Accept Button
                        Button(
                            onClick = {
                                isAcceptButtonLoading = true
                                UserApiService.acceptUser(
                                    context = context,
                                    requestId = user.id,
                                    role = selectedRole.toString().lowercase(),
                                    onSuccess = {
                                        isAcceptButtonLoading = false
                                        Toast.makeText(context, "User approved!", Toast.LENGTH_SHORT).show()
                                        onActionComplete() // <--- Trigger List Update
                                    },
                                    onError = { error ->
                                        isAcceptButtonLoading = false
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier.width(120.dp).height(60.dp),
                            enabled = !isAcceptButtonLoading && selectedRole != null,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7EFFDB)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            if (isAcceptButtonLoading) {
                                AnimationUtils.AcceptButtonLoaderGlow(modifier = Modifier.fillMaxSize())
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                Text("Accept", color = Color.Black)
                            }
                        }

                        // Reject Button
                        Button(
                            onClick = {
                                isRejectButtonLoading = true
                                UserApiService.rejectUser(
                                    context = context,
                                    requestId = user.id,
                                    onSuccess = {
                                        isRejectButtonLoading = false
                                        Toast.makeText(context, "User rejected!", Toast.LENGTH_SHORT).show()
                                        onActionComplete() // <--- Trigger List Update
                                    },
                                    onError = { error ->
                                        isRejectButtonLoading = false
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier.width(120.dp).height(60.dp),
                            enabled = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7F7A)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            if (isRejectButtonLoading) {
                                AnimationUtils.RejectButtonLoaderGlow(modifier = Modifier.fillMaxSize())
                            } else {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
                                Text("Reject", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. All Users Card (Read Only)
@Composable
fun AllUsersCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = colorResource(R.color.blue).copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = user.email.take(1).uppercase(),
                            color = colorResource(R.color.blue),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = user.email,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.blue),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "ID: ${user.id}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Role Badge Logic
            val badgeColor = when (user.role.lowercase()) {
                "admin" -> Color(0xFFE26900)
                "teacher" -> Color(0xFF00A090)
                else -> Color.Gray
            }
            Surface(
                color = badgeColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = user.role.uppercase(),
                    color = badgeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}