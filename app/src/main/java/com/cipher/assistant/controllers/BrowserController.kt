package com.cipher.assistant.controllers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.cipher.assistant.accessibility.CipherAccessibilityService

class BrowserController(private val context: Context) {

    fun openUrl(url: String): Boolean {
        return try {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open URL $url", e)
            false
        }
    }

    fun searchGoogle(query: String): Boolean {
        return try {
            val encodedQuery = Uri.encode(query)
            val searchUrl = "https://www.google.com/search?q=$encodedQuery"
            openUrl(searchUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute search query $query", e)
            false
        }
    }

    fun readPageContent(): String {
        val accessibility = CipherAccessibilityService.instance
        if (accessibility == null || !CipherAccessibilityService.isRunning) {
            return "Accessibility service is not active. Unable to read page content."
        }

        val screenContent = accessibility.readCurrentScreen()
        val pageText = screenContent.allTexts.joinToString(separator = "\n")

        return if (pageText.isNotBlank()) {
            pageText
        } else {
            "No readable text content found on active browser screen."
        }
    }

    fun clickLink(linkText: String): Boolean {
        val accessibility = CipherAccessibilityService.instance ?: return false
        val node = accessibility.findElementByText(linkText) ?: return false
        return accessibility.clickElement(node)
    }

    fun scrollPageDown(): Boolean {
        val accessibility = CipherAccessibilityService.instance ?: return false
        return accessibility.scrollDown()
    }

    fun scrollPageUp(): Boolean {
        val accessibility = CipherAccessibilityService.instance ?: return false
        return accessibility.scrollUp()
    }

    fun goBack(): Boolean {
        val accessibility = CipherAccessibilityService.instance ?: return false
        return accessibility.pressBack()
    }

    companion object {
        private const val TAG = "BrowserController"
    }
}
