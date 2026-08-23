package com.ourlauncher.app.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ─────────────────────────────────────────────
   FULL SETTINGS SCREEN WITH ALL SUB-PAGES
   ───────────────────────────────────────────── */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    dockRadius: Float = 32f,
    onDockRadiusChange: (Float) -> Unit = {},
    showDockBg: Boolean = true,
    onShowDockBgChange: (Boolean) -> Unit = {},
    searchOffset: Float = 0f,
    onSearchOffsetChange: (Float) -> Unit = {}
) {
    // Current Active Sub-page ("main", "appearance", "animation", "icons", "dock", "search_pos")
    var currentSubPage by remember { mutableStateOf("main") }

    // Toggle States
    var disableWhatsNew by remember { mutableStateOf(false) }
    var showAssistant by remember { mutableStateOf(true) }
    var lockScreen by remember { mutableStateOf(false) }
    var fakeFingerprint by remember { mutableStateOf(false) }
    var graphicsLevel by remember { mutableStateOf("Medium") }

    // Appearance & Font States
    var selectedFont by remember { mutableStateOf("sans-serif") }

    // App Open Animation Bezier Curve States
    var animEnabled by remember { mutableStateOf(true) }
    var advancedTexture by remember { mutableStateOf(false) }
    var animDuration by remember { mutableStateOf(300f) }
    var tensionX1 by remember { mutableStateOf(0.25f) }
    var velocityY1 by remember { mutableStateOf(0.50f) }
    var tensionX2 by remember { mutableStateOf(0.00f) }
    var velocityY2 by remember { mutableStateOf(1.00f) }

    // Icon Customization States
    var iconTab by remember { mutableStateOf("General") }
    var iconThemeStyle by remember { mutableStateOf("Standard") }
    var iconCornerRadius by remember { mutableStateOf(27f) }
    var blurDp by remember { mutableStateOf(0.5f) }
    var falloffVal by remember { mutableStateOf(1.5f) }
    var widthDp by remember { mutableStateOf(1.5f) }
    var intensityVal by remember { mutableStateOf(100f) }
    var angleVal by remember { mutableStateOf(75f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable {
                        if (currentSubPage != "main") {
                            currentSubPage = "main"
                        } else {
                            onBack()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", color = Color(0xFF0A84FF), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = when (currentSubPage) {
                    "appearance" -> "Appearance"
                    "animation" -> "App Open Animation"
                    "icons" -> "App icons"
                    "dock" -> "Dock"
                    "search_pos" -> "Search Bar Position"
                    else -> "Settings"
                },
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when (currentSubPage) {

            /* ── 1. APPEARANCE & FONT FAMILY SCREEN (Screenshot #6) ── */
            "appearance" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    SettingsSectionHeader("APPEARANCE")
                    SettingsGroup {
                        SettingsNavRow("Theme", "Dark") {}
                        SettingsDivider()
                        SettingsNavRow("Color Tone", "Tonal Spot") {}
                    }

                    SettingsSectionHeader("FONT FAMILY")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Default", color = Color.White, fontSize = 14.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF2C2C2E))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Epstein Files Libre Barcode", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("🔍 Search font...", color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            val fonts = listOf("sans-serif", "sans-serif-medium", "sans-serif-condensed", "ABeeZee", "ADLaM Display")
                            fonts.forEach { font ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedFont = font }
                                        .padding(vertical = 8.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedFont == font,
                                        onClick = { selectedFont = font },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF), unselectedColor = Color.White.copy(alpha = 0.4f))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = font, color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }

            /* ── 2. APP OPEN ANIMATION BEZIER GRAPH SCREEN (Screenshots #9 & #10) ── */
            "animation" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    SettingsSectionHeader("APP OPEN ANIMATION CONFIGURATION")
                    SettingsGroup {
                        SettingsToggleRow("Enable Animation", "If disabled, apps will launch instantly without scaling", animEnabled) { animEnabled = it }
                        SettingsDivider()
                        SettingsToggleRow("Advanced Texture", "Scales down and blurs workspace in sync with app sizing", advancedTexture) { advancedTexture = it }
                    }

                    SettingsSectionHeader("SPEED & TIMING")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Animation Duration", color = Color.White, fontSize = 15.sp)
                                Text("${animDuration.toInt()} ms", color = Color(0xFF0A84FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = animDuration,
                                onValueChange = { animDuration = it },
                                valueRange = 100f..800f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF))
                            )
                        }
                    }

                    SettingsSectionHeader("POSITION MOVEMENT CURVE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // BEZIER CURVE GRAPH CANVAS
                            BezierCurveCanvas(x1 = tensionX1, y1 = velocityY1, x2 = tensionX2, y2 = velocityY2)

                            Spacer(modifier = Modifier.height(16.dp))

                            // X1 Slider
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Initial Tension (X1)", color = Color.White, fontSize = 15.sp)
                                Text(String.format("%.2f", tensionX1), color = Color(0xFF0A84FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(value = tensionX1, onValueChange = { tensionX1 = it }, valueRange = 0f..1f, colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF)))

                            // Y1 Slider
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Initial Velocity (Y1)", color = Color.White, fontSize = 15.sp)
                                Text(String.format("%.2f", velocityY1), color = Color(0xFF0A84FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(value = velocityY1, onValueChange = { velocityY1 = it }, valueRange = 0f..1f, colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF)))

                            // X2 Slider
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Final Tension (X2)", color = Color.White, fontSize = 15.sp)
                                Text(String.format("%.2f", tensionX2), color = Color(0xFF0A84FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(value = tensionX2, onValueChange = { tensionX2 = it }, valueRange = 0f..1f, colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF)))

                            // Y2 Slider
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Final Velocity (Y2)", color = Color.White, fontSize = 15.sp)
                                Text(String.format("%.2f", velocityY2), color = Color(0xFF0A84FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(value = velocityY2, onValueChange = { velocityY2 = it }, valueRange = 0f..1.5f, colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF)))
                        }
                    }
                }
            }

            /* ── 3. APP ICONS CUSTOMIZE SHEET (Screenshots #7 & #8) ── */
            "icons" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1C1C1E))
                            .padding(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Customize", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(16.dp))

                            // General vs Themes Tabs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2C2C2E))
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (iconTab == "General") Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable { iconTab = "General" }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("General", color = Color.White, fontSize = 14.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (iconTab == "Themes") Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable { iconTab = "Themes" }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Themes", color = Color.White, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (iconTab == "General") {
                                // General Tab Icon Styles (Standard, Dark, Transparent, Tinted)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    val styles = listOf("Standard", "Dark", "Transparent", "Tinted")
                                    styles.forEach { style ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Box(
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(
                                                        when (style) {
                                                            "Dark" -> Color(0xFF121212)
                                                            "Transparent" -> Color.White.copy(alpha = 0.15f)
                                                            "Tinted" -> Color(0xFF900C3F)
                                                            else -> Color(0xFF333333)
                                                        }
                                                    )
                                                    .border(
                                                        if (iconThemeStyle == style) 2.dp else 1.dp,
                                                        if (iconThemeStyle == style) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.2f),
                                                        RoundedCornerShape(16.dp)
                                                    )
                                                    .clickable { iconThemeStyle = style },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("⬡", color = Color.White, fontSize = 20.sp)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(style, color = if (iconThemeStyle == style) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Corner Radius", color = Color.White, fontSize = 15.sp)
                                    Text("${iconCornerRadius.toInt()}%", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                                }
                                Slider(
                                    value = iconCornerRadius,
                                    onValueChange = { iconCornerRadius = it },
                                    valueRange = 0f..50f,
                                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF))
                                )
                            } else {
                                // Themes Tab Tweak Settings
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Tweak Settings", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                                    Spacer(mod
