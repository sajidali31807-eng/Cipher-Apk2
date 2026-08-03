package com.cipher.assistant.ui.settings

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cipher.assistant.CipherApplication
import com.cipher.assistant.accessibility.CipherAccessibilityService
import com.cipher.assistant.gemini.ConnectionTestResult
import com.cipher.assistant.service.CipherForegroundService
import com.cipher.assistant.service.CipherNotificationListenerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class SettingsUiState(
    val apiKey: String = "",
    val wakeWordLabel: String = "Get Ready Cipher",
    val languageMode: String = "Auto Detect (Hindi / English)",
    val voiceSpeed: String = "Normal",
    val isSaved: Boolean = false,
    val connectionTestResult: ConnectionTestResult? = null,
    val isTestingConnection: Boolean = false,
    val serviceRunning: Boolean = false,
    val accessibilityEnabled: Boolean = false,
    val notificationListenerEnabled: Boolean = false,
    val permissionStatuses: Map<String, Boolean> = emptyMap()
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = (getApplication<Application>() as CipherApplication).preferences

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.apiKeyFlow.collect { key ->
                _uiState.value = _uiState.value.copy(apiKey = key)
            }
        }
        refreshStatus()
    }

    fun refreshStatus() {
        val context = getApplication<Application>()
        val permissions = getAllPermissionStatuses()
        val isServiceRunning = CipherForegroundService.serviceState.value != CipherForegroundService.ServiceState.Idle
        val isAccessibilityActive = CipherAccessibilityService.isRunning
        val isNotifActive = CipherNotificationListenerService.isRunning

        _uiState.value = _uiState.value.copy(
            permissionStatuses = permissions,
            serviceRunning = isServiceRunning,
            accessibilityEnabled = isAccessibilityActive,
            notificationListenerEnabled = isNotifActive
        )
    }

    fun updateApiKey(key: String) {
        _uiState.value = _uiState.value.copy(apiKey = key, isSaved = false)
    }

    fun saveApiKey(key: String = _uiState.value.apiKey) {
        viewModelScope.launch {
            preferences.setGeminiApiKey(key)
            _uiState.value = _uiState.value.copy(apiKey = key, isSaved = true)
        }
    }

    suspend fun loadApiKey(): String {
        return _uiState.value.apiKey
    }

    fun setLanguageMode(mode: String) {
        _uiState.value = _uiState.value.copy(languageMode = mode)
    }

    fun setVoiceSpeed(speed: String) {
        _uiState.value = _uiState.value.copy(voiceSpeed = speed)
    }

    fun testGeminiConnection(): Flow<ConnectionTestResult> = flow {
        _uiState.value = _uiState.value.copy(isTestingConnection = true, connectionTestResult = null)
        val key = _uiState.value.apiKey
        if (key.isBlank()) {
            val result = ConnectionTestResult.Error("API Key is missing")
            _uiState.value = _uiState.value.copy(isTestingConnection = false, connectionTestResult = result)
            emit(result)
            return@flow
        }

        val startTime = System.currentTimeMillis()
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models?key=$key")
                .build()

            val response = client.newCall(request).execute()
            val latency = System.currentTimeMillis() - startTime

            if (response.isSuccessful) {
                val result = ConnectionTestResult.Success(latency)
                _uiState.value = _uiState.value.copy(isTestingConnection = false, connectionTestResult = result)
                emit(result)
            } else {
                val result = ConnectionTestResult.Error("HTTP ${response.code}: ${response.message}")
                _uiState.value = _uiState.value.copy(isTestingConnection = false, connectionTestResult = result)
                emit(result)
            }
        } catch (e: Exception) {
            val result = ConnectionTestResult.Error(e.localizedMessage ?: "Network error")
            _uiState.value = _uiState.value.copy(isTestingConnection = false, connectionTestResult = result)
            emit(result)
        }
    }.flowOn(Dispatchers.IO)

    fun getAllPermissionStatuses(): Map<String, Boolean> {
        val context = getApplication<Application>()
        return mapOf(
            "Microphone" to (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED),
            "Accessibility" to CipherAccessibilityService.isRunning,
            "Overlay Display" to Settings.canDrawOverlays(context),
            "Notifications" to CipherNotificationListenerService.isRunning,
            "Contacts" to (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED),
            "Phone Call" to (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED),
            "SMS Messaging" to (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED),
            "Storage & Media" to (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || android.os.Build.VERSION.SDK_INT >= 33)
        )
    }
}
