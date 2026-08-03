package com.cipher.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.cipher.assistant.navigation.CipherNavGraph
import com.cipher.assistant.navigation.Routes
import com.cipher.assistant.ui.theme.CipherTheme
import com.cipher.assistant.util.PermissionUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate()
        enableEdgeToEdge()

        val preferences = (application as CipherApplication).preferences

        setContent {
            CipherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val onboardingComplete by preferences.isOnboardingCompleteFlow.collectAsState(initial = false)

                    val startDestination = if (onboardingComplete && PermissionUtils.areCorePermissionsGranted(this)) {
                        Routes.Dashboard.route
                    } else {
                        Routes.Onboarding.route
                    }

                    CipherNavGraph(startDestination = startDestination)
                }
            }
        }
    }
}
