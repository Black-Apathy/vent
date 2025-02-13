package com.example.vent.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDate(inputDate: String): String {
        return try {
            // ISO 8601 input format (from your crash logs)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Parse in UTC

            // Desired output format
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            val date: Date = inputFormat.parse(inputDate) ?: return "Invalid Date"
            outputFormat.format(date) // Convert to readable format
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid Date"
        }
    }
}

