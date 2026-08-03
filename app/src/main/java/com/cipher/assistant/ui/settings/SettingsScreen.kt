package com.cipher.assistant.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cipher.assistant.gemini.ConnectionTestResult
import com.cipher.assistant.ui.theme.*
import com.cipher.assistant.util.VivoOptimizationHelper
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onReRunOnboarding: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CipherBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Text("←", color = CipherTextPrimary, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SETTINGS & CONTROL",
                style = MaterialTheme.typography.titleLarge,
                color = CipherElectricBlue,
                fontWeight = FontWeight.Bold
            )
        }

        // Section 1 — Cipher Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECTION 1: CIPHER SYSTEM STATUS",
                    style = MaterialTheme.typography.labelMedium,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                StatusRow(
                    title = "Foreground Service",
                    isActive = uiState.serviceRunning,
                    activeLabel = "Running",
                    inactiveLabel = "Stopped"
                )
                StatusRow(
                    title = "Sherpa-ONNX Wake Word",
                    isActive = uiState.serviceRunning,
                    activeLabel = "Listening",
                    inactiveLabel = "Inactive"
                )
                StatusRow(
                    title = "Gemini Live Client",
                    isActive = uiState.apiKey.isNotBlank(),
                    activeLabel = "Ready",
                    inactiveLabel = "API Key Required"
                )
                StatusRow(
                    title = "Accessibility Engine",
                    isActive = uiState.accessibilityEnabled,
                    activeLabel = "Active",
                    inactiveLabel = "Disabled"
                )
            }
        }

        // Section 2 — API Configuration
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECTION 2: API CONFIGURATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.apiKey,
                    onValueChange = { viewModel.updateApiKey(it) },
                    label = { Text("Gemini Live API Key") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showPassword) "HIDE" else "SHOW",
                            color = CipherElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showPassword = !showPassword }
                                .padding(8.dp)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CipherElectricBlue,
                        unfocusedBorderColor = CipherOutline,
                        focusedTextColor = CipherTextPrimary,
                        unfocusedTextColor = CipherTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.saveApiKey() },
                        colors = ButtonDefaults.buttonColors(containerColor = CipherElectricBlue)
                    ) {
                        Text(if (uiState.isSaved) "SAVED ✓" else "SAVE KEY", color = CipherBackground)
                    }

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.testGeminiConnection().collect {}
                            }
                        },
                        enabled = !uiState.isTestingConnection,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CipherPurpleAccent)
                    ) {
                        if (uiState.isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("TEST CONNECTION")
                        }
                    }
                }

                uiState.connectionTestResult?.let { result ->
                    Spacer(modifier = Modifier.height(8.dp))
                    when (result) {
                        is ConnectionTestResult.Success -> {
                            Text(
                                text = "Connection Successful! Latency: ${result.latencyMs} ms",
                                color = CipherSuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        is ConnectionTestResult.Error -> {
                            Text(
                                text = "Connection Failed: ${result.message}",
                                color = CipherErrorRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Section 3 — Voice Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECTION 3: VOICE & PERSONALITY SETTINGS",
                    style = MaterialTheme.typography.labelMedium,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Active Wake Word", color = CipherTextMuted, fontSize = 12.sp)
                Text(
                    text = uiState.wakeWordLabel,
                    color = CipherTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Language Preference", color = CipherTextMuted, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Auto Detect", "English Only", "Hindi Only").forEach { lang ->
                        FilterChip(
                            selected = uiState.languageMode.contains(lang),
                            onClick = { viewModel.setLanguageMode(lang) },
                            label = { Text(lang, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Speech Response Speed", color = CipherTextMuted, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Normal", "Fast").forEach { speed ->
                        FilterChip(
                            selected = uiState.voiceSpeed == speed,
                            onClick = { viewModel.setVoiceSpeed(speed) },
                            label = { Text(speed, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Section 4 — Permissions Status Grid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECTION 4: SYSTEM PERMISSIONS (8 CRITICAL)",
                    style = MaterialTheme.typography.labelMedium,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                uiState.permissionStatuses.forEach { (permName, isGranted) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isGranted) CipherSuccessGreen else CipherErrorRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(permName, color = CipherTextPrimary, fontSize = 13.sp)
                        }

                        if (!isGranted) {
                            Text(
                                text = "FIX",
                                color = CipherElectricBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable {
                                        openPermissionSettings(context, permName)
                                    }
                                    .padding(4.dp)
                            )
                        } else {
                            Text("GRANTED ✓", color = CipherSuccessGreen, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 5 — Vivo Optimization
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECTION 5: VIVO & BATTERY STABILITY",
                    style = MaterialTheme.typography.labelMedium,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vivo Funtouch OS aggressively kills background processes. Grant autostart & battery whitelist to prevent Cipher from stopping.",
                    color = CipherTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { VivoOptimizationHelper.openVivoAutostartSettings(context) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CipherPurpleAccent)
                ) {
                    Text("OPEN VIVO AUTOSTART SETTINGS", color = CipherBackground)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { VivoOptimizationHelper.openVivoBatteryOptimizationSettings(context) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CipherPurpleAccent)
                ) {
                    Text("OPEN BATTERY OPTIMIZATION SETTINGS", color = CipherBackground)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { VivoOptimizationHelper.requestIgnoreBatteryOptimization(context) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CipherElectricBlue)
                ) {
                    Text("REQUEST BATTERY WHITELIST")
                }
            }
        }

        // Section 6 — About
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECTION 6: ABOUT CIPHER",
                    style = MaterialTheme.typography.labelMedium,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cipher Assistant Version: v2.0 Production", color = CipherTextPrimary, fontSize = 13.sp)
                Text("Build State: Mega Phase 19 Complete", color = CipherTextMuted, fontSize = 12.sp)
                Text("Target Target: Vivo Y21 / Low RAM High Precision", color = CipherTextMuted, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onReRunOnboarding,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RE-RUN PERMISSION WIZARD", color = CipherElectricBlue)
                }
            }
        }
    }
}

@Composable
private fun StatusRow(
    title: String,
    isActive: Boolean,
    activeLabel: String,
    inactiveLabel: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = CipherTextPrimary, fontSize = 13.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) CipherSuccessGreen else CipherErrorRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isActive) activeLabel else inactiveLabel,
                color = if (isActive) CipherSuccessGreen else CipherErrorRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun openPermissionSettings(context: android.content.Context, permName: String) {
    when (permName) {
        "Accessibility" -> {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        "Overlay Display" -> {
            context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        "Notifications" -> {
            context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        else -> {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
