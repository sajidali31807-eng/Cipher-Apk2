package com.cipher.assistant.gemini

sealed class GeminiState {
    object Disconnected : GeminiState()
    object Connecting : GeminiState()
    object Connected : GeminiState()
    object Listening : GeminiState()
    object Processing : GeminiState()
    data class Speaking(val text: String = "") : GeminiState()
    data class Error(val message: String) : GeminiState()
}
