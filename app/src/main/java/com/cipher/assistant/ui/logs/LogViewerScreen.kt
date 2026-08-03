package com.cipher.assistant.ui.logs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cipher.assistant.ui.theme.*
import com.cipher.assistant.util.CipherLogger
import com.cipher.assistant.util.LogEntry
import com.cipher.assistant.util.LogLevel

@Composable
fun LogViewerScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var logs by remember { mutableStateOf(CipherLogger.getRecentLogs(100)) }
    var selectedLevel by remember { mutableStateOf<LogLevel?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(logs, selectedLevel, searchQuery) {
        logs.filter { entry ->
            val matchesLevel = (selectedLevel == null || entry.level == selectedLevel)
            val matchesQuery = searchQuery.isBlank() ||
                    entry.message.contains(searchQuery, ignoreCase = true) ||
                    entry.tag.contains(searchQuery, ignoreCase = true)
            matchesLevel && matchesQuery
        }.reversed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CipherBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Text("←", color = CipherTextPrimary, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SYSTEM LOGS",
                    style = MaterialTheme.typography.titleLarge,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                TextButton(onClick = {
                    val exportText = CipherLogger.exportLogs()
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Cipher Logs", exportText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Logs copied to clipboard!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("EXPORT", color = CipherElectricBlue, fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = {
                    CipherLogger.clearLogs()
                    logs = emptyList()
                }) {
                    Text("CLEAR", color = CipherErrorRed, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search logs...", color = CipherTextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CipherElectricBlue,
                unfocusedBorderColor = CipherOutline,
                focusedTextColor = CipherTextPrimary,
                unfocusedTextColor = CipherTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedLevel == null,
                onClick = { selectedLevel = null },
                label = { Text("ALL", fontSize = 11.sp) }
            )
            LogLevel.values().forEach { level ->
                FilterChip(
                    selected = selectedLevel == level,
                    onClick = { selectedLevel = level },
                    label = { Text(level.name, fontSize = 11.sp) }
                )
            }
        }

        // Log Entries List
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (filteredLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No log entries match filter", color = CipherTextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(filteredLogs) { entry ->
                        LogEntryItem(entry = entry)
                        HorizontalDivider(color = CipherOutline.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogEntryItem(entry: LogEntry) {
    val levelColor = when (entry.level) {
        LogLevel.DEBUG -> CipherTextMuted
        LogLevel.INFO -> CipherElectricBlue
        LogLevel.WARNING -> Color(0xFFFFD166)
        LogLevel.ERROR -> CipherErrorRed
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[${entry.formattedTime()}] ${entry.tag}",
                color = levelColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = entry.level.name,
                color = levelColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = entry.message,
            color = CipherTextPrimary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
