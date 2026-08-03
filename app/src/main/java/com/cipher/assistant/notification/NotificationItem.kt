package com.cipher.assistant.notification

data class NotificationItem(
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long
)
