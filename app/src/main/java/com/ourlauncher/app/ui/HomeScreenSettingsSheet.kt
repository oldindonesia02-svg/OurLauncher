package com.ourlauncher.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import kotlin.math.roundToInt

@Composable
fun HomeScreenSettingsSheet(
    settingsManager: SettingsManager,
    onOpenTransitionEffects: () -> Unit,
    onSetDefaultScreen: () -> Unit,
    onRegenerateIcons: () -> Unit,
    onOpenMoreSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    var showLabel by remember { mutableStateOf(settingsManager.showLabels) }
    var isLiquidFolderEnabled by remember { mutableStateOf(true) }
    var iconSize by remember { mutableStateOf(settingsManager.iconSize) }
    var gridRows by remember { mutableStateOf(settingsManager.gridRows.toFloat()) }

    val liquidCyan = Color(0xFF00E5FF)

    LiquidGlassDialog(
        title = "Home Screen",
        confirmText = "Apply",
        cancelText = "More",
        onDismiss = onDismiss,
        onCancel = {
            onDismiss()
            onOpenMoreSettings()
        },
        onConfirm = {
            settingsManager.iconSize = iconSize
            settingsManager.gridRows = gridRows.roundToInt()
            settingsManager.showLabels = showLabel
            onDismiss()
        }
    ) {
        // 1. Desktop Grid Floating Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Desktop Grid", color = Color.White, fontSize = 14.sp)
            Text("${gridRows.roundToInt()} Rows", color = liquidCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LiquidGlassSlider(
            value = gridRows,
            onValueChange = { gridRows = it },
            valueRange = 4f..7f,
            steps = 3
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Icon Size Floating Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Icon Size", color = Color.White, fontSize = 14.sp)
            Text("${iconSize.toInt()} dp", color = liquidCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LiquidGlassSlider(
            value = iconSize,
            onValueChange = { iconSize = it },
            valueRange = 40f..72f
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Navigation Rows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSetDefaultScreen() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Set default screen", color = Color.White, fontSize = 14.sp)
            Text("›", color = Color.White.copy(alpha = 0.5f), fontSize = 18.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenTransitionEffects() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Customize Icons", color = Color.White, fontSize = 14.sp)
            Text("›", color = Color.White.copy(alpha = 0.5f), fontSize = 18.sp)
        }

        // 4. Show Label Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show label", color = Color.White, fontSize = 14.sp)
            Switch(
                checked = showLabel,
                onCheckedChange = { showLabel = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = liquidCyan,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }

        // 5. Liquid Folder Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Liquid folder", color = Color.White, fontSize = 14.sp)
            Switch(
                checked = isLiquidFolderEnabled,
                onCheckedChange = { isLiquidFolderEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = liquidCyan,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}
