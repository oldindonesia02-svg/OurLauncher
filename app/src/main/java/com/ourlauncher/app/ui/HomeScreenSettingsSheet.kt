package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var iconSize by remember { mutableStateOf(1f) }
    var gridRows by remember { mutableStateOf(5f) }

    val cardShape = RoundedCornerShape(32.dp)
    val liquidCyan = Color(0xFF00E5FF)

    val glassTint = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E293B).copy(alpha = 0.92f),
            Color(0xFF0F172A).copy(alpha = 0.96f)
        )
    )
    val glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.08f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .padding(horizontal = 20.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(glassTint)
                .border(width = 1.5.dp, brush = glassBorder, shape = cardShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Glass Drag Indicator
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
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
                        text = "Home Screen",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 1. Desktop Grid Liquid Glass Slider
                Text(
                    text = "Desktop Grid: ${gridRows.roundToInt()} Rows",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LiquidGlassPillSlider(
                    value = gridRows,
                    onValueChange = { gridRows = it },
                    valueRange = 4f..7f,
                    steps = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Icon Size Liquid Glass Slider
                Text(
                    text = "Icon Size: ${(iconSize * 100).roundToInt()}%",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LiquidGlassPillSlider(
                    value = iconSize,
                    onValueChange = { iconSize = it },
                    valueRange = 0.8f..1.2f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Navigation Rows
                SheetNavRow(title = "Set default screen", onClick = onSetDefaultScreen)
                SheetNavRow(title = "Customize Icons", onClick = onOpenTransitionEffects)

                // 4. Show Label Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
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
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }

                // 5. Liquid Folder Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Liquid folder", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
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

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LiquidActionButton(
                        title = "Regenerate",
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        onClick = onRegenerateIcons
                    )
                    LiquidActionButton(
                        title = "More",
                        modifier = Modifier.weight(1f),
                        isPrimary = true,
                        onClick = onOpenMoreSettings
                    )
                }
            }
        }
    }
}

@Composable
fun SheetNavRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(text = "›", color = Color.White.copy(alpha = 0.45f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LiquidActionButton(
    title: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isPrimary) Color(0xFF00A2FF).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.12f)
    val border = if (isPrimary) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(21.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
