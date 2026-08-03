package com.cipher.assistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cipher.assistant.ui.dashboard.DashboardScreen
import com.cipher.assistant.ui.logs.LogViewerScreen
import com.cipher.assistant.ui.onboarding.OnboardingScreen
import com.cipher.assistant.ui.settings.SettingsScreen

@Composable
fun CipherNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onOnboardingFinished = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Dashboard.route) {
            DashboardScreen(
                onNavigateToSettings = {
                    navController.navigate(Routes.Settings.route)
                },
                onNavigateToLogs = {
                    navController.navigate(Routes.Logs.route)
                }
            )
        }

        composable(Routes.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onReRunOnboarding = {
                    navController.navigate(Routes.Onboarding.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Logs.route) {
            LogViewerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
