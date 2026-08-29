package com.ourlauncher.app.ui

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
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
    var iconSize by remember { mutableStateOf(1f) }
    var gridRows by remember { mutableStateOf(5f) }

    // Liquid Glass Shaders & Colors
    val cardShape = RoundedCornerShape(32.dp)
    val liquidCyan = Color(0xFF00E5FF)
    val glassTint = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.18f),
            Color(0xFF0F172A).copy(alpha = 0.75f)
        )
    )
    val glassBorder = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.08f),
            liquidCyan.copy(alpha = 0.25f)
        )
    )

    // Full-screen backdrop dismiss area
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() }
            .padding(horizontal = 20.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Rounded Liquid Glass Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = RenderEffect
                            .createBlurEffect(35f, 35f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
                }
                .background(glassTint)
                .border(width = 1.5.dp, brush = glassBorder, shape = cardShape)
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Glass Pill Drag Indicator
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
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
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 1. Desktop Grid Slider
                Text(
                    text = "Desktop Grid",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = gridRows,
                        onValueChange = { gridRows = it },
                        valueRange = 4f..7f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = liquidCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Icon Size Slider
                Text(
                    text = "Icon Size",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = iconSize,
                        onValueChange = { iconSize = it },
                        valueRange = 0.8f..1.2f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = liquidCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Navigation Rows
                SheetNavRow(title = "Set default screen", onClick = onSetDefaultScreen)
                SheetNavRow(title = "Customize Icons", onClick = onOpenTransitionEffects)

                // 4. Show Label Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
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
                        .padding(vertical = 8.dp),
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

                Spacer(modifier = Modifier.height(12.dp))

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
    val bg = if (isPrimary) Color(0xFF00A2FF).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.12f)
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
