package com.cipher.assistant.gemini

import kotlin.random.Random

object VoiceResponseFormatter {

    private val GREETINGS = listOf(
        "Haan, batao?",
        "Yes, how can I help?",
        "Cipher ready.",
        "Haan ji, boliye?",
        "Listening, tell me!",
        "Haan, kya karna hai?",
        "At your service, sir.",
        "Haan, bolo!"
    )

    fun getRandomGreeting(): String {
        return GREETINGS[Random.nextInt(GREETINGS.size)]
    }

    fun formatActionSuccess(action: String, detail: String): String {
        return if (detail.isBlank()) {
            "$action completed successfully."
        } else {
            "$action done: $detail"
        }
    }

    fun formatActionFailure(action: String, reason: String): String {
        return if (reason.isBlank()) {
            "Could not complete $action."
        } else {
            "Could not complete $action because: $reason. Should I try another way?"
        }
    }

    fun formatScreenReading(screenContent: String): String {
        if (screenContent.isBlank()) {
            return "Screen is currently empty or cannot be read."
        }
        val lines = screenContent.lineSequence().filter { it.isNotBlank() }.take(5).toList()
        return "On screen: " + lines.joinToString(", ")
    }

    fun formatNotificationSummary(notifications: List<String>): String {
        if (notifications.isEmpty()) {
            return "Aapke paas koi nayi notification nahi hai."
        }
        val count = notifications.size
        val items = notifications.take(3).joinToString("; ")
        return "You have $count notifications. First few: $items"
    }
}
