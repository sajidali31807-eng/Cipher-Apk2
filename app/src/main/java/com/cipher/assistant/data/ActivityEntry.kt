package com.cipher.assistant.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActivityEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val action: String,
    val detail: String,
    val success: Boolean
) {
    fun formattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
