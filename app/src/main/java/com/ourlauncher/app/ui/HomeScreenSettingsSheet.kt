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
    visible: Boolean = true,
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

    val accentBlue = Color(0xFF007AFF)
    val textColor = Color(0xFF102844)

    LiquidGlassBottomSheet(
        visible = visible,
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
        // 1. Desktop Grid Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Desktop Grid", color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("${gridRows.roundToInt()} Rows", color = accentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LiquidGlassSlider(
            value = gridRows,
            onValueChange = { gridRows = it },
            valueRange = 4f..7f,
            steps = 3
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Icon Size Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Icon Size", color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text("${iconSize.toInt()} dp", color = accentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LiquidGlassSlider(
            value = iconSize,
            onValueChange = { iconSize = it },
            valueRange = 40f..72f
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Navigation Links
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSetDefaultScreen() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Set default screen", color = textColor, fontSize = 15.sp)
            Text("›", color = textColor.copy(alpha = 0.5f), fontSize = 18.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenTransitionEffects() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Customize Icons", color = textColor, fontSize = 15.sp)
            Text("›", color = textColor.copy(alpha = 0.5f), fontSize = 18.sp)
        }

        // 4. Show Label Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show label", color = textColor, fontSize = 15.sp)
            Switch(
                checked = showLabel,
                onCheckedChange = { showLabel = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFC0D3E5)
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
            Text("Liquid folder", color = textColor, fontSize = 15.sp)
            Switch(
                checked = isLiquidFolderEnabled,
                onCheckedChange = { isLiquidFolderEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFC0D3E5)
                )
            )
        }
    }
}
