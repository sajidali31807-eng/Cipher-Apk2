package com.cipher.assistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CipherDarkColorScheme = darkColorScheme(
    primary = CipherElectricBlue,
    onPrimary = CipherBackground,
    primaryContainer = CipherElectricBlueDim,
    onPrimaryContainer = CipherTextPrimary,
    secondary = CipherPurpleAccent,
    onSecondary = CipherTextPrimary,
    secondaryContainer = CipherPurpleDim,
    onSecondaryContainer = CipherTextPrimary,
    tertiary = CipherCyan,
    onTertiary = CipherBackground,
    background = CipherBackground,
    onBackground = CipherTextPrimary,
    surface = CipherSurface,
    onSurface = CipherTextPrimary,
    surfaceVariant = CipherSurfaceVariant,
    onSurfaceVariant = CipherTextSecondary,
    outline = CipherOutline,
    error = CipherError,
    onError = CipherBackground
)

@Composable
fun CipherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CipherDarkColorScheme,
        typography = Typography,
        content = content
    )
}
