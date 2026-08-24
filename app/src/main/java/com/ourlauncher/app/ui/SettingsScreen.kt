package com.ourlauncher.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.IconPackInfo

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    dockRadius: Float = 32f,
    onDockRadiusChange: (Float) -> Unit = {},
    showDockBg: Boolean = true,
    onShowDockBgChange: (Boolean) -> Unit = {},
    searchOffset: Float = 0f,
    onSearchOffsetChange: (Float) -> Unit = {},
    iconSize: Float = 54f,
    onIconSizeChange: (Float) -> Unit = {},
    iconCornerRadius: Float = 25f,
    onIconCornerRadiusChange: (Float) -> Unit = {},
    iconOpacity: Float = 1.0f,
    onIconOpacityChange: (Float) -> Unit = {},
    installedIconPacks: List<IconPackInfo> = emptyList(),
    selectedIconPack: String = "default",
    onIconPackSelect: (String) -> Unit = {},
    swipeUp: String = "drawer",
    onSwipeUpChange: (String) -> Unit = {},
    swipeDown: String = "none",
    onSwipeDownChange: (String) -> Unit = {},
    swipeLeft: String = "none",
    onSwipeLeftChange: (String) -> Unit = {},
    swipeRight: String = "none",
    onSwipeRightChange: (String) -> Unit = {}
) {
    var currentSubPage by remember { mutableStateOf("main") }
    var font by remember { mutableStateOf("sans-serif") }
    var animDur by remember { mutableStateOf(300f) }
    var px1 by remember { mutableStateOf(0.25f) }
    var py1 by remember { mutableStateOf(0.5f) }
    var px2 by remember { mutableStateOf(0f) }
    var py2 by remember { mutableStateOf(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                text = currentSubPage.replaceFirstChar { it.uppercase() },
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
                "icons" -> {
                    SettingsSectionHeader("ICON SIZE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Size: ${iconSize.toInt()} dp", color = Color.White)
                            Slider(value = iconSize, onValueChange = onIconSizeChange, valueRange = 40f..72f)
                        }
                    }

                    SettingsSectionHeader("SHAPE & CORNER RADIUS")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Corner Radius: ${iconCornerRadius.toInt()}%", color = Color.White)
                            Slider(value = iconCornerRadius, onValueChange = onIconCornerRadiusChange, valueRange = 0f..50f)
                        }
                    }

                    SettingsSectionHeader("ICON OPACITY")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Opacity: ${(iconOpacity * 100).toInt()}%", color = Color.White)
                            Slider(value = iconOpacity, onValueChange = onIconOpacityChange, valueRange = 0.2f..1.0f)
                        }
                    }

                    SettingsSectionHeader("ICON PACK")
                    SettingsGroup {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onIconPackSelect("default") }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedIconPack == "default",
                                onClick = { onIconPackSelect("default") },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                            )
                            Text(text = "Default System Icons", color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                        }

                        installedIconPacks.forEach { pack ->
                            SettingsDivider()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onIconPackSelect(pack.packageName) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedIconPack == pack.packageName,
                                    onClick = { onIconPackSelect(pack.packageName) },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                                )
                                Text(text = pack.label, color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
                "appearance" -> {
                    SettingsSectionHeader("FONT FAMILY")
                    SettingsGroup {
                        listOf("sans-serif", "sans-serif-medium", "ABeeZee", "ADLaM Display").forEach { f ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { font = f }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = font == f, onClick = { font = f }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF)))
                                Text(text = f, color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
                "animation" -> {
                    SettingsSectionHeader("SPEED & TIMING")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Duration: ${animDur.toInt()} ms", color = Color.White)
                            Slider(value = animDur, onValueChange = { animDur = it }, valueRange = 100f..800f)
                        }
                    }
                    SettingsSectionHeader("POSITION BEZIER CURVE")
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            BezierCanvas(px1, py1, px2, py2)
                            CurveSlider("Tension X1", px1) { px1 = it }
                            CurveSlider("Velocity Y1", py1) { py1 = it }
                            CurveSlider("Tension X2", px2) { px2 = it }
                            CurveSlider("Velocity Y2", py2) { py2 = it }
                        }
                    }
                }
                "dock" -> {
                    SettingsGroup {
                        SettingsToggleRow("Show dock background", null, showDockBg, onShowDockBgChange)
                        SettingsDivider()
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Dock Radius: ${dockRadius.toInt()}dp", color = Color.White)
                            Slider(value = dockRadius, onValueChange = onDockRadiusChange, valueRange = 8f..50f)
                        }
                    }
                }
                "swipe" -> {
                    SwipeActionPicker("Swipe Up", swipeUp, onSwipeUpChange)
                    SwipeActionPicker("Swipe Down", swipeDown, onSwipeDownChange)
                    SwipeActionPicker("Swipe Left", swipeLeft, onSwipeLeftChange)
                    SwipeActionPicker("Swipe Right", swipeRight, onSwipeRightChange)
                }
                else -> {
                    SettingsSectionHeader("CUSTOMIZATION")
                    SettingsGroup {
                        SettingsNavRow("Appearance", "Theme & Fonts") { currentSubPage = "appearance" }
                        SettingsDivider()
                        SettingsNavRow("App icons", "Shape, Size & Icon Pack") { currentSubPage = "icons" }
                        SettingsDivider()
                        SettingsNavRow("App Open Animation", "Duration & Bezier Curves") { currentSubPage = "animation" }
                        SettingsDivider()
                        SettingsNavRow("Dock", "Padding & Corner Radius") { currentSubPage = "dock" }
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

@Composable
fun SwipeActionPicker(label: String, selected: String, onSelect: (String) -> Unit) {
    val options = listOf("none" to "None", "drawer" to "Open App Drawer", "settings" to "Open Settings")
    SettingsSectionHeader(label.uppercase())
    SettingsGroup {
        options.forEach { (value, displayName) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(value) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == value,
                    onClick = { onSelect(value) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                )
                Text(
                    text = displayName,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BezierCanvas(x1: Float, y1: Float, x2: Float, y2: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141418))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cp1 = Offset(w * x1, h * (1f - y1))
            val cp2 = Offset(w * x2, h * (1f - y2))
            val path = Path().apply {
                moveTo(0f, h)
                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, w, 0f)
            }
            drawPath(path = path, color = Color(0xFF0A84FF), style = Stroke(width = 4f))
            drawCircle(color = Color.Green, radius = 6f, center = cp1)
            drawCircle(color = Color.Red, radius = 6f, center = cp2)
        }
    }
}

@Composable
fun CurveSlider(label: String, valIn: Float, onChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.White, fontSize = 13.sp)
            Text(text = String.format("%.2f", valIn), color = Color(0xFF0A84FF), fontSize = 13.sp)
        }
        Slider(value = valIn, onValueChange = onChange, valueRange = 0f..1.5f)
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C1C1E))
            .padding(4.dp),
        content = content
    )
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .height(0.5.dp)
            .background(Color.White.copy(alpha = 0.1f))
    )
}

@Composable
fun SettingsNavRow(title: String, subtitle: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            }
        }
        Text(text = "›", color = Color.White.copy(alpha = 0.3f), fontSize = 20.sp)
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
