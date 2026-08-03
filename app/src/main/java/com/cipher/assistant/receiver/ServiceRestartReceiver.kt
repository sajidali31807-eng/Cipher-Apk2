package com.cipher.assistant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.cipher.assistant.util.CipherLogger
import com.cipher.assistant.util.LogLevel
import com.cipher.assistant.util.ServiceUtils

class ServiceRestartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        CipherLogger.log(TAG, "ServiceRestartReceiver triggered. Checking service status in 1 second...", LogLevel.WARNING)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!ServiceUtils.isCipherServiceRunning(context)) {
                CipherLogger.log(TAG, "Service is stopped. Restarting CipherForegroundService now.", LogLevel.INFO)
                ServiceUtils.startCipherService(context)
            } else {
                CipherLogger.log(TAG, "CipherForegroundService is already running. Skipping duplicate restart.", LogLevel.INFO)
            }
        }, 1000L)
    }

    companion object {
        private const val TAG = "ServiceRestartReceiver"
    }
}
