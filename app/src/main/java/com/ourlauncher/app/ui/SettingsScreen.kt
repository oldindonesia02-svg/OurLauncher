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
   HOME SCREEN SETTINGS (Bottom Sheet Popup)
   ───────────────────────────────────────────── */
@Composable
fun HomeScreenSettingsSheet(
    onDismiss: () -> Unit = {},
    onOpenMoreSettings: () -> Unit = {},
    selectedEffect: String = "Crossfade",
    onEffectSelect: (String) -> Unit = {}
) {
    var showLabel by remember { mutableStateOf(true) }
    var liquidFolder by remember { mutableStateOf(true) }
    var showTransitionPicker by remember { mutableStateOf(false) }

    if (showTransitionPicker) {
        TransitionEffectsSheet(
            selectedEffect = selectedEffect,
            onEffectSelect = {
                onEffectSelect(it)
                showTransitionPicker = false
            },
            onDismiss = { showTransitionPicker = false }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF1C1C1E))
                .clickable(enabled = false) {}
                .padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "✕",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Home screen settings",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup {
                SettingsNavRow("Transition effects") { showTransitionPicker = true }
                SettingsDivider()
                SettingsNavRow("Set default screen")
                SettingsDivider()
                SettingsValueRow(
                    title = "Show label",
                    value = if (showLabel) "On" else "Off",
                    onClick = { showLabel = !showLabel }
                )
                SettingsDivider()
                SettingsToggleRow(
                    title = "Liquid folder",
                    checked = liquidFolder,
                    onCheckedChange = { liquidFolder = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsGroup {
                SettingsLinkRow("Regenerate all icons", onClick = {})
                SettingsDivider()
                SettingsLinkRow("More settings", onClick = onOpenMoreSettings)
            }
        }
    }
}

/* ─────────────────────────────────────────────
   TRANSITION EFFECTS PICKER
   ───────────────────────────────────────────── */
@Composable
fun TransitionEffectsSheet(
    selectedEffect: String = "Crossfade",
    onEffectSelect: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val effects = listOf("Slide", "Crossfade", "Tumble", "Rotate", "Cube")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF1C1C1E))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Text(
                text = "Transition effects",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                effects.forEach { effect ->
                    val isSelected = effect.lowercase() == selectedEffect.lowercase()
                    Box(
                        modifier = Modifier
                            .size(width = 100.dp, height = 130.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF2C2C2E))
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.dp, Color(0xFF0A84FF), RoundedCornerShape(18.dp))
                                } else Modifier
                            )
                            .clickable { onEffectSelect(effect) }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when (effect) {
                                    "Slide" -> "❙ ❙ ❙"
                                    "Crossfade" -> "❨ ❩"
                                    "Tumble" -> "⬞"
                                    "Rotate" -> "⟳"
                                    else -> "🧊"
                                },
                                color = if (isSelected) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.6f),
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = effect,
                                color = if (isSelected) Color(0xFF0A84FF) else Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A84FF))
                    .clickable { onDismiss() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Done", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ─────────────────────────────────────────────
   FULL SETTINGS SCREEN WITH ALL SUB-PAGES
   ───────────────────────────────────────────── */
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    dockRadius: Float = 32f,
    onDockRadiusChange: (Float) -> Unit = {},
    showDockBg: Boolean = true,
    onShowDockBgChange: (Boolean) -> Unit = {},
    searchOffset: Float = 0f,
    onSearchOffsetChange: (Float) -> Unit = {}
) {
    var currentSubPage by remember { mutableStateOf("main") }

    var disableWhatsNew by remember { mutableStateOf(false) }
    var showAssistant by remember { mutableStateOf(true) }
    var lockScreen by remember { mutableStateOf(false) }
    var fakeFingerprint by remember { mutableStateOf(false) }
    var graphicsLevel by remember { mutableStateOf("Medium") }

    var selectedFont by remember { mutableStateOf("sans-serif") }

    var animEnabled by remember { mutableStateOf(true) }
    var advancedTexture by remember { mutableStateOf(false) }
    var animDuration by remember { mutableStateOf(300f) }

    var posX1 by remember { mutableStateOf(0.25f) }
    var posY1 by remember { mutableStateOf(0.50f) }
    var posX2 by remember { mutableStateOf(0.00f) }
    var posY2 by remember { mutableStateOf(1.00f) }

    var widthX1 by remember { mutableStateOf(0.15f) }
    var widthY1 by remember { mutableStateOf(0.10f) }
    var widthX2 by remember { mutableStateOf(0.15f) }
    var widthY2 by remember { mutableStateOf(1.00f) }

    var heightX1 by remember { mutableStateOf(0.30f) }
    var heightY1 by remember { mutableStateOf(0.10f) }
    var heightX2 by remember { mutableStateOf(0.15f) }
    var heightY2 by remember { mutableStateOf(1.00f) }

    var cornerX1 by remember { mutableStateOf(0.30f) }
    var cornerY1 by remember { mutableStateOf(0.00f) }
    var cornerX2 by remember { mutableStateOf(1.23f) }
    var cornerY2 by remember { mutableStateOf(0.26f) }

    var iconTab by remember { mutableStateOf("General") }
    var iconThemeStyle by remember { mutableStateOf("Standard") }
    var themeType by remember { mutableStateOf("Highlight") }
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
                            BezierCurveCanvas(x1 = posX1, y1 = posY1, x2 = posX2, y2 = posY2)
                            Spacer(modifier = Modifier.height(16.dp))
                            CurveSliderRow("Initial Tension (X1)", "Delays the start of movement", posX1) { posX1 = it }
                            CurveSliderRow("Initial Velocity (Y1)", "Controls initial burst of speed", posY1) { posY1 = it }
                            CurveSliderRow("Final Tension (X2)", "Delays the end of movement", posX2) { posX2 = it }
                            CurveSliderRow("Final Velocity (Y2)", "Values > 1.0 create overshoot/bounce", posY2) { posY2 = it }
                        }
                    }

                    SettingsSectionHeader("WIDTH SCALING CURVE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BezierCurveCanvas(x1 = widthX1, y1 = widthY1, x2 = widthX2, y2 = widthY2)
                            Spacer(modifier = Modifier.height(16.dp))
                            CurveSliderRow("Initial Tension (X1)", "Delays the start of width growth", widthX1) { widthX1 = it }
                            CurveSliderRow("Initial Velocity (Y1)", "Controls initial burst of width growth", widthY1) { widthY1 = it }
                            CurveSliderRow("Final Tension (X2)", "Delays the en
