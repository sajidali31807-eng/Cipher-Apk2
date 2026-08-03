package com.cipher.assistant.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cipher.assistant.data.ActivityEntry
import com.cipher.assistant.service.CipherForegroundService
import com.cipher.assistant.service.FloatingOrbService
import com.cipher.assistant.service.CipherForegroundService.ServiceState
import com.cipher.assistant.util.CipherLogger
import com.cipher.assistant.util.LogLevel
import com.cipher.assistant.util.ServiceUtils

data class DashboardUiState(
    val isServiceRunning: Boolean = false,
    val serviceState: ServiceState = ServiceState.Idle,
    val isOrbVisible: Boolean = true,
    val wakeWordActive: Boolean = false,
    val recentActivities: List<ActivityEntry> = emptyList(),
    val logs: List<String> = emptyList()
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshStatus()
        observeServiceState()
        loadInitialActivities()
    }

    fun refreshStatus() {
        val isRunning = ServiceUtils.isCipherServiceRunning(context)
        val recentLogs = CipherLogger.getRecentLogs(20).map { "[${it.formattedTime()}] ${it.tag}: ${it.message}" }
        _uiState.value = _uiState.value.copy(
            isServiceRunning = isRunning,
            wakeWordActive = isRunning,
            logs = recentLogs
        )
    }

    private fun observeServiceState() {
        viewModelScope.launch {
            CipherForegroundService.serviceState.collect { state ->
                val isRunning = state !is ServiceState.Idle
                _uiState.value = _uiState.value.copy(
                    serviceState = state,
                    isServiceRunning = isRunning
                )
                refreshStatus()
            }
        }
    }

    fun toggleCipherService() {
        if (_uiState.value.isServiceRunning) {
            ServiceUtils.stopCipherService(context)
            FloatingOrbService.hide(context)
            addActivity("Engine Stopped", "User manually stopped Cipher Foreground Service", true)
            CipherLogger.log("Dashboard", "Cipher Foreground Service stopped manually", LogLevel.INFO)
        } else {
            ServiceUtils.startCipherService(context)
            FloatingOrbService.showListening(context)
            addActivity("Engine Started", "User manually started Cipher Foreground Service", true)
            CipherLogger.log("Dashboard", "Cipher Foreground Service started manually", LogLevel.INFO)
        }
        refreshStatus()
    }

    fun toggleFloatingOrb() {
        val newOrbState = !_uiState.value.isOrbVisible
        _uiState.value = _uiState.value.copy(isOrbVisible = newOrbState)
        if (newOrbState) {
            FloatingOrbService.showListening(context)
            addActivity("Floating Orb", "Visual orb overlay shown", true)
        } else {
            FloatingOrbService.hide(context)
            addActivity("Floating Orb", "Visual orb overlay hidden", true)
        }
    }

    fun addActivity(action: String, detail: String, success: Boolean) {
        val entry = ActivityEntry(action = action, detail = detail, success = success)
        val current = _uiState.value.recentActivities.toMutableList()
        current.add(0, entry)
        if (current.size > 20) current.removeAt(current.size - 1)
        _uiState.value = _uiState.value.copy(recentActivities = current)
    }

    private fun loadInitialActivities() {
        val initialList = listOf(
            ActivityEntry(action = "System Boot", detail = "Cipher Assistant Phase 19 Engine Initialized", success = true),
            ActivityEntry(action = "Wake Word Model", detail = "Sherpa-ONNX 'Get Ready Cipher' model ready", success = true),
            ActivityEntry(action = "Gemini Live API", detail = "Bidi WebSocket Client primed", success = true)
        )
        _uiState.value = _uiState.value.copy(recentActivities = initialList)
    }
}
