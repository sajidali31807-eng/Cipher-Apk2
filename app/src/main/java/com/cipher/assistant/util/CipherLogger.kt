package com.cipher.assistant.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogLevel {
    DEBUG, INFO, WARNING, ERROR
}

data class LogEntry(
    val timestamp: Long,
    val tag: String,
    val message: String,
    val level: LogLevel
) {
    fun formattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

object CipherLogger {

    private const val MAX_LOGS = 200
    private val logBuffer = mutableListOf<LogEntry>()

    @Synchronized
    fun log(tag: String, message: String, level: LogLevel = LogLevel.INFO) {
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            tag = tag,
            message = message,
            level = level
        )

        logBuffer.add(entry)
        if (logBuffer.size > MAX_LOGS) {
            logBuffer.removeAt(0)
        }

        when (level) {
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARNING -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
        }
    }

    @Synchronized
    fun getRecentLogs(count: Int = 50): List<LogEntry> {
        val available = logBuffer.takeLast(count)
        return available.toList()
    }

    @Synchronized
    fun exportLogs(): String {
        val sb = StringBuilder("=== CIPHER SYSTEM LOGS EXPORT ===\n")
        logBuffer.forEach { entry ->
            sb.append("[${entry.formattedTime()}] [${entry.level.name}] [${entry.tag}] ${entry.message}\n")
        }
        return sb.toString()
    }

    @Synchronized
    fun clearLogs() {
        logBuffer.clear()
    }
}
