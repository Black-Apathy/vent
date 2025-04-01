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

                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(Color(0xFFFFE1B5), shape = RoundedCornerShape(30.dp))
                            .padding(16.dp)
                            .clickable { expanded = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .width(150.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center // Centers text & icon
                        ) {
                            Text(text = selectedRole, color = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp)) // Adds space between text and icon
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Arrow",
                                tint = Color.Black
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false })
                    {
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
                    Row (
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Button(
                            onClick = { /* Handle click */ },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp), // Adjust width as needed
                            enabled = true, // Keeps it disabled
                            shape = RoundedCornerShape(8.dp), // Adds 8dp border radius
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7EFFDB), // Corrected syntax
                                disabledContainerColor = Color(0xFF9C9C9C)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Accept",
                                color = Color.Black,
                                maxLines = 1
                            ) // Button text
                        }
                        Button(
                            onClick = { /* Handle click */ },
                            modifier = Modifier
                                .width(100.dp)
                                .height(60.dp), // Adjust width as needed
                            enabled = true, // Keeps it disabled
                            shape = RoundedCornerShape(8.dp), // Adds 8dp border radius
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF7F7A), // Corrected syntax
                                disabledContainerColor = Color(0xFF9C9C9C)
                            )
                        ) {
                            Text(
                                text = "Reject",
                                color = Color.Black,
                                maxLines = 1
                            ) // Button text
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
