package com.ourlauncher.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.GlassMode
import com.ourlauncher.app.SettingsManager
import com.ourlauncher.app.ui.components.LiquidGlassSurface
import com.ourlauncher.app.ui.components.liquidGlassEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassScreen(
    settings: SettingsManager,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liquid Glass Engine", color = Color.White, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Live Preview Box
            Text(
                text = "REAL-TIME PREVIEW",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            LiquidGlassSurface(
                settings = settings,
                cornerRadius = 28.dp,
                isDarkTheme = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dynamic Refraction",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Mode: ${settings.glassMode.name}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Specular: ${(settings.specularHighlight * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Blur: ${settings.glassBlurRadius.toInt()}px", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Glass Mode Switcher Matrix
            Text(
                text = "GLASS MATERIAL MODE",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassMode.values().forEach { mode ->
                    val isSelected = settings.glassMode == mode
                    val targetBg = if (isSelected) Color(0xFF007AFF) else Color.White.copy(alpha = 0.08f)
                    val bgColor by animateColorAsState(targetBg, label = "modeBg")

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bgColor)
                            .clickable { settings.glassMode = mode },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Sliders Section
            Text(
                text = "OPTICAL PARAMETERS",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassEffect(settings, cornerRadius = 24.dp, isDarkTheme = true)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Blur Radius
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Hardware Blur Radius", color = Color.White, fontSize = 14.sp)
                        Text("${settings.glassBlurRadius.toInt()} dp", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                    Slider(
                        value = settings.glassBlurRadius,
                        onValueChange = { settings.glassBlurRadius = it },
                        valueRange = 0f..60f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF007AFF)
                        )
                    )
                }

                // Specular Highlights
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Specular Reflection Sheen", color = Color.White, fontSize = 14.sp)
                        Text("${(settings.specularHighlight * 100).toInt()}%", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                    Slider(
                        value = settings.specularHighlight,
                        onValueChange = { settings.specularHighlight = it },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF007AFF)
                        )
                    )
                }

                // Transparency Alpha
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Glass Base Transparency", color = Color.White, fontSize = 14.sp)
                        Text("${(settings.glassTransparency * 100).toInt()}%", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                    Slider(
                        value = settings.glassTransparency,
                        onValueChange = { settings.glassTransparency = it },
                        valueRange = 0.1f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF007AFF)
                        )
                    )
                }
            }

            // Toggles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassEffect(settings, cornerRadius = 24.dp, isDarkTheme = true)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rainbow Edge Dispersion", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = settings.enableRainbowSheen,
                        onCheckedChange = { settings.enableRainbowSheen = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Inner Depth Gradient", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = settings.glassDepthEnabled,
                        onCheckedChange = { settings.glassDepthEnabled = it }
                    )
                }
            }
        }
    }
}
