package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.ourlauncher.app.IconPackInfo
import com.ourlauncher.app.SettingsManager

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    settingsManager: SettingsManager,
    installedIconPacks: List<IconPackInfo> = emptyList(),
    selectedIconPack: String = "default",
    onIconPackSelect: (String) -> Unit = {}
) {
    var currentSubPage by remember { mutableStateOf("main") }

    var gridColumns by remember { mutableStateOf(settingsManager.gridColumns) }
    var gridRows by remember { mutableStateOf(settingsManager.gridRows) }

    var iconSize by remember { mutableStateOf(settingsManager.iconSize) }
    var iconCornerRadius by remember { mutableStateOf(settingsManager.iconCornerRadius) }
    var iconOpacity by remember { mutableStateOf(settingsManager.iconOpacity) }

    var iconTheme by remember { mutableStateOf(settingsManager.iconTheme) }
    var lensLightEnabled by remember { mutableStateOf(settingsManager.lensLightEnabled) }
    var lensAngle by remember { mutableStateOf(settingsManager.lensAngle) }
    var lensIntensity by remember { mutableStateOf(settingsManager.lensIntensity) }
    var lensStroke by remember { mutableStateOf(settingsManager.lensStrokeWidth) }
    var graphicPreset by remember { mutableStateOf(settingsManager.graphicPreset) }

    var animEnabled by remember { mutableStateOf(settingsManager.animEnabled) }
    var animAdvancedTexture by remember { mutableStateOf(settingsManager.animAdvancedTexture) }
    var animDuration by remember { mutableStateOf(settingsManager.animDuration) }

    var posX1 by remember { mutableStateOf(settingsManager.posCurveX1) }
    var posY1 by remember { mutableStateOf(settingsManager.posCurveY1) }
    var posX2 by remember { mutableStateOf(settingsManager.posCurveX2) }
    var posY2 by remember { mutableStateOf(settingsManager.posCurveY2) }

    if (currentSubPage == "liquid_glass") {
        LiquidGlassScreen(onBack = { currentSubPage = "main" }, settingsManager = settingsManager)
        return
    }

    if (currentSubPage == "swipe") {
        SwipeActionsScreen(onBack = { currentSubPage = "main" }, settingsManager = settingsManager)
        return
    }

    if (currentSubPage == "dock_sheet") {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
            DockAdjustmentSheet(
                settingsManager = settingsManager,
                onDismiss = { currentSubPage = "main" },
                onSwitchToSearch = { currentSubPage = "search_sheet" }
            )
        }
        return
    }

    if (currentSubPage == "search_sheet") {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
            SearchBarAdjustmentSheet(
                settingsManager = settingsManager,
                onDismiss = { currentSubPage = "main" },
                onSwitchToDock = { currentSubPage = "dock_sheet" }
            )
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onBack() }
            .padding(horizontal = 16.dp, vertical = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        LiquidGlassContainer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clickable(enabled = false) {}
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                if (currentSubPage != "main") currentSubPage = "main" else onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "‹", color = Color(0xFF00E5FF), fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (currentSubPage == "main") "Settings" else currentSubPage.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (currentSubPage) {
                    "grid" -> {
                        SettingsSectionHeader("HOME SCREEN GRID")
                        SettingsGroup {
                            val gridPresets = listOf(
                                (4 to 4) to "4 x 4 (Spacious)",
                                (4 to 5) to "4 x 5 (Default)",
                                (4 to 6) to "4 x 6 (Tall)",
                                (5 to 5) to "5 x 5 (Compact)",
                                (5 to 6) to "5 x 6 (Dense)"
                            )
                            gridPresets.forEachIndexed { i, (pair, label) ->
                                val (c, r) = pair
                                val isSelected = gridColumns == c && gridRows == r
                                if (i > 0) SettingsDivider()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            gridColumns = c
                                            gridRows = r
                                            settingsManager.gridColumns = c
                                            settingsManager.gridRows = r
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            gridColumns = c
                                            gridRows = r
                                            settingsManager.gridColumns = c
                                            settingsManager.gridRows = r
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E5FF))
                                    )
                                    Text(label, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }

                    "animation" -> {
                        SettingsSectionHeader("ANIMATION CONFIG")
                        SettingsGroup {
                            SettingsToggleRow("Enable Animation", "Launch apps with scale animation", animEnabled) {
                                animEnabled = it
                                settingsManager.animEnabled = it
                            }
                            SettingsDivider()
                            SettingsToggleRow("Advanced Texture", "Scales down and blurs workspace", animAdvancedTexture) {
                                animAdvancedTexture = it
                                settingsManager.animAdvancedTexture = it
                            }
                        }

                        SettingsSectionHeader("TIMING")
                        SettingsGroup {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Duration", color = Color.White, fontSize = 14.sp)
                                    Text("${animDuration.toInt()} ms", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LiquidGlassSlider(
                                    value = animDuration,
                                    onValueChange = { animDuration = it; settingsManager.animDuration = it },
                                    valueRange = 100f..800f
                                )
                            }
                        }
                    }

                    "icons" -> {
                        SettingsSectionHeader("ICON SIZE & SHAPE")
                        SettingsGroup {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Size", color = Color.White, fontSize = 13.sp)
                                    Text("${iconSize.toInt()} dp", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LiquidGlassSlider(value = iconSize, onValueChange = { iconSize = it; settingsManager.iconSize = it }, valueRange = 40f..72f)

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Corner Radius", color = Color.White, fontSize = 13.sp)
                                    Text("${iconCornerRadius.toInt()}%", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LiquidGlassSlider(value = iconCornerRadius, onValueChange = { iconCornerRadius = it; settingsManager.iconCornerRadius = it }, valueRange = 0f..50f)
                            }
                        }
                    }

                    else -> {
                        SettingsSectionHeader("CUSTOMIZATION")
                        SettingsGroup {
                            SettingsNavRow("Desktop Grid", "Configure Columns & Rows") { currentSubPage = "grid" }
                            SettingsDivider()
                            SettingsNavRow("App icons", "Shape, Size & Lens Light") { currentSubPage = "icons" }
                            SettingsDivider()
                            SettingsNavRow("App Open Animation", "Duration & Curves") { currentSubPage = "animation" }
                            SettingsDivider()
                            SettingsNavRow("Dock", "Padding & Corner Radius") { currentSubPage = "dock_sheet" }
                            SettingsDivider()
                            SettingsNavRow("Liquid Glass", "Blur & Refraction") { currentSubPage = "liquid_glass" }
                            SettingsDivider()
                            SettingsNavRow("Search Bar Position", "Offset Settings") { currentSubPage = "search_sheet" }
                        }
                        SettingsSectionHeader("ACTIONS")
                        SettingsGroup {
                            SettingsNavRow("Swipe actions", "Gesture behaviors") { currentSubPage = "swipe" }
                        }
                    }
                }
            }
        }
    }
}
