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
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.BottomCenter) {
            DockAdjustmentSheet(
                settingsManager = settingsManager,
                onDismiss = { currentSubPage = "main" },
                onSwitchToSearch = { currentSubPage = "search_sheet" }
            )
        }
        return
    }

    if (currentSubPage == "search_sheet") {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.BottomCenter) {
            SearchBarAdjustmentSheet(
                settingsManager = settingsManager,
                onDismiss = { currentSubPage = "main" },
                onSwitchToDock = { currentSubPage = "dock_sheet" }
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable {
                        if (currentSubPage != "main") currentSubPage = "main" else onBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "‹", color = Color(0xFF0A84FF), fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
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
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                                )
                                Text(label, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }

                "animation" -> {
                    SettingsSectionHeader("APP OPEN ANIMATION CONFIGURATION")
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

                    SettingsSectionHeader("SPEED & TIMING")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Animation Duration", color = Color.White, fontSize = 14.sp)
                                Text("${animDuration.toInt()} ms", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LiquidGlassSlider(
                                value = animDuration,
                                onValueChange = { animDuration = it; settingsManager.animDuration = it },
                                valueRange = 100f..800f
                            )
                        }
                    }

                    SettingsSectionHeader("POSITION MOVEMENT CURVE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BezierCanvas(posX1, posY1, posX2, posY2)
                            CurveSlider("Initial Tension (X1)", "Delays start", posX1) { posX1 = it; settingsManager.posCurveX1 = it }
                            CurveSlider("Initial Velocity (Y1)", "Controls speed burst", posY1) { posY1 = it; settingsManager.posCurveY1 = it }
                            CurveSlider("Final Tension (X2)", "Delays end", posX2) { posX2 = it; settingsManager.posCurveX2 = it }
                            CurveSlider("Final Velocity (Y2)", "Values > 1.0 overshoot", posY2) { posY2 = it; settingsManager.posCurveY2 = it }
                        }
                    }

                    SettingsSectionHeader("LIVE ANIMATION PREVIEW")
                    SettingsGroup {
                        PhoneMockupPreview(durationMs = animDuration.toInt())
                    }
                }

                "icons" -> {
                    SettingsSectionHeader("GRAPHICS PRESET")
                    SettingsGroup {
                        val presets = listOf("ultra" to "Ultra (Full Blur & Refraction)", "high" to "High", "medium" to "Medium", "low" to "Low (Battery Saver)")
                        presets.forEachIndexed { i, (key, label) ->
                            if (i > 0) SettingsDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { graphicPreset = key; settingsManager.graphicPreset = key }.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = graphicPreset == key, onClick = { graphicPreset = key; settingsManager.graphicPreset = key }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF)))
                                Text(label, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }

                    SettingsSectionHeader("ICON THEME")
                    SettingsGroup {
                        val themes = listOf("standard" to "Standard Colors", "dark" to "Dark (Monochrome)", "transparent" to "Transparent Glass", "tinted" to "Tinted Blue")
                        themes.forEachIndexed { i, (key, label) ->
                            if (i > 0) SettingsDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { iconTheme = key; settingsManager.iconTheme = key }.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = iconTheme == key, onClick = { iconTheme = key; settingsManager.iconTheme = key }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF)))
                                Text(label, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }

                    SettingsSectionHeader("LENS LIGHTING ENGINE")
                    SettingsGroup {
                        SettingsToggleRow("Enable Lens Highlight", "Adds reflective edge light to icons", lensLightEnabled) {
                            lensLightEnabled = it
                            settingsManager.lensLightEnabled = it
                        }
                        if (lensLightEnabled) {
                            SettingsDivider()
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Light Angle", color = Color.White, fontSize = 13.sp)
                                    Text("${lensAngle.toInt()}°", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LiquidGlassSlider(value = lensAngle, onValueChange = { lensAngle = it; settingsManager.lensAngle = it }, valueRange = 0f..360f)

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Light Intensity", color = Color.White, fontSize = 13.sp)
                                    Text("${(lensIntensity * 100).toInt()}%", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LiquidGlassSlider(value = lensIntensity, onValueChange = { lensIntensity = it; settingsManager.lensIntensity = it }, valueRange = 0.1f..1.0f)

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Stroke Width", color = Color.White, fontSize = 13.sp)
                                    Text("${String.format("%.1f", lensStroke)} dp", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LiquidGlassSlider(value = lensStroke, onValueChange = { lensStroke = it; settingsManager.lensStrokeWidth = it }, valueRange = 0.5f..3.0f)
                            }
                        }
                    }

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

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Opacity", color = Color.White, fontSize = 13.sp)
                                Text("${(iconOpacity * 100).toInt()}%", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LiquidGlassSlider(value = iconOpacity, onValueChange = { iconOpacity = it; settingsManager.iconOpacity = it }, valueRange = 0.2f..1.0f)
                        }
                    }

                    SettingsSectionHeader("ICON PACK")
                    SettingsGroup {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onIconPackSelect("default") }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedIconPack == "default",
                                onClick = { onIconPackSelect("default") },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                            )
                            Text("Default System Icons", color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                        installedIconPacks.forEach { pack ->
                            SettingsDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onIconPackSelect(pack.packageName) }.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedIconPack == pack.packageName,
                                    onClick = { onIconPackSelect(pack.packageName) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                                )
                                Text(pack.label, color = Color.White, fontSize = 15.sp, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }

                else -> {
                    SettingsSectionHeader("CUSTOMIZATION")
                    SettingsGroup {
                        SettingsNavRow("Desktop Grid", "Configure Columns & Rows (4x5, 5x5, 5x6)") { currentSubPage = "grid" }
                        SettingsDivider()
                        SettingsNavRow("App icons", "Themes, Lens Light, Shape & Size") { currentSubPage = "icons" }
                        SettingsDivider()
                        SettingsNavRow("App Open Animation", "Duration & Bezier Curves") { currentSubPage = "animation" }
                        SettingsDivider()
                        SettingsNavRow("Dock", "Padding, Gap and Corner Radius") { currentSubPage = "dock_sheet" }
                        SettingsDivider()
                        SettingsNavRow("Liquid Glass", "Adjust transparency, blur and lens refraction") { currentSubPage = "liquid_glass" }
                        SettingsNavRow("Search Bar Position", "Adjust the vertical offset
