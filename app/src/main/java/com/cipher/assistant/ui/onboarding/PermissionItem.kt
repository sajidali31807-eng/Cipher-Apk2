package com.cipher.assistant.ui.onboarding

import androidx.compose.runtime.Composable

data class PermissionStep(
    val stepIndex: Int,
    val title: String,
    val description: String,
    val buttonText: String,
    val iconResId: Int? = null,
    val isGranted: Boolean = false,
    val isVivoSpecific: Boolean = false
)
