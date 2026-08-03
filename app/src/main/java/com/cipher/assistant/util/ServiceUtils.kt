package com.cipher.assistant.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.cipher.assistant.service.CipherForegroundService

object ServiceUtils {

    fun isCipherServiceRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (CipherForegroundService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun startCipherService(context: Context) {
        val intent = Intent(context, CipherForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopCipherService(context: Context) {
        val intent = Intent(context, CipherForegroundService::class.java)
        context.stopService(intent)
    }
}
