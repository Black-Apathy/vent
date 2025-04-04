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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.vent.network.UserApiService
import com.example.vent.utils.AnimationUtils

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val createdAt: String
)

class PendingUsersFragment : Fragment() {

    private var users by mutableStateOf(emptyList<User>())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().title = "Pending Users"  // Dynamically set the title

        return ComposeView(requireContext()).apply {
            setContent {
                val context = LocalContext.current
                var isLoading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                // Fetch users from API
                LaunchedEffect(Unit) {
                    UserApiService.fetchPendingUsers(
                        context,
                        onSuccess = { fetchedUsers ->
                            users = fetchedUsers
                            isLoading = false
                            errorMessage = null
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                            Log.e("PendingUsersFragment", error)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                PendingUsersScreen(
                    users = users,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                )
            }
        }
    }

    @Composable
    fun PendingUsersScreen(users: List<User>, isLoading: Boolean, errorMessage: String?) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.cream))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> {
                    AnimationUtils.LoadingScreen()
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                users.isEmpty() -> {
                    Text(
                        text = "No pending users available",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // TODO: When user scrolls down on the card list, refresh the page
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(users.size) { index ->
                            PendingUserCard(users[index])  // Correct way to pass user
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PendingUserCard(
        user: User,
    ) {

        var expanded by remember { mutableStateOf(false) }
        var selectedRole by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Removed unnecessary background
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(350.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 20.dp
                ),
                shape = RoundedCornerShape(20.dp)
            )
            {
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
                            contentDescription = "Profile Pic" // Or provide a description if needed for accessibility
                        )
                        Text(
                            color = Color.White,
                            text = user.email
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            Box(
                                modifier = Modifier
                                    .menuAnchor(
                                        MenuAnchorType.PrimaryEditable,
                                        true
                                    ) // Ensures correct dropdown positioning
                                    .width(150.dp)
                                    .padding(top = 20.dp)
                                    .background(
                                        Color(0xFFFFF5EE),
                                        shape = RoundedCornerShape(30.dp)
                                    )
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
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow",
                                        tint = Color.Black
                                    )
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
                                        onClick = {
                                            selectedRole = role
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Buttons
                        Row(
                            modifier = Modifier
                                .padding(top = 25.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            var isAcceptButtonLoading by remember { mutableStateOf(false) }
                            var isRejectButtonLoading by remember { mutableStateOf(false) }

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
                                            users = users - user
                                            Toast.makeText(context, "User approved!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { errorMessage ->
                                            isAcceptButtonLoading = false
                                            Log.e("PendingUsersFragment", errorMessage)
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(60.dp),
                                enabled = !isAcceptButtonLoading && selectedRole != null,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7EFFDB),
                                    disabledContainerColor = Color(0xFF9C9C9C)
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isAcceptButtonLoading) {
                                        AnimationUtils.AcceptButtonLoaderGlow(modifier = Modifier.fillMaxSize())
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Accept",
                                            tint = Color.Black
                                        )
                                        Text(
                                            text = "Accept",
                                            color = Color.Black,
                                            maxLines = 1
                                        )
                                    }
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
                                            users = users - user
                                            Toast.makeText(context, "User rejected!", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { errorMessage ->
                                            isRejectButtonLoading = false
                                            Log.e("PendingUsersFragment", errorMessage)
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(60.dp), // Adjust width as needed
                                enabled = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF7F7A),
                                    disabledContainerColor = Color(0xFF9C9C9C)
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isRejectButtonLoading) {
                                        AnimationUtils.RejectButtonLoaderGlow(
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Reject",
                                                tint = Color.Black
                                            )
                                            Text(
                                                text = "Reject",
                                                color = Color.Black,
                                                maxLines = 1
                                            )
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
}