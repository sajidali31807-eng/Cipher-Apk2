package com.cipher.assistant.service

import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.cipher.assistant.notification.NotificationItem
import java.util.Collections

class CipherNotificationListenerService : NotificationListenerService() {

    private val activeNotificationsList = Collections.synchronizedList(mutableListOf<NotificationItem>())

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        isRunning = true
        Log.i(TAG, "CipherNotificationListenerService connected.")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        try {
            val extras = sbn.notification.extras
            val title = extras.getCharSequence("android.title")?.toString() ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""
            val pkg = sbn.packageName ?: ""
            val postTime = sbn.postTime

            if (title.isNotEmpty() || text.isNotEmpty()) {
                val item = NotificationItem(
                    packageName = pkg,
                    title = title,
                    text = text,
                    timestamp = postTime
                )

                synchronized(activeNotificationsList) {
                    activeNotificationsList.add(0, item)
                    if (activeNotificationsList.size > 50) {
                        activeNotificationsList.removeAt(activeNotificationsList.size - 1)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing posted notification", e)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn ?: return

        val pkg = sbn.packageName ?: return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString() ?: ""

        synchronized(activeNotificationsList) {
            activeNotificationsList.removeAll { it.packageName == pkg && (title.isEmpty() || it.title == title) }
        }
    }

    override fun onDestroy() {
        if (instance == this) {
            instance = null
            isRunning = false
        }
        super.onDestroy()
    }

    fun getActiveNotificationsList(): List<NotificationItem> {
        return synchronized(activeNotificationsList) {
            activeNotificationsList.toList()
        }
    }

    fun getNotificationsByApp(packageName: String): List<NotificationItem> {
        val targetPkg = packageName.lowercase()
        return synchronized(activeNotificationsList) {
            activeNotificationsList.filter {
                it.packageName.lowercase().contains(targetPkg)
            }
        }
    }

    fun replyToNotification(packageName: String, message: String): Boolean {
        val targetPkg = packageName.lowercase()
        val activeNotifs = activeNotifications ?: return false

        for (sbn in activeNotifs) {
            val pkg = sbn.packageName ?: continue
            if (pkg.lowercase().contains(targetPkg)) {
                val notification = sbn.notification ?: continue
                val actions = notification.actions ?: continue

                for (action in actions) {
                    val remoteInputs = action.remoteInputs ?: continue
                    for (remoteInput in remoteInputs) {
                        val intent = Intent()
                        val bundle = Bundle()
                        bundle.putCharSequence(remoteInput.resultKey, message)
                        RemoteInput.addResultsToIntent(arrayOf(remoteInput), intent, bundle)

                        try {
                            action.actionIntent.send(applicationContext, 0, intent)
                            Log.i(TAG, "Successfully sent inline reply to notification from $pkg")
                            return true
                        } catch (e: Exception) {
                            Log.e(TAG, "Error sending reply to notification action intent", e)
                        }
                    }
                }
            }
        }
        return false
    }

    companion object {
        private const val TAG = "CipherNotifListener"

        @Volatile
        var instance: CipherNotificationListenerService? = null
            private set

        @Volatile
        var isRunning: Boolean = false
            private set
    }
}
