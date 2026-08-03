package com.cipher.assistant.controllers

import android.content.Context
import android.util.Log
import com.cipher.assistant.service.CipherNotificationListenerService

class NotificationController(private val context: Context) {

    fun readAllNotifications(): String {
        val listener = CipherNotificationListenerService.instance
        if (listener == null || !CipherNotificationListenerService.isRunning) {
            return "Notification Listener Service is not enabled or running. Please grant notification access in settings."
        }

        val notifications = listener.getActiveNotificationsList()
        if (notifications.isEmpty()) {
            return "No active notifications present."
        }

        val sb = StringBuilder("Active Notifications (${notifications.size}):\n")
        notifications.forEachIndexed { index, notif ->
            sb.append("${index + 1}. [${notif.packageName}] ${notif.title}: ${notif.text}\n")
        }
        return sb.toString()
    }

    fun readNotificationsFromApp(appName: String): String {
        val listener = CipherNotificationListenerService.instance
        if (listener == null || !CipherNotificationListenerService.isRunning) {
            return "Notification Listener Service is not active."
        }

        val notifications = listener.getNotificationsByApp(appName)
        if (notifications.isEmpty()) {
            return "No active notifications found for '$appName'."
        }

        val sb = StringBuilder("Notifications for $appName (${notifications.size}):\n")
        notifications.forEachIndexed { index, notif ->
            sb.append("${index + 1}. ${notif.title}: ${notif.text}\n")
        }
        return sb.toString()
    }

    fun replyToNotification(appName: String, message: String): Boolean {
        val listener = CipherNotificationListenerService.instance ?: return false
        return listener.replyToNotification(appName, message)
    }

    fun dismissNotification(appName: String): Boolean {
        val listener = CipherNotificationListenerService.instance ?: return false
        val targetPkg = appName.lowercase()
        return try {
            val activeNotifs = listener.activeNotifications ?: return false
            var dismissedAny = false
            for (sbn in activeNotifs) {
                val pkg = sbn.packageName ?: continue
                if (pkg.lowercase().contains(targetPkg)) {
                    listener.cancelNotification(sbn.key)
                    dismissedAny = true
                }
            }
            dismissedAny
        } catch (e: Exception) {
            Log.e(TAG, "Failed to dismiss notification for $appName", e)
            false
        }
    }

    companion object {
        private const val TAG = "NotificationController"
    }
}
