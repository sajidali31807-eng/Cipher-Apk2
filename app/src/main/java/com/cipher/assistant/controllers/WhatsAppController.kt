package com.cipher.assistant.controllers

import android.content.Context
import android.util.Log
import com.cipher.assistant.accessibility.CipherAccessibilityService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WhatsAppController(private val context: Context) {

    private val appController = AppController(context)

    fun openWhatsApp(): Boolean {
        Log.i(TAG, "Opening WhatsApp...")
        return appController.openApp("WhatsApp")
    }

    fun openChat(contactName: String): Boolean {
        if (!openWhatsApp()) return false

        // Give WhatsApp time to open
        runBlocking { delay(1500) }

        val accessibility = CipherAccessibilityService.instance ?: return false

        // Search for contact/chat in WhatsApp
        val searchNode = accessibility.findElementByDescription("Search")
            ?: accessibility.findElementByText("Search")

        if (searchNode != null) {
            accessibility.clickElement(searchNode)
            runBlocking { delay(500) }

            accessibility.typeTextInFocused(contactName)
            runBlocking { delay(1000) }

            // Click matching contact
            val resultNode = accessibility.findElementByText(contactName)
            if (resultNode != null) {
                accessibility.clickElement(resultNode)
                runBlocking { delay(1000) }
                return true
            }
        }
        return false
    }

    fun sendMessage(contactName: String, message: String): Boolean {
        if (!openChat(contactName)) return false

        val accessibility = CipherAccessibilityService.instance ?: return false

        // Find input box in chat
        val inputNode = accessibility.findElementByText("Type a message")
            ?: accessibility.findElementByText("Message")

        if (inputNode != null) {
            accessibility.typeText(inputNode, message)
            runBlocking { delay(500) }

            // Find send button
            val sendButton = accessibility.findElementByDescription("Send")
                ?: accessibility.findElementByText("Send")

            if (sendButton != null) {
                return accessibility.clickElement(sendButton)
            }
        }
        return false
    }

    fun readLastMessages(contactName: String, count: Int = 5): List<String> {
        if (!openChat(contactName)) return emptyList()

        val accessibility = CipherAccessibilityService.instance ?: return emptyList()
        val screenContent = accessibility.readCurrentScreen()

        val messages = screenContent.allTexts.filter { text ->
            text.isNotBlank() &&
                    !text.equals("WhatsApp", ignoreCase = true) &&
                    !text.equals(contactName, ignoreCase = true) &&
                    !text.equals("Type a message", ignoreCase = true) &&
                    !text.equals("Search", ignoreCase = true)
        }

        return messages.takeLast(count)
    }

    fun replyToLastMessage(message: String): Boolean {
        val accessibility = CipherAccessibilityService.instance ?: return false

        val inputNode = accessibility.findElementByText("Type a message")
            ?: accessibility.findElementByText("Message")

        if (inputNode != null) {
            accessibility.typeText(inputNode, message)
            runBlocking { delay(500) }

            val sendButton = accessibility.findElementByDescription("Send")
                ?: accessibility.findElementByText("Send")

            if (sendButton != null) {
                return accessibility.clickElement(sendButton)
            }
        }
        return false
    }

    companion object {
        private const val TAG = "WhatsAppController"
    }
}
