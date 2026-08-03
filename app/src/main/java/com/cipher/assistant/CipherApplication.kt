package com.cipher.assistant

import android.app.Application
import com.cipher.assistant.data.CipherPreferences
import com.cipher.assistant.notification.CipherNotificationManager
import com.cipher.assistant.util.CipherLogger
import com.cipher.assistant.util.LogLevel

class CipherApplication : Application() {

    lateinit var preferences: CipherPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        CipherLogger.log(TAG, "Cipher Application initializing...", LogLevel.INFO)

        setupUncaughtExceptionHandler()

        preferences = CipherPreferences(applicationContext)
        CipherNotificationManager.createNotificationChannel(applicationContext)

        CipherLogger.log(TAG, "Cipher Application initialized successfully.", LogLevel.INFO)
    }

    private fun setupUncaughtExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            CipherLogger.log(
                TAG,
                "UNCAUGHT EXCEPTION on thread ${thread.name}: ${throwable.localizedMessage}\n${throwable.stackTraceToString()}",
                LogLevel.ERROR
            )
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        private const val TAG = "CipherApplication"
        lateinit var instance: CipherApplication
            private set
    }
}
