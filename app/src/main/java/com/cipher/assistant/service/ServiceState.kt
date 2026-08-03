package com.cipher.assistant.service

sealed class ServiceState {
    object Idle : ServiceState()
    object ListeningForWakeWord : ServiceState()
    object WakeWordDetected : ServiceState()
    object ProcessingCommand : ServiceState()
    data class Speaking(val text: String = "") : ServiceState()
    data class Error(val message: String) : ServiceState()
}
