package com.cipher.assistant.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.cipher.assistant.gemini.GeminiLiveClient
import com.cipher.assistant.notification.CipherNotificationManager
import com.cipher.assistant.offline.ConnectivityChecker
import com.cipher.assistant.offline.OfflineCommandEngine
import com.cipher.assistant.util.CipherLogger
import com.cipher.assistant.util.LogLevel
import com.cipher.assistant.util.MemoryManager
import com.cipher.assistant.wakeword.WakeWordEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CipherForegroundService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var wakeLock: PowerManager.WakeLock? = null
    private var wakeWordEngine: WakeWordEngine? = null
    private var geminiLiveClient: GeminiLiveClient? = null
    private lateinit var connectivityChecker: ConnectivityChecker
    private lateinit var offlineCommandEngine: OfflineCommandEngine

    @Volatile
    private var isStreamingToGemini = false

    override fun onCreate() {
        super.onCreate()
        CipherLogger.log(TAG, "CipherForegroundService onCreate initializing...", LogLevel.INFO)

        acquireWakeLock()
        connectivityChecker = ConnectivityChecker(applicationContext)
        offlineCommandEngine = OfflineCommandEngine(applicationContext)
        geminiLiveClient = GeminiLiveClient(applicationContext)

        MemoryManager.startMemoryMonitor(applicationContext, serviceScope)
        ServiceWatchdog.startWatching(applicationContext, serviceScope)

        wakeWordEngine = WakeWordEngine(
            context = applicationContext,
            onWakeWordDetected = {
                onWakeWordTriggered()
            },
            onAudioChunkReceived = { pcmShorts ->
                if (isStreamingToGemini && connectivityChecker.isInternetAvailable()) {
                    val pcmBytes = ByteArray(pcmShorts.size * 2)
                    for (i in pcmShorts.indices) {
                        val value = pcmShorts[i].toInt()
                        pcmBytes[i * 2] = (value and 0x00FF).toByte()
                        pcmBytes[i * 2 + 1] = ((value shr 8) and 0x00FF).toByte()
                    }
                    geminiLiveClient?.sendAudioChunk(pcmBytes)
                }
            }
        )

        _serviceState.value = ServiceState.ListeningForWakeWord
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            CipherLogger.log(TAG, "Stop service requested via Intent", LogLevel.INFO)
            FloatingOrbService.hide(applicationContext)
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = CipherNotificationManager.buildNotification(applicationContext)
        startForeground(CipherNotificationManager.NOTIFICATION_ID, notification)

        CipherLogger.log(TAG, "Starting Sherpa-ONNX wake word engine...", LogLevel.INFO)
        wakeWordEngine?.startListening()
        FloatingOrbService.showListening(applicationContext)

        return START_STICKY
    }

    private fun onWakeWordTriggered() {
        CipherLogger.log(TAG, "WAKE WORD DETECTED!", LogLevel.INFO)
        _serviceState.value = ServiceState.WakeWordDetected
        FloatingOrbService.showProcessing(applicationContext)

        serviceScope.launch {
            if (connectivityChecker.isInternetAvailable()) {
                CipherLogger.log(TAG, "Internet available. Connecting to Gemini Live API...", LogLevel.INFO)
                geminiLiveClient?.connect()
                isStreamingToGemini = true
                _serviceState.value = ServiceState.ProcessingCommand
            } else {
                CipherLogger.log(TAG, "No internet connectivity. Switching to OfflineCommandEngine fallback...", LogLevel.WARNING)
                isStreamingToGemini = false
                val offlineResponse = offlineCommandEngine.processCommand("battery")
                CipherLogger.log(TAG, "Offline response: $offlineResponse", LogLevel.INFO)
                _serviceState.value = ServiceState.ListeningForWakeWord
                FloatingOrbService.showListening(applicationContext)
            }
        }
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "CipherAssistant::ForegroundServiceWakeLock"
        ).apply {
            acquire(10 * 60 * 1000L /*10 mins fallback*/)
        }
        CipherLogger.log(TAG, "WakeLock acquired.", LogLevel.DEBUG)
    }

    override fun onDestroy() {
        CipherLogger.log(TAG, "CipherForegroundService onDestroy triggered. Cleaning resources & broadcasting restart.", LogLevel.WARNING)

        isStreamingToGemini = false
        wakeWordEngine?.stopListening()
        geminiLiveClient?.disconnect()
        FloatingOrbService.hide(applicationContext)
        ServiceWatchdog.stopWatching()

        wakeLock?.let {
            if (it.isHeld) it.release()
        }

        serviceJob.cancel()

        _serviceState.value = ServiceState.Idle

        val broadcastIntent = Intent("com.cipher.assistant.RESTART_SERVICE").apply {
            setPackage(packageName)
        }
        sendBroadcast(broadcastIntent)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "CipherForegroundService"
        const val ACTION_STOP_SERVICE = "com.cipher.assistant.ACTION_STOP_SERVICE"

        private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Idle)
        val serviceState: StateFlow<ServiceState> = _serviceState.asStateFlow()
    }

    sealed class ServiceState {
        object Idle : ServiceState()
        object ListeningForWakeWord : ServiceState()
        object WakeWordDetected : ServiceState()
        object ProcessingCommand : ServiceState()
    }
}
