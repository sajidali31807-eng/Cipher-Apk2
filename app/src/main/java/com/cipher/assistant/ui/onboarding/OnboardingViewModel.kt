package com.cipher.assistant.ui.onboarding

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cipher.assistant.CipherApplication
import com.cipher.assistant.util.PermissionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val isRecordAudioGranted: Boolean = false,
    val isAccessibilityGranted: Boolean = false,
    val isNotificationListenerGranted: Boolean = false,
    val isOverlayGranted: Boolean = false,
    val isBatteryOptimizationDisabled: Boolean = false,
    val isVivoAutostartGranted: Boolean = false,
    val isVivoHighPowerGranted: Boolean = false,
    val isAllMandatoryGranted: Boolean = false
)

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
    private val preferences = (getApplication<Application>() as CipherApplication).preferences

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkAllPermissions()
    }

    fun checkAllPermissions() {
        val recordAudio = PermissionUtils.isRecordAudioGranted(context)
        val accessibility = PermissionUtils.isAccessibilityServiceEnabled(context)
        val notification = PermissionUtils.isNotificationListenerEnabled(context)
        val overlay = PermissionUtils.isOverlayPermissionGranted(context)
        val battery = PermissionUtils.isBatteryOptimizationDisabled(context)
        val vivoAutostart = PermissionUtils.isVivoAutostartEnabled(context)

        val mandatoryGranted = recordAudio && accessibility && notification && overlay && battery

        _uiState.value = _uiState.value.copy(
            isRecordAudioGranted = recordAudio,
            isAccessibilityGranted = accessibility,
            isNotificationListenerGranted = notification,
            isOverlayGranted = overlay,
            isBatteryOptimizationDisabled = battery,
            isVivoAutostartGranted = vivoAutostart,
            isAllMandatoryGranted = mandatoryGranted
        )
    }

    fun nextStep() {
        if (_uiState.value.currentStep < 8) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferences.setOnboardingComplete(true)
            onSuccess()
        }
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openNotificationListenerSettings() {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openOverlaySettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openBatteryOptimizationSettings() {
        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openVivoAutostartSettings() {
        PermissionUtils.openVivoAutostartSettings(context)
    }

    fun openVivoHighPowerSettings() {
        PermissionUtils.openVivoHighPowerSettings(context)
    }
}
