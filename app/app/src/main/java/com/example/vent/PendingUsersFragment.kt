package com.example.vent

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

class PendingUsersFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = "Pending Users"  // Dynamically set the title
        return ComposeView(requireContext()).apply {
            setContent {
                PendingUsersScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingUsersScreen() {

    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Set Role") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.cream))
            .padding(16.dp), // Adjust for inner padding
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card (
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
                    .background(Brush.verticalGradient(
                        colors = listOf(Color(0xFF5386E4), Color(0xFF003366))
                    ))
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Image(
                        painter = painterResource(id = R.drawable.profile_pic),
                        contentDescription = "Profile Pic" // Or provide a description if needed for accessibility
                    )
                    Text(
                        color = Color.White,
                        text = "test@example.com"
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        Box(
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable, true) // Ensures correct dropdown positioning
                                .width(150.dp)
                                .padding(top = 20.dp)
                                .background(Color(0xFFFFF5EE), shape = RoundedCornerShape(30.dp))
                                .padding(16.dp)
                                .clickable { expanded = true }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = selectedRole, color = Color.Black)
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
                            onDismissRequest = { expanded = false } ,
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

                    Row (
                        modifier = Modifier
                            .padding(top = 25.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Button(
                            onClick = { /* Handle click */ },
                            modifier = Modifier
                                .width(120.dp)
                                .height(60.dp), // Adjust width as needed
                            enabled = selectedRole != "Set Role", // Keeps it disabled
                            shape = RoundedCornerShape(8.dp), // Adds 8dp border radius
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7EFFDB), // Corrected syntax
                                disabledContainerColor = Color(0xFF9C9C9C)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp) // Space between icon and text
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check, // Tick Icon
                                    contentDescription = "Accept",
                                    tint = Color.Black
                                )
                                Text(
                                    text = "Accept",
                                    color = Color.Black,
                                    maxLines = 1
                                ) // Button text
                            }
                        }
                        Button(
                            onClick = { /* Handle click */ },
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(60.dp), // Adjust width as needed
                            enabled = selectedRole != "Set Role", // Keeps it disabled
                            shape = RoundedCornerShape(8.dp), // Adds 8dp border radius
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF7F7A), // Corrected syntax
                                disabledContainerColor = Color(0xFF9C9C9C)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ){
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Accept",
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


@Preview(showBackground = true)
@Composable
fun PendingUsersScreenPreview() {
    PendingUsersScreen()
}
