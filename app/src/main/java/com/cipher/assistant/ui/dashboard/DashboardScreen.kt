package com.cipher.assistant.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cipher.assistant.data.ActivityEntry
import com.cipher.assistant.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToLogs: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
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
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROJECT CIPHER",
                    style = MaterialTheme.typography.titleLarge,
                    color = CipherElectricBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Cyber Sentinel Assistant v2.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = CipherTextMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNavigateToLogs,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CipherSurface)
                ) {
                    Text("📋", fontSize = 18.sp)
                }

                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CipherSurface)
                ) {
                    Text("⚙️", fontSize = 18.sp)
                }
            }
        }

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CIPHER ENGINE STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CipherTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.isServiceRunning) "ACTIVE & LISTENING" else "STANDBY / STOPPED",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (uiState.isServiceRunning) CipherSuccessGreen else CipherErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isServiceRunning) CipherSuccessGreen else CipherErrorRed)
                )
            }
        }

        // Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.toggleCipherService() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isServiceRunning) CipherErrorRed else CipherElectricBlue,
                    contentColor = CipherBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = if (uiState.isServiceRunning) "STOP SERVICE" else "START SERVICE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            OutlinedButton(
                onClick = { viewModel.toggleFloatingOrb() },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (uiState.isOrbVisible) CipherPurpleAccent else CipherTextMuted
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = if (uiState.isOrbVisible) "HIDE ORB" else "SHOW ORB",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Recent Activity Timeline Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT ACTIVITY TIMELINE",
                style = MaterialTheme.typography.labelLarge,
                color = CipherTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "VIEW FULL LOGS →",
                color = CipherElectricBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogs() }
            )
        }

        // Timeline Items Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = CipherSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (uiState.recentActivities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No recent activities recorded", color = CipherTextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.recentActivities) { item ->
                        ActivityTimelineItem(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityTimelineItem(item: ActivityEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (item.success) CipherSuccessGreen else CipherErrorRed)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.action,
                    color = CipherTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.formattedTime(),
                    color = CipherTextMuted,
                    fontSize = 11.sp
                )
            }
            Text(
                text = item.detail,
                color = CipherTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
