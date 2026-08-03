package com.cipher.assistant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.cipher.assistant.service.FloatingOrbService
import com.cipher.assistant.util.CipherLogger
import com.cipher.assistant.util.LogLevel
import com.cipher.assistant.util.ServiceUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        CipherLogger.log(TAG, "BootReceiver triggered with action: $action", LogLevel.INFO)

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            CipherLogger.log(TAG, "Device booted / package replaced. Waiting 3 seconds to launch Cipher background services...", LogLevel.INFO)

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    ServiceUtils.startCipherService(context)
                    FloatingOrbService.showListening(context)
                    CipherLogger.log(TAG, "Successfully started CipherForegroundService and FloatingOrbService on boot.", LogLevel.INFO)
                } catch (e: Exception) {
                    CipherLogger.log(TAG, "Error launching Cipher services on boot: ${e.message}", LogLevel.ERROR)
                }
            }, 3000L)
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
