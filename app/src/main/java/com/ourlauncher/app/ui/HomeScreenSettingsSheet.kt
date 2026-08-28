package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

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
    
    // Naye State Variables for missing settings (inhe future mein SettingsManager se link kar lena)
    var iconSize by remember { mutableStateOf(1f) }
    var gridRows by remember { mutableStateOf(5f) }

    // Liquid Glass Gradient Variables
    val glassBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E2029).copy(alpha = 0.85f), 
            Color(0xFF0F1015).copy(alpha = 0.95f)
        )
    )
    val glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF).copy(alpha = 0.2f),
            Color.Transparent
        )
    )
    val liquidCyan = Color(0xFF00E5FF)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(glassBackground) // Frosted Glass Background
            .border(
                width = 1.dp,
                brush = glassBorder,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) // Reflective Border Highlight
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Glowing Drag Indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.4f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✕",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 20.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
                Text(
                    text = "Home screen",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(20.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Grid & Layout (NEW)
            Text("Desktop Grid", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Slider(
                value = gridRows,
                onValueChange = { gridRows = it },
                valueRange = 4f..7f,
                steps = 2,
                colors = SliderDefaults.colors(
                    thumbColor = liquidCyan,
                    activeTrackColor = liquidCyan.copy(alpha = 0.8f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )

            // 2. Icon Size (NEW)
            Text("Icon Size", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Slider(
                value = iconSize,
                onValueChange = { iconSize = it },
                valueRange = 0.8f..1.2f,
                colors = SliderDefaults.colors(
                    thumbColor = liquidCyan,
                    activeTrackColor = liquidCyan.copy(alpha = 0.8f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Set default screen
            SheetNavRow(title = "Set default screen", onClick = onSetDefaultScreen)
            
            // 4. Transition Effects
            SheetNavRow(title = "Customize Icons", onClick = onOpenTransitionEffects)

            // 5. Show Label Toggle (Upgraded style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show label", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = showLabel,
                    onCheckedChange = { 
                        showLabel = it
                        settingsManager.showLabels = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = liquidCyan,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }

            // 6. Liquid Folder Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Liquid folder",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = isLiquidFolderEnabled,
                    onCheckedChange = { isLiquidFolderEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = liquidCyan,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Regenerate All Icons (Glowing Action)
            Text(
                text = "Regenerate all icons   ›",
                color = liquidCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onRegenerateIcons() }
                    .padding(vertical = 10.dp)
            )

            // 8. More Settings
            Text(
                text = "More settings   ›",
                color = liquidCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onOpenMoreSettings() }
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
fun SheetNavRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(text = "›", color = Color.White.copy(alpha = 0.45f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
