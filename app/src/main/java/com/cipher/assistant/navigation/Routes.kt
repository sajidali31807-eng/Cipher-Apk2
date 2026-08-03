package com.cipher.assistant.navigation

sealed class Routes(val route: String) {
    object Onboarding : Routes("onboarding")
    object Dashboard : Routes("dashboard")
    object Settings : Routes("settings")
    object Logs : Routes("logs")
}
