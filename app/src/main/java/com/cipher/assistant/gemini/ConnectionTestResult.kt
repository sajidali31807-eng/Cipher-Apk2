package com.cipher.assistant.gemini

sealed class ConnectionTestResult {
    data class Success(val latencyMs: Long) : ConnectionTestResult()
    data class Error(val message: String) : ConnectionTestResult()
}
