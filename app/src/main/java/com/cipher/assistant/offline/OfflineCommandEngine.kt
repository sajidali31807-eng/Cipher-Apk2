package com.cipher.assistant.offline

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log

class OfflineCommandEngine(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun processCommand(commandText: String): String {
        val lowerText = commandText.lowercase().trim()
        Log.i(TAG, "Processing offline voice command: '$lowerText'")

        return when {
            // Flashlight commands
            lowerText.contains("flashlight on") || lowerText.contains("torch on") || lowerText.contains("turn on flashlight") -> {
                setTorchMode(true)
            }
            lowerText.contains("flashlight off") || lowerText.contains("torch off") || lowerText.contains("turn off flashlight") -> {
                setTorchMode(false)
            }

            // Volume commands
            lowerText.contains("volume up") || lowerText.contains("increase volume") -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                "Volume increased."
            }
            lowerText.contains("volume down") || lowerText.contains("decrease volume") -> {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                "Volume decreased."
            }
            lowerText.contains("mute") || lowerText.contains("volume mute") || lowerText.contains("silent mode") -> {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI)
                "Audio muted."
            }
            lowerText.contains("volume max") || lowerText.contains("maximum volume") -> {
                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, AudioManager.FLAG_SHOW_UI)
                "Volume set to maximum."
            }

            // Battery level command
            lowerText.contains("battery") || lowerText.contains("power level") -> {
                getBatteryLevelResponse()
            }

            // Brightness commands
            lowerText.contains("brightness up") || lowerText.contains("increase brightness") -> {
                adjustBrightness(delta = 40)
            }
            lowerText.contains("brightness down") || lowerText.contains("decrease brightness") -> {
                adjustBrightness(delta = -40)
            }

            // Do Not Disturb
            lowerText.contains("do not disturb on") || lowerText.contains("enable dnd") -> {
                toggleDoNotDisturb(true)
            }
            lowerText.contains("do not disturb off") || lowerText.contains("disable dnd") -> {
                toggleDoNotDisturb(false)
            }

            // Airplane mode
            lowerText.contains("airplane mode") || lowerText.contains("flight mode") -> {
                "Please toggle Airplane Mode directly from quick settings."
            }

            else -> {
                "Command not recognized offline. Please connect to the internet for full AI capabilities."
            }
        }
    }

    private fun setTorchMode(enabled: Boolean): String {
        return try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return "No camera flashlight found."
            cameraManager.setTorchMode(cameraId, enabled)
            if (enabled) "Flashlight turned on." else "Flashlight turned off."
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle flashlight offline", e)
            "Unable to control flashlight."
        }
    }

    private fun getBatteryLevelResponse(): String {
        return try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, filter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            if (level != -1 && scale != -1) {
                val percentage = (level * 100 / scale.toFloat()).toInt()
                "Your battery level is $percentage percent."
            } else {
                "Unable to read battery level."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking battery level offline", e)
            "Error reading battery status."
        }
    }

    private fun adjustBrightness(delta: Int): String {
        return try {
            if (Settings.System.canWrite(context)) {
                val curBrightness = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    128
                )
                val newBrightness = (curBrightness + delta).coerceIn(10, 255)
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    newBrightness
                )
                "Screen brightness adjusted."
            } else {
                "Permission needed to modify system settings."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error modifying brightness", e)
            "Unable to adjust brightness."
        }
    }

    private fun toggleDoNotDisturb(enable: Boolean): String {
        return try {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                val filter = if (enable) NotificationManager.INTERRUPTION_FILTER_NONE else NotificationManager.INTERRUPTION_FILTER_ALL
                notificationManager.setInterruptionFilter(filter)
                if (enable) "Do Not Disturb enabled." else "Do Not Disturb disabled."
            } else {
                "Notification Policy access required for Do Not Disturb."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling DND", e)
            "Failed to change Do Not Disturb mode."
        }
    }

    companion object {
        private const val TAG = "OfflineCommandEngine"
    }
}
