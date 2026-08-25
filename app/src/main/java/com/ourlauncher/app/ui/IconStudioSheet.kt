package com.ourlauncher.app.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

@Composable
fun IconStudioSheet(
    settingsManager: SettingsManager,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: General, 1: Themes
    var iconStyle by remember { mutableStateOf("Standard") } // Standard, Dark, Transparent, Tinted
    var cornerRadius by remember { mutableStateOf(settingsManager.iconCornerRadius) }

    // Themes & Lighting Tweak States
    var themePreset by remember { mutableStateOf("Highlight") } // Empty, Highlight, Shadow border
    var blurEnabled by remember { mutableStateOf(true) }
    var blurValue by remember { mutableStateOf(0.5f) }
    var falloff by remember { mutableStateOf(1.5f) }
    var strokeWidth by remember { mutableStateOf(1.5f) }
    var intensity by remember { mutableStateOf(100f) }
    var lightAngle by remember { mutableStateOf(75f) }

    val pillTabBg = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.16f),
            Color(0xFF141418).copy(alpha = 0.50f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color(0xFF161619).copy(alpha = 0.98f))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Drag handle & Title
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Floating Top Badge "Icon pack"
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text("Icon pack", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Customize",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector: General | Themes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selectedTab == 0) pillTabBg else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
                        .clickable { selectedTab = 0 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("General", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selectedTab == 1) pillTabBg else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
                        .clickable { selectedTab = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Themes", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (selectedTab == 0) {
                // ==================== GENERAL TAB ====================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Standard", "Dark", "Transparent", "Tinted").forEach { style ->
                        val isSelected = iconStyle == style
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { iconStyle = style }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🗂", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(style, color = Color.White.copy(alpha = 0.8f), fontSize = 11.5.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text("Corner Radius: ${cornerRadius.toInt()}%", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                Slider(
                    value = cornerRadius,
                    onValueChange = {
                        cornerRadius = it
                        settingsManager.iconCornerRadius = it
                    },
                    valueRange = 0f..50f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                )
            } else {
                // ==================== THEMES & LIGHTING TAB ====================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Empty", "Highlight", "Shadow border").forEach { preset ->
                        val isSelected = themePreset == preset
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { themePreset = preset }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✦", fontSize = 18.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(preset, color = Color.White.copy(alpha = 0.8f), fontSize = 11.5.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Tweak Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                // Blur Row + Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Blur: ${String.format("%.1f", blurValue)} dp", color = Color.White.copy(alpha = 0.8f), fontSize = 12.5.sp)
                    Switch(
                        checked = blurEnabled,
                        onCheckedChange = { blurEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0A84FF))
                    )
                }

                // Falloff
                Text("Falloff: ${String.format("%.1f", falloff)}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.5.sp)
                Slider(
                    value = falloff,
                    onValueChange = { falloff = it },
                    valueRange = 0.5f..5f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                )

                // Width
                Text("Width: ${String.format("%.1f", strokeWidth)} dp", color = Color.White.copy(alpha = 0.8f), fontSize = 12.5.sp)
                Slider(
                    value = strokeWidth,
                    onValueChange = { strokeWidth = it },
                    valueRange = 0.5f..5f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                )

                // Intensity
                Text("Intensity: ${intensity.toInt()}%", color = Color.White.copy(alpha = 0.8f), fontSize = 12.5.sp)
                Slider(
                    value = intensity,
                    onValueChange = { intensity = it },
                    valueRange = 10f..100f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                )

                // Light Angle
                Text("Angle: ${lightAngle.toInt()}°", color = Color.White.copy(alpha = 0.8f), fontSize = 12.5.sp)
                Slider(
                    value = lightAngle,
                    onValueChange = { lightAngle = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0A84FF))
                    .clickable {
                        onApply()
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Apply", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
