package com.cipher.assistant.service

import android.content.Context
import com.cipher.assistant.accessibility.CipherAccessibilityService
import com.cipher.assistant.notification.CipherNotificationManager
import com.cipher.assistant.util.CipherLogger
import com.cipher.assistant.util.LogLevel
import com.cipher.assistant.util.ServiceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object ServiceWatchdog {

    private const val TAG = "ServiceWatchdog"
    private var watchdogJob: Job? = null

    fun startWatching(context: Context, scope: CoroutineScope) {
        stopWatching()
        CipherLogger.log(TAG, "Starting Cipher Service Watchdog loop (30s interval)", LogLevel.INFO)

        watchdogJob = scope.launch {
            while (isActive) {
                delay(30_000L)

                val isForegroundServiceRunning = ServiceUtils.isCipherServiceRunning(context)
                val isAccessibilityActive = CipherAccessibilityService.isRunning

                if (!isForegroundServiceRunning) {
                    CipherLogger.log(TAG, "Watchdog Alert: CipherForegroundService is NOT running! Restarting immediately.", LogLevel.WARNING)
                    ServiceUtils.startCipherService(context)
                } else {
                    CipherLogger.log(TAG, "Watchdog Check: CipherForegroundService is active and healthy.", LogLevel.DEBUG)
                }

                if (!isAccessibilityActive) {
                    CipherLogger.log(
                        TAG,
                        "Watchdog Warning: CipherAccessibilityService is disabled in settings.",
                        LogLevel.WARNING
                    )
                }
            }
        }
    }

    fun stopWatching() {
        watchdogJob?.cancel()
        watchdogJob = null
        CipherLogger.log(TAG, "Stopped Cipher Service Watchdog", LogLevel.INFO)
    }
}
