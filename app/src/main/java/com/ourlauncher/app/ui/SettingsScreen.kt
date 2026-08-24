package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
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

    var widthX1 by remember { mutableStateOf(settingsManager.widthCurveX1) }
    var widthY1 by remember { mutableStateOf(settingsManager.widthCurveY1) }
    var widthX2 by remember { mutableStateOf(settingsManager.widthCurveX2) }
    var widthY2 by remember { mutableStateOf(settingsManager.widthCurveY2) }

    var heightX1 by remember { mutableStateOf(settingsManager.heightCurveX1) }
    var heightY1 by remember { mutableStateOf(settingsManager.heightCurveY1) }
    var heightX2 by remember { mutableStateOf(settingsManager.heightCurveX2) }
    var heightY2 by remember { mutableStateOf(settingsManager.heightCurveY2) }

    var cornerX1 by remember { mutableStateOf(settingsManager.cornerCurveX1) }
    var cornerY1 by remember { mutableStateOf(settingsManager.cornerCurveY1) }
    var cornerX2 by remember { mutableStateOf(settingsManager.cornerCurveX2) }
    var cornerY2 by remember { mutableStateOf(settingsManager.cornerCurveY2) }

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
                                Text("${animDuration.toInt()} ms", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(value = animDuration, onValueChange = { animDuration = it; settingsManager.animDuration = it }, valueRange = 100f..800f)
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

                    SettingsSectionHeader("WIDTH SCALING CURVE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BezierCanvas(widthX1, widthY1, widthX2, widthY2)
                            CurveSlider("Initial Tension (X1)", "Delays width growth", widthX1) { widthX1 = it; settingsManager.widthCurveX1 = it }
                            CurveSlider("Initial Velocity (Y1)", "Width speed burst", widthY1) { widthY1 = it; settingsManager.widthCurveY1 = it }
                            CurveSlider("Final Tension (X2)", "Delays end width", widthX2) { widthX2 = it; settingsManager.widthCurveX2 = it }
                            CurveSlider("Final Velocity (Y2)", "Width overshoot", widthY2) { widthY2 = it; settingsManager.widthCurveY2 = it }
                        }
                    }

                    SettingsSectionHeader("HEIGHT SCALING CURVE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BezierCanvas(heightX1, heightY1, heightX2, heightY2)
                            CurveSlider("Initial Tension (X1)", "Delays height growth", heightX1) { heightX1 = it; settingsManager.heightCurveX1 = it }
                            CurveSlider("Initial Velocity (Y1)", "Height speed burst", heightY1) { heightY1 = it; settingsManager.heightCurveY1 = it }
                            CurveSlider("Final Tension (X2)", "Delays end height", heightX2) { heightX2 = it; settingsManager.heightCurveY2 = it }
                            CurveSlider("Final Velocity (Y2)", "Height overshoot", heightY2) { heightY2 = it; settingsManager.heightCurveY2 = it }
                        }
                    }

                    SettingsSectionHeader("CORNER RADIUS TRANSITION CURVE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BezierCanvas(cornerX1, cornerY1, cornerX2, cornerY2)
                            CurveSlider("Initial Tension (X1)", "Delays corner transition", cornerX1) { cornerX1 = it; settingsManager.cornerCurveX1 = it }
                            CurveSlider("Initial Velocity (Y1)", "Corner change speed", cornerY1) { cornerY1 = it; settingsManager.cornerCurveY1 = it }
                            CurveSlider("Final Tension (X2)", "Delays corner end", cornerX2) { cornerX2 = it; settingsManager.cornerCurveX2 = it }
                            CurveSlider("Final Velocity (Y2)", "Corner overshoot", cornerY2) { cornerY2 = it; settingsManager.cornerCurveY2 = it }
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
                                Text("Light Angle: ${lensAngle.toInt()}°", color = Color.White, fontSize = 13.sp)
                                Slider(value = lensAngle, onValueChange = { lensAngle = it; settingsManager.lensAngle = it }, valueRange = 0f..360f)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Light Intensity: ${(lensIntensity * 100).toInt()}%", color = Color.White, fontSize = 13.sp)
                                Slider(value = lensIntensity, onValueChange = { lensIntensity = it; settingsManager.lensIntensity = it }, valueRange = 0.1f..1.0f)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Stroke Width: ${String.format("%.1f", lensStroke)} dp", color = Color.White, fontSize = 13.sp)
                                Slider(value = lensStroke, onValueChange = { lensStroke = it; settingsManager.lensStrokeWidth = it }, valueRange = 0.5f..3.0f)
                            }
                        }
                    }

                    SettingsSectionHeader("ICON SIZE & SHAPE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Size: ${iconSize.toInt()} dp", color = Color.White, fontSize = 13.sp)
                            Slider(value = iconSize, onValueChange = { iconSize = it; settingsManager.iconSize = it }, valueRange = 40f..72f)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Corner Radius: ${iconCornerRadius.toInt()}%", color = Color.White, fontSize = 13.sp)
                            Slider(value = iconCornerRadius, onValueChange = { iconCornerRadius = it; settingsManager.iconCornerRadius = it }, valueRange = 0f..50f)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Opacity: ${(iconOpacity * 100).toInt()}%", color = Color.White, fontSize = 13.sp)
                            Slider(value = iconOpacity, onValueChange = { iconOpacity = it; settingsManager.iconOpacity = it }, valueRange = 0.2f..1.0f)
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
                        SettingsNavRow("App icons", "Themes, Lens Light, Shape & Size") { currentSubPage = "icons" }
                        SettingsDivider()
                        SettingsNavRow("App Open Animation", "Duration & Bezier Curves") { currentSubPage = "animation" }
                        SettingsDivider()
                        SettingsNavRow("Dock", "Padding, Gap and Corner Radius") { currentSubPage = "dock_sheet" }
                        SettingsDivider()
                        SettingsNavRow("Liquid Glass", "Adjust transparency, blur and lens refraction") { currentSubPage = "liquid_glass" }
                        SettingsDivider()
                        SettingsNavRow("Search Bar Position", "Adjust the vertical offset of the search pill") { currentSubPage = "search_sheet" }
                    }
                    SettingsSectionHeader("ACTIONS")
                    SettingsGroup {
                        SettingsNavRow("Swipe actions", "Customize gesture swipe behaviors") { currentSubPage = "swipe" }
                    }
                }
            }
        }
    }
}
