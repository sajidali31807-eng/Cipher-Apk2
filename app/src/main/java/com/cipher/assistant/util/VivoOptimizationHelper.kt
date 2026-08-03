package com.cipher.assistant.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log

object VivoOptimizationHelper {

    private const val TAG = "VivoOptimization"

    fun isVivoDevice(): Boolean {
        return Build.MANUFACTURER.contains("vivo", ignoreCase = true) ||
                Build.MANUFACTURER.contains("iqoo", ignoreCase = true)
    }

    fun openVivoAutostartSettings(context: Context) {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e1: Exception) {
            Log.w(TAG, "Primary Vivo autostart intent failed, trying fallback 1", e1)
            try {
                val fallback1 = Intent().apply {
                    component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.PurviewTabActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback1)
            } catch (e2: Exception) {
                Log.w(TAG, "Fallback 1 failed, opening App Details Settings", e2)
                try {
                    val appDetails = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(appDetails)
                } catch (e3: Exception) {
                    Log.e(TAG, "All Vivo autostart intents failed", e3)
                }
            }
        }
    }

    fun openVivoBatteryOptimizationSettings(context: Context) {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.HighPowerManagerActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "Vivo HighPowerManager intent failed, trying system battery settings", e)
            try {
                val systemBatteryIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(systemBatteryIntent)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to open battery optimization settings", e2)
            }
        }
    }

    fun requestIgnoreBatteryOptimization(context: Context) {
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = context.packageName

            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request battery optimization ignore", e)
        }
    }
}
