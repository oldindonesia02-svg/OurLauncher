package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

@Composable
fun SearchBarAdjustmentSheet(
    settingsManager: SettingsManager,
    onDismiss: () -> Unit,
    onSwitchToDock: () -> Unit
) {
    var offset by remember { mutableStateOf(settingsManager.searchOffset) }
    var hideCapsule by remember { mutableStateOf(settingsManager.hideSearchCapsule) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Reset, Title, Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reset",
                    color = Color(0xFF0A84FF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        offset = 0f
                        hideCapsule = false
                        settingsManager.searchOffset = 0f
                        settingsManager.hideSearchCapsule = false
                    }
                )
                Text(
                    text = "Search Bar Position",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✕",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vertical Offset Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("VERTICAL OFFSET", color = Color(0xFF8E8E93), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("${offset.toInt()} px", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = offset,
                onValueChange = {
                    offset = it
                    settingsManager.searchOffset = it
                },
                valueRange = -50f..50f
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hide Search Capsule Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Hide search capsule", color = Color.White, fontSize = 14.sp)
                    Text("Hides the capsule on home screen", color = Color(0xFF8E8E93), fontSize = 11.sp)
                }
                Switch(
                    checked = hideCapsule,
                    onCheckedChange = {
                        hideCapsule = it
                        settingsManager.hideSearchCapsule = it
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0A84FF))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch to Dock Link
            Text(
                text = "Change dock position?",
                color = Color(0xFF0A84FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSwitchToDock() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apply Button
            Button(
                onClick = { onDismiss() },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF))
            ) {
                Text("Apply", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DockAdjustmentSheet(
    settingsManager: SettingsManager,
    onDismiss: () -> Unit,
    onSwitchToSearch: () -> Unit
) {
    var showBg by remember { mutableStateOf(settingsManager.showDockBg) }
    var radius by remember { mutableStateOf(settingsManager.dockRadius) }
    var offset by remember { mutableStateOf(settingsManager.dockOffset) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reset",
                    color = Color(0xFF0A84FF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        showBg = true
                        radius = 33f
                        offset = 0f
                        settingsManager.showDockBg = true
                        settingsManager.dockRadius = 33f
                        settingsManager.dockOffset = 0f
                    }
                )
                Text(
                    text = "Dock customization",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✕",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Show dock background
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Show dock background", color = Color.White, fontSize = 14.sp)
                    Text("Show or hide the dock's glass backdrop", color = Color(0xFF8E8E93), fontSize = 11.sp)
                }
                Switch(
                    checked = showBg,
                    onCheckedChange = {
                        showBg = it
                        settingsManager.showDockBg = it
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0A84FF))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Corner Radius
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dock corner radius", color = Color.White, fontSize = 13.sp)
                Text("${radius.toInt()}%", color = Color(0xFF0A84FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = radius,
                onValueChange = {
                    radius = it
                    settingsManager.dockRadius = it
                },
                valueRange = 8f..50f
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Vertical Offset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dock vertical offset", color = Color.White, fontSize = 13.sp)
                Text("${offset.toInt()} dp", color = Color(0xFF0A84FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = offset,
                onValueChange = {
                    offset = it
                    settingsManager.dockOffset = it
                },
                valueRange = -30f..30f
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Switch to Search Bar Link
            Text(
                text = "Change search bar position?",
                color = Color(0xFF0A84FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSwitchToSearch() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onDismiss() },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF))
            ) {
                Text("Apply", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
