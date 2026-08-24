package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
fun LiquidGlassScreen(
    onBack: () -> Unit,
    settingsManager: SettingsManager
) {
    var mode by remember { mutableStateOf(settingsManager.glassMode) }
    var transparency by remember { mutableStateOf(settingsManager.glassTransparency) }
    var blurRadius by remember { mutableStateOf(settingsManager.glassBlurRadius) }
    var refractionHeight by remember { mutableStateOf(settingsManager.glassRefractionHeight) }
    var refractionAmount by remember { mutableStateOf(settingsManager.glassRefractionAmount) }
    var depthEnabled by remember { mutableStateOf(settingsManager.glassDepthEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0E))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "‹", color = Color(0xFF0A84FF), fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Liquid Glass",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Reset Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable {
                        mode = "easy"
                        transparency = 0.15f
                        blurRadius = 0.30f
                        refractionHeight = 20f
                        refractionAmount = 35f
                        depthEnabled = false
                        settingsManager.glassMode = mode
                        settingsManager.glassTransparency = transparency
                        settingsManager.glassBlurRadius = blurRadius
                        settingsManager.glassRefractionHeight = refractionHeight
                        settingsManager.glassRefractionAmount = refractionAmount
                        settingsManager.glassDepthEnabled = depthEnabled
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "↻", color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- INTERACTIVE LIVE GLASS PREVIEW CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFCBB292),
                                Color(0xFF8E7356),
                                Color(0xFF352B20)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Liquid Glass Floating Capsule
                val glassShape = RoundedCornerShape(32.dp)
                val glassAlpha = transparency.coerceIn(0.05f, 0.85f)
                val borderAlpha = (refractionAmount / 50f).coerceIn(0.15f, 0.9f)
                val topLightAlpha = (refractionHeight / 50f).coerceIn(0.2f, 0.95f)

                val glassBg = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (glassAlpha + 0.1f).coerceAtMost(0.9f)),
                        Color.Black.copy(alpha = (1f - glassAlpha).coerceIn(0.1f, 0.6f))
                    )
                )

                val glassBorder = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = topLightAlpha),
                        Color.White.copy(alpha = borderAlpha * 0.3f),
                        Color.Black.copy(alpha = 0.4f)
                    )
                )

                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(68.dp)
                        .clip(glassShape)
                        .background(glassBg)
                        .border(1.5.dp, glassBorder, glassShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Geometric Shapes: Square, Circle, Triangle
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                    ) {
                        Text("□", color = Color.White.copy(alpha = 0.9f), fontSize = 24.sp, fontWeight = FontWeight.Light)
                        Text("○", color = Color.White.copy(alpha = 0.9f), fontSize = 24.sp, fontWeight = FontWeight.Light)
                        Text("△", color = Color.White.copy(alpha = 0.9f), fontSize = 24.sp, fontWeight = FontWeight.Light)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // --- EASY / ADVANCED SEGMENTED TAB ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1C1C1E))
                    .padding(3.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (mode == "easy") Color.White.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable {
                                mode = "easy"
                                settingsManager.glassMode = "easy"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Easy",
                            color = if (mode == "easy") Color.White else Color.White.copy(alpha = 0.4f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (mode == "advanced") Color.White.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable {
                                mode = "advanced"
                                settingsManager.glassMode = "advanced"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Advanced",
                            color = if (mode == "advanced") Color.White else Color.White.copy(alpha = 0.4f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SettingsSectionHeader("LIQUID GLASS PROPERTIES")

            SettingsGroup {
                if (mode == "easy") {
                    // Transparency
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transparency", color = Color.White, fontSize = 14.sp)
                            Text("${(transparency * 100).toInt()}%", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = transparency,
                            onValueChange = {
                                transparency = it
                                settingsManager.glassTransparency = it
                            },
                            valueRange = 0.05f..0.80f
                        )
                    }

                    SettingsDivider()

                    // Blur
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Blur", color = Color.White, fontSize = 14.sp)
                            Text("${(blurRadius * 100).toInt()}%", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = blurRadius,
                            onValueChange = {
                                blurRadius = it
                                settingsManager.glassBlurRadius = it
                            },
                            valueRange = 0.0f..1.0f
                        )
                    }
                } else {
                    // ADVANCED MODE SLIDERS
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transparency", color = Color.White, fontSize = 14.sp)
                            Text("${(transparency * 100).toInt()}%", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = transparency,
                            onValueChange = {
                                transparency = it
                                settingsManager.glassTransparency = it
                            },
                            valueRange = 0.05f..0.80f
                        )
                    }

                    SettingsDivider()

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Blur Radius", color = Color.White, fontSize = 14.sp)
                            Text("${(blurRadius * 100).toInt()}%", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = blurRadius,
                            onValueChange = {
                                blurRadius = it
                                settingsManager.glassBlurRadius = it
                            },
                            valueRange = 0.0f..1.0f
                        )
                    }

                    SettingsDivider()

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Refraction Height", color = Color.White, fontSize = 14.sp)
                            Text("${refractionHeight.toInt()} dp", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = refractionHeight,
                            onValueChange = {
                                refractionHeight = it
                                settingsManager.glassRefractionHeight = it
                            },
                            valueRange = 0f..50f
                        )
                    }

                    SettingsDivider()

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Refraction Amount", color = Color.White, fontSize = 14.sp)
                            Text("${refractionAmount.toInt()} dp", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = refractionAmount,
                            onValueChange = {
                                refractionAmount = it
                                settingsManager.glassRefractionAmount = it
                            },
                            valueRange = 0f..50f
                        )
                    }
                }

                SettingsDivider()

                SettingsToggleRow(
                    title = "Enable Depth",
                    subtitle = if (!depthEnabled) "Disabled by current Graphic preset" else "Full 3D refractive depth enabled",
                    checked = depthEnabled,
                    onCheckedChange = {
                        depthEnabled = it
                        settingsManager.glassDepthEnabled = it
                    }
                )
            }
        }
    }
}
