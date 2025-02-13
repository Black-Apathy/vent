package com.example.vent.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun formatTime(inputTime: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // 24-hour format input
        val date: Date = inputFormat.parse(inputTime) ?: return "Invalid Time"

        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format with AM/PM
        return outputFormat.format(date)
    }
}
