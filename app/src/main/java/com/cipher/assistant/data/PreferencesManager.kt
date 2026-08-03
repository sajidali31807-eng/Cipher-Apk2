package com.cipher.assistant.data

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PreferencesManager(context: Context) {
    private val cipherPreferences = CipherPreferences(context)

    fun getApiKey(): String {
        return runBlocking { cipherPreferences.apiKeyFlow.first() }
    }

    suspend fun setApiKey(key: String) {
        cipherPreferences.setGeminiApiKey(key)
    }
}
