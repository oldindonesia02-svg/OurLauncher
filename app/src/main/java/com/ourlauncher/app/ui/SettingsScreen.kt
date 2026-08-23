package com.ourlauncher.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreenSettingsSheet(onDismiss: () -> Unit = {}, onOpenMoreSettings: () -> Unit = {}) {
    var showLabel by remember { mutableStateOf(true) }
    var liquidFolder by remember { mutableStateOf(true) }
    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.45f)).clickable { onDismiss() }) {
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(Color(0xFF1C1C1E)).padding(16.dp)) {
            Box(Modifier.align(Alignment.CenterHorizontally).size(36.dp, 4.dp).background(Color.White.copy(0.3f), CircleShape))
            Text("Home screen settings", Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 12.dp))
            SettingsGroup {
                SettingsValueRow("Show label", if (showLabel) "On" else "Off") { showLabel = !showLabel }
                SettingsDivider()
                SettingsToggleRow("Liquid folder", null, liquidFolder) { liquidFolder = it }
            }
            Spacer(Modifier.height(12.dp))
            SettingsGroup {
                SettingsLinkRow("More settings") { onOpenMoreSettings() }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    dockRadius: Float = 32f, onDockRadiusChange: (Float) -> Unit = {},
    showDockBg: Boolean = true, onShowDockBgChange: (Boolean) -> Unit = {},
    searchOffset: Float = 0f, onSearchOffsetChange: (Float) -> Unit = {}
) {
    var currentSubPage by remember { mutableStateOf("main") }
    var font by remember { mutableStateOf("sans-serif") }
    var animDur by remember { mutableStateOf(300f) }
    var px1 by remember { mutableStateOf(0.25f) }; var py1 by remember { mutableStateOf(0.5f) }
    var px2 by remember { mutableStateOf(0f) }; var py2 by remember { mutableStateOf(1f) }

    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Row(Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(0.12f)).clickable {
                if (currentSubPage != "main") currentSubPage = "main" else onBack()
            }, contentAlignment = Alignment.Center) { Text("‹", color = Color(0xFF0A84FF), fontSize = 28.sp) }
            Spacer(Modifier.width(12.dp))
            Text(currentSubPage.replaceFirstChar { it.uppercase() }, Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            when (currentSubPage) {
                "appearance" -> {
                    SettingsSectionHeader("FONT FAMILY")
                    SettingsGroup {
                        listOf("sans-serif", "sans-serif-medium", "ABeeZee", "ADLaM Display").forEach { f ->
                            Row(Modifier.fillMaxWidth().clickable { font = f }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = font == f, onClick = { font = f }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF)))
                                Text(f, Color.White, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
                "animation" -> {
                    SettingsSectionHeader("SPEED & TIMING")
                    SettingsGroup {
                        Column(Modifier.padding(16.dp)) {
                            Text("Duration: ${animDur.toInt()} ms", Color.White)
                            Slider(value = animDur, onValueChange = { animDur = it }, valueRange = 100f..800f)
                        }
                    }
                    SettingsSectionHeader("POSITION BEZIER CURVE")
                    SettingsGroup {
                        Column(Modifier.padding(16.dp)) {
                            BezierCanvas(px1, py1, px2, py2)
                            CurveSlider("Tension X1", px1) { px1 = it }
                            CurveSlider("Velocity Y1", py1) { py1 = it }
                            CurveSlider("Tension X2", px2) { px2 = it }
                            CurveSlider("Velocity Y2", py2) { py2 = it }
                        }
                    }
                }
                "icons" -> {
                    SettingsSectionHeader("ICON PACK")
                    SettingsGroup {
                        Column(Modifier.padding(16.dp)) {
                            Text("Corner Radius: ${dockRadius.toInt()}%", Color.White)
                            Slider(value = dockRadius, onValueChange = onDockRadiusChange, valueRange = 0f..50f)
                        }
                    }
                }
                "dock" -> {
                    SettingsGroup {
                        SettingsToggleRow("Show dock background", null, showDockBg, onShowDockBgChange)
                        SettingsDivider()
                        Column(Modifier.padding(16.dp)) {
                            Text("Dock Radius: ${dockRadius.toInt()}dp", Color.White)
                            Slider(value = dockRadius, onValueChange = onDockRadiusChange, valueRange = 8f..50f)
                        }
                    }
                }
                else -> {
                    SettingsSectionHeader("CUSTOMIZATION")
                    SettingsGroup {
                        SettingsNavRow("Appearance", "Theme & Fonts") { currentSubPage = "appearance" }
                        SettingsDivider()
                        SettingsNavRow("App icons", "Shape & Icon Pack") { currentSubPage = "icons" }
                        SettingsDivider()
                        SettingsNavRow("App Open Animation", "Duration & Bezier Curves") { currentSubPage = "animation" }
                        SettingsDivider()
                        SettingsNavRow("Dock", "Padding & Corner Radius") { currentSubPage = "dock" }
                    }
                }
            }
        }
    }
}

@Composable fun BezierCanvas(x1: Float, y1: Float, x2: Float, y2: Float) {
    Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF141418)).padding(12.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            val cp1 = Offset(w * x1, h * (1f - y1)); val cp2 = Offset(w * x2, h * (1f - y2))
            val path = Path().apply { moveTo(0f, h); cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, w, 0f) }
            drawPath(path, Color(0xFF0A84FF), style = Stroke(4f))
            drawCircle(Color.Green, 6f, cp1); drawCircle(Color.Red, 6f, cp2)
        }
    }
}

@Composable fun CurveSlider(label: String, valIn: Float, onChange: (Float) -> Unit) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, Color.White, fontSize = 13.sp); Text(String.format("%.2f", valIn), Color(0xFF0A84FF), fontSize = 13.sp) }
        Slider(value = valIn, onValueChange = onChange, valueRange = 0f..1.5f)
    }
}

@Composable fun SettingsSectionHeader(t: String) { Text(t, Color.White.copy(0.4f), fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 6.dp)) }
@Composable fun SettingsGroup(c: @Composable ColumnScope.() -> Unit) { Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF1C1C1E)).padding(4.dp), content = c) }
@Composable fun SettingsDivider() { Box(Modifier.fillMaxWidth().padding(start = 16.dp).height(0.5.dp).background(Color.White.copy(0.1f))) }
@Composable fun SettingsNavRow(t: String, s: String? = null, onClick: () -> Unit = {}) { Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(t, Color.White, fontSize = 16.sp); if (s != null) Text(s, Color.White.copy(0.4f), fontSize = 12.sp) }; Text("›", Color.White.copy(0.3f), fontSize = 20.sp) } }
@Composable fun SettingsLinkRow(t: String, onClick: () -> Unit = {}) { Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(14.dp)) { Text(t, Color(0xFF0A84FF), fontSize = 16.sp) } }
@Composable fun SettingsValueRow(t: String, v: String, onClick: () -> Unit = {}) { Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(14.dp)) { Text(t, Color.White, Modifier.weight(1f)); Text(v, Color.White.copy(0.4f)) } }
@Composable fun SettingsToggleRow(t: String, s: String? = null, checked: Boolean = false, onChange: (Boolean) -> Unit = {}) { Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(t, Color.White, fontSize = 16.sp); if (s != null) Text(s, Color.White.copy(0.4f), fontSize = 12.sp) }; Switch(checked = checked, onCheckedChange = onChange) } }
