package com.cipher.assistant.util

import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object MemoryManager {

    private const val TAG = "MemoryManager"
    private const val LOW_MEMORY_THRESHOLD_MB = 150L

    fun getAvailableMemoryMB(context: Context): Long {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            memoryInfo.availMem / (1024 * 1024)
        } catch (e: Exception) {
            CipherLogger.log(TAG, "Failed to inspect available memory: ${e.message}", LogLevel.ERROR)
            500L
        }
    }

    fun isLowMemory(context: Context): Boolean {
        return getAvailableMemoryMB(context) < LOW_MEMORY_THRESHOLD_MB
    }

    fun trimCipherMemory() {
        CipherLogger.log(TAG, "Trimming Cipher memory allocations...", LogLevel.INFO)
        System.gc()
    }

    fun startMemoryMonitor(context: Context, scope: CoroutineScope) {
        scope.launch {
            while (isActive) {
                val freeMb = getAvailableMemoryMB(context)
                if (freeMb < LOW_MEMORY_THRESHOLD_MB) {
                    CipherLogger.log(
                        TAG,
                        "Low RAM detected: ${freeMb}MB remaining. Triggering memory trim.",
                        LogLevel.WARNING
                    )
                    trimCipherMemory()
                } else {
                    CipherLogger.log(TAG, "Memory check normal: ${freeMb}MB available.", LogLevel.DEBUG)
                }
                delay(60_000L) // Check every 60 seconds
            }
        }
    }
}
