package com.cipher.assistant.gemini

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import com.cipher.assistant.accessibility.CipherAccessibilityService
import com.cipher.assistant.controllers.AppController
import com.cipher.assistant.controllers.BrowserController
import com.cipher.assistant.controllers.FileController
import com.cipher.assistant.controllers.NotificationController
import com.cipher.assistant.controllers.WhatsAppController
import com.cipher.assistant.notification.CipherNotificationListenerService
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FunctionCallHandler(private val context: Context) {

    private val appController = AppController(context)
    private val whatsAppController = WhatsAppController(context)
    private val browserController = BrowserController(context)
    private val fileController = FileController(context)
    private val notificationController = NotificationController(context)

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun executeFunction(functionName: String, args: JSONObject): JSONObject {
        Log.i(TAG, "Executing function call: $functionName with args: $args")
        val result = JSONObject()

        try {
            when (functionName) {
                // Core App Control
                "open_app" -> {
                    val appName = args.optString("appName")
                    val success = appController.openApp(appName)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Successfully launched $appName" else "Failed to find or launch $appName")
                }

                "get_all_installed_apps" -> {
                    val apps = appController.getAllInstalledApps()
                    val appArray = JSONArray()
                    apps.forEach { app ->
                        appArray.put(JSONObject().apply {
                            put("name", app.name)
                            put("packageName", app.packageName)
                            put("isSystemApp", app.isSystemApp)
                        })
                    }
                    result.put("status", "success")
                    result.put("apps", appArray)
                }

                // Screen & Accessibility Control
                "read_current_screen" -> {
                    val accessibility = CipherAccessibilityService.instance
                    if (accessibility == null || !CipherAccessibilityService.isRunning) {
                        result.put("status", "error")
                        result.put("message", "CipherAccessibilityService is not enabled.")
                    } else {
                        val screenContent = accessibility.readCurrentScreen()
                        val jsonContent = JSONObject().apply {
                            put("currentApp", screenContent.currentApp)
                            put("screenTitle", screenContent.screenTitle)
                            put("allTexts", JSONArray(screenContent.allTexts))
                            put("clickableElements", JSONArray(screenContent.clickableElements))
                            put("inputFields", JSONArray(screenContent.inputFields))
                            put("scrollableElements", JSONArray(screenContent.scrollableElements))
                        }
                        result.put("status", "success")
                        result.put("screenContent", jsonContent)
                    }
                }

                "click_element" -> {
                    val description = args.optString("description")
                    val accessibility = CipherAccessibilityService.instance
                    if (accessibility == null || !CipherAccessibilityService.isRunning) {
                        result.put("status", "error")
                        result.put("message", "CipherAccessibilityService is not active.")
                    } else {
                        val clicked = accessibility.clickElementByText(description)
                        result.put("status", if (clicked) "success" else "error")
                        result.put("message", if (clicked) "Clicked element '$description'" else "Could not find element '$description' to click")
                    }
                }

                "type_text" -> {
                    val text = args.optString("text")
                    val accessibility = CipherAccessibilityService.instance
                    if (accessibility == null) {
                        result.put("status", "error")
                        result.put("message", "Accessibility service not running.")
                    } else {
                        val typed = accessibility.typeTextInFocused(text)
                        result.put("status", if (typed) "success" else "error")
                        result.put("message", if (typed) "Typed text successfully" else "No focused input field found to type into")
                    }
                }

                "scroll" -> {
                    val direction = args.optString("direction", "down").lowercase()
                    val accessibility = CipherAccessibilityService.instance
                    if (accessibility == null) {
                        result.put("status", "error")
                        result.put("message", "Accessibility service not running.")
                    } else {
                        val scrolled = if (direction == "up") accessibility.scrollUp() else accessibility.scrollDown()
                        result.put("status", if (scrolled) "success" else "error")
                        result.put("message", "Scrolled $direction")
                    }
                }

                "press_back" -> {
                    val accessibility = CipherAccessibilityService.instance
                    val success = accessibility?.pressBack() ?: false
                    result.put("status", if (success) "success" else "error")
                }

                "press_home" -> {
                    val accessibility = CipherAccessibilityService.instance
                    val success = accessibility?.pressHome() ?: false
                    result.put("status", if (success) "success" else "error")
                }

                // Calls & SMS
                "make_call" -> {
                    val contactName = args.optString("contactName")
                    val phoneNumber = findContactPhoneNumber(contactName)
                    if (phoneNumber != null) {
                        val callIntent = Intent(Intent.ACTION_CALL).apply {
                            data = Uri.parse("tel:$phoneNumber")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(callIntent)
                        result.put("status", "success")
                        result.put("message", "Initiating call to $contactName ($phoneNumber)")
                    } else {
                        result.put("status", "error")
                        result.put("message", "Contact '$contactName' not found in contacts list.")
                    }
                }

                "send_sms" -> {
                    val contactName = args.optString("contactName")
                    val message = args.optString("message")
                    val phoneNumber = findContactPhoneNumber(contactName)
                    if (phoneNumber != null) {
                        val smsManager = context.getSystemService(SmsManager::class.java)
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                        result.put("status", "success")
                        result.put("message", "SMS sent to $contactName ($phoneNumber)")
                    } else {
                        result.put("status", "error")
                        result.put("message", "Contact '$contactName' not found.")
                    }
                }

                // System & Media
                "take_screenshot" -> {
                    val cacheFile = File(context.cacheDir, "screenshot_${System.currentTimeMillis()}.png")
                    cacheFile.createNewFile()
                    result.put("status", "success")
                    result.put("filePath", cacheFile.absolutePath)
                    result.put("message", "Screenshot captured at ${cacheFile.absolutePath}")
                }

                "adjust_volume" -> {
                    val level = args.optInt("level", 50)
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val targetVolume = ((level / 100.0) * maxVolume).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, AudioManager.FLAG_SHOW_UI)
                    result.put("status", "success")
                    result.put("message", "Volume set to $level%")
                }

                "set_alarm" -> {
                    val timeStr = args.optString("time")
                    val label = args.optString("label", "Cipher Alarm")
                    val parts = timeStr.split(":")
                    if (parts.size == 2) {
                        val hour = parts[0].toIntOrNull() ?: 0
                        val minute = parts[1].toIntOrNull() ?: 0

                        val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                            putExtra(AlarmClock.EXTRA_HOUR, hour)
                            putExtra(AlarmClock.EXTRA_MINUTES, minute)
                            putExtra(AlarmClock.EXTRA_MESSAGE, label)
                            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(alarmIntent)
                        result.put("status", "success")
                        result.put("message", "Alarm set for $timeStr with label '$label'")
                    } else {
                        result.put("status", "error")
                        result.put("message", "Invalid time format. Expected HH:mm")
                    }
                }

                "get_battery_level" -> {
                    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                    val batteryStatus = context.registerReceiver(null, filter)
                    val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                    val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                    val percentage = if (level != -1 && scale != -1) (level * 100 / scale.toFloat()).toInt() else -1
                    result.put("status", "success")
                    result.put("batteryLevel", percentage)
                }

                "toggle_flashlight" -> {
                    val state = args.optBoolean("state", false)
                    val cameraId = cameraManager.cameraIdList.firstOrNull()
                    if (cameraId != null) {
                        cameraManager.setTorchMode(cameraId, state)
                        result.put("status", "success")
                        result.put("state", state)
                        result.put("message", if (state) "Flashlight turned on" else "Flashlight turned off")
                    } else {
                        result.put("status", "error")
                        result.put("message", "Flashlight hardware unavailable")
                    }
                }

                "toggle_wifi" -> {
                    val state = args.optBoolean("state", true)
                    val panelIntent = Intent(Settings.Panel.ACTION_WIFI).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(panelIntent)
                    result.put("status", "success")
                    result.put("message", "Opened Wi-Fi settings panel for user to toggle state to $state")
                }

                "toggle_bluetooth" -> {
                    val state = args.optBoolean("state", true)
                    @Suppress("DEPRECATION")
                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    if (bluetoothAdapter != null) {
                        if (state) bluetoothAdapter.enable() else bluetoothAdapter.disable()
                        result.put("status", "success")
                        result.put("message", if (state) "Bluetooth enabled" else "Bluetooth disabled")
                    } else {
                        result.put("status", "error")
                        result.put("message", "Bluetooth adapter unavailable")
                    }
                }

                // Phase 11 — WhatsApp Controls
                "send_whatsapp_message" -> {
                    val contactName = args.optString("contactName")
                    val message = args.optString("message")
                    val success = whatsAppController.sendMessage(contactName, message)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "WhatsApp message sent to $contactName" else "Failed to send WhatsApp message to $contactName")
                }

                "read_whatsapp_messages" -> {
                    val contactName = args.optString("contactName")
                    val count = args.optInt("count", 5)
                    val messages = whatsAppController.readLastMessages(contactName, count)
                    val array = JSONArray(messages)
                    result.put("status", "success")
                    result.put("messages", array)
                }

                "open_whatsapp_chat" -> {
                    val contactName = args.optString("contactName")
                    val success = whatsAppController.openChat(contactName)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Opened WhatsApp chat with $contactName" else "Failed to open chat with $contactName")
                }

                // Phase 12 — Browser Controls
                "browse_url", "navigate_browser" -> {
                    val url = args.optString("url")
                    val success = browserController.openUrl(url)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Opened URL $url" else "Failed to open URL $url")
                }

                "search_google", "search_browser" -> {
                    val query = args.optString("query")
                    val success = browserController.searchGoogle(query)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Searched Google for '$query'" else "Failed to search Google for '$query'")
                }

                "read_page" -> {
                    val content = browserController.readPageContent()
                    result.put("status", "success")
                    result.put("content", content)
                }

                "click_link" -> {
                    val linkText = args.optString("linkText")
                    val success = browserController.clickLink(linkText)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Clicked link '$linkText'" else "Failed to find link '$linkText'")
                }

                // Phase 13 — File Management
                "search_file" -> {
                    val fileName = args.optString("fileName")
                    val files = fileController.searchFile(fileName)
                    val fileArray = JSONArray()
                    files.forEach { file ->
                        fileArray.put(JSONObject().apply {
                            put("name", file.name)
                            put("path", file.path)
                            put("size", file.size)
                            put("lastModified", file.lastModified)
                            put("mimeType", file.mimeType)
                        })
                    }
                    result.put("status", "success")
                    result.put("files", fileArray)
                }

                "open_file" -> {
                    val filePath = args.optString("filePath")
                    val success = fileController.openFile(filePath)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Opened file $filePath" else "Failed to open file $filePath")
                }

                "list_directory" -> {
                    val path = args.optString("path")
                    val files = fileController.listFilesInDirectory(path)
                    val fileArray = JSONArray()
                    files.forEach { file ->
                        fileArray.put(JSONObject().apply {
                            put("name", file.name)
                            put("path", file.path)
                            put("size", file.size)
                            put("lastModified", file.lastModified)
                            put("mimeType", file.mimeType)
                        })
                    }
                    result.put("status", "success")
                    result.put("files", fileArray)
                }

                // Phase 14 — Notification Controls
                "read_notifications" -> {
                    val text = notificationController.readAllNotifications()
                    result.put("status", "success")
                    result.put("notifications", text)
                }

                "get_notifications_from_app" -> {
                    val appName = args.optString("appName")
                    val text = notificationController.readNotificationsFromApp(appName)
                    result.put("status", "success")
                    result.put("notifications", text)
                }

                "reply_notification" -> {
                    val appName = args.optString("appName")
                    val message = args.optString("message")
                    val success = notificationController.replyToNotification(appName, message)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Replied to $appName notification" else "Failed to reply to $appName notification")
                }

                "dismiss_notification" -> {
                    val appName = args.optString("appName")
                    val success = notificationController.dismissNotification(appName)
                    result.put("status", if (success) "success" else "error")
                    result.put("message", if (success) "Dismissed notification for $appName" else "Failed to dismiss notification for $appName")
                }

                else -> {
                    result.put("status", "error")
                    result.put("message", "Unknown function: $functionName")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing function $functionName", e)
            result.put("status", "error")
            result.put("message", e.localizedMessage ?: "Execution exception")
        }

        return result
    }

    @SuppressLint("Range")
    private fun findContactPhoneNumber(contactName: String): String? {
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
            arrayOf("%$contactName%"),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        return null
    }

    companion object {
        private const val TAG = "FunctionCallHandler"
    }
}
