package com.example.vent

import Model
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vent.utils.AnimationUtils
import com.example.vent.utils.TimeUtils.formatTime
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun ViewEventsScreen(
    events: List<Model>,
    onEventClick: (Model) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Filter events based on the search bar
    val filteredEvents = events.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.type.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5E1)) // Your CreamBG
            .padding(16.dp)
    ) {
        // 1. Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            placeholder = { Text("Search events...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF003366), // BlueMain
                unfocusedBorderColor = Color.Gray
            )
        )

        // 2. Events List
        if (filteredEvents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No events found", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredEvents) { event ->
                    EventCard(event = event, onClick = { onEventClick(event) })
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Model, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Event Name
                Text(
                    text = event.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF003366), // BlueMain
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    maxLines = 2, // Wraps long names beautifully
                    overflow = TextOverflow.Ellipsis
                )

                // Type Badge
                Surface(
                    color = Color(0xFFFF6600).copy(alpha = 0.1f), // OrangeAccent
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = event.type,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFFFF6600),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date and Time Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                val prettyDate = formatIsoDate(event.startDate)
                val prettyStartTime = formatTime(event.startTime)
                val prettyEndTime = formatTime(event.endTime)

                // Prints: Jun 10, 2026 • 10:00 AM - 1:00 PM
                Text(text = "$prettyDate • $prettyStartTime - $prettyEndTime", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Participants Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${event.participants} Participants", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

fun formatIsoDate(isoString: String): String {
    return try {
        // Read the MariaDB format
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(isoString)

        // Output the beautiful format
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        date?.let { formatter.format(it) } ?: isoString
    } catch (e: Exception) {
        // Fallback just in case the server sends something weird
        isoString
    }
}