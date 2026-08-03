package com.cipher.assistant.ui.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cipher.assistant.ui.theme.*

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAllPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.checkAllPermissions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CipherBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Step Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(9) { step ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (step <= uiState.currentStep) CipherElectricBlue else CipherSurfaceVariant
                        )
                )
            }
        }

        // Step Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.currentStep) {
                0 -> WelcomeStep()
                1 -> PermissionCard(
                    title = "Microphone Access",
                    description = "Cipher requires continuous background microphone access to listen for the wake word 'Get Ready Cipher'.",
                    statusGranted = uiState.isRecordAudioGranted,
                    actionText = "Grant Microphone Permission",
                    onAction = { recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                )
                2 -> PermissionCard(
                    title = "Accessibility Service",
                    description = "Required to execute voice commands automatically on your screen (tapping buttons, opening apps, scrolling).",
                    statusGranted = uiState.isAccessibilityGranted,
                    actionText = "Enable Accessibility Service",
                    onAction = { viewModel.openAccessibilitySettings() }
                )
                3 -> PermissionCard(
                    title = "Notification Access",
                    description = "Allows Cipher to read incoming notifications (WhatsApp, Messages, System alerts) and speak them aloud.",
                    statusGranted = uiState.isNotificationListenerGranted,
                    actionText = "Enable Notification Access",
                    onAction = { viewModel.openNotificationListenerSettings() }
                )
                4 -> PermissionCard(
                    title = "Display Over Other Apps",
                    description = "Enables the Floating Cipher Orb visual indicator to appear over any application when active.",
                    statusGranted = uiState.isOverlayGranted,
                    actionText = "Grant Overlay Permission",
                    onAction = { viewModel.openOverlaySettings() }
                )
                5 -> PermissionCard(
                    title = "Battery Optimization Exclusion",
                    description = "Exempts Cipher from Android Doze mode so the wake word engine stays alive 24/7 without being killed.",
                    statusGranted = uiState.isBatteryOptimizationDisabled,
                    actionText = "Disable Battery Optimization",
                    onAction = { viewModel.openBatteryOptimizationSettings() }
                )
                6 -> PermissionCard(
                    title = "Vivo Auto-Start Manager",
                    description = "[Vivo / iQOO Specific]\nEnable Cipher in Auto-Start Manager to prevent FuntouchOS / OriginOS from terminating background listening.",
                    statusGranted = uiState.isVivoAutostartGranted,
                    actionText = "Open Vivo Auto-Start Settings",
                    onAction = { viewModel.openVivoAutostartSettings() }
                )
                7 -> PermissionCard(
                    title = "Vivo High Background Power",
                    description = "[Vivo / iQOO Specific]\nSet Cipher power consumption mode to 'High Background Power Consumption' in Battery settings.",
                    statusGranted = uiState.isVivoHighPowerGranted,
                    actionText = "Open Vivo Battery Settings",
                    onAction = { viewModel.openVivoHighPowerSettings() }
                )
                8 -> CompletionStep(
                    onFinish = { viewModel.completeOnboarding(onOnboardingFinished) }
                )
            }
        }

        // Bottom Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.currentStep > 0 && uiState.currentStep < 8) {
                OutlinedButton(
                    onClick = { viewModel.previousStep() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CipherTextSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (uiState.currentStep < 8) {
                Button(
                    onClick = { viewModel.nextStep() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CipherElectricBlue,
                        contentColor = CipherBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (uiState.currentStep == 0) "Get Started" else "Next",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(CipherPurpleDim),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CipherElectricBlue)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "PROJECT CIPHER",
            style = MaterialTheme.typography.displayLarge,
            color = CipherElectricBlue,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Autonomous Voice & Automation System",
            style = MaterialTheme.typography.titleMedium,
            color = CipherTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to Cipher. Before launching, we must configure 8 deep system permissions to guarantee 24/7 background persistence and wake word execution.",
            style = MaterialTheme.typography.bodyLarge,
            color = CipherTextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    statusGranted: Boolean,
    actionText: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = CipherSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = CipherTextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = CipherTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = if (statusGranted) CipherSuccess.copy(alpha = 0.2f) else CipherError.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (statusGranted) "STATUS: GRANTED" else "STATUS: NOT GRANTED",
                    color = if (statusGranted) CipherSuccess else CipherError,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (statusGranted) CipherSurfaceVariant else CipherPurpleAccent,
                    contentColor = CipherTextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (statusGranted) "Re-check Settings" else actionText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CompletionStep(onFinish: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "SYSTEM READY",
            style = MaterialTheme.typography.displayLarge,
            color = CipherSuccess,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "All core system setup requirements have been configured. You are ready to ignite Cipher foreground engine.",
            style = MaterialTheme.typography.bodyLarge,
            color = CipherTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(
                containerColor = CipherElectricBlue,
                contentColor = CipherBackground
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "LAUNCH CIPHER ENGINE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
