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
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreenSettingsSheet(
    onDismiss: () -> Unit,
    onOpenMoreSettings: () -> Unit
) {
    var showLabel by remember { mutableStateOf(true) }
    var liquidFolder by remember { mutableStateOf(true) }

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
                SettingsNavRow("Transition effects")
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

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    dockRadius: Float = 32f,
    onDockRadiusChange: (Float) -> Unit = {},
    showDockBg: Boolean = true,
    onShowDockBgChange: (Boolean) -> Unit = {}
) {
    var currentSubPage by remember { mutableStateOf("main") }

    var disableWhatsNew by remember { mutableStateOf(false) }
    var showAssistant by remember { mutableStateOf(true) }
    var lockScreen by remember { mutableStateOf(false) }
    var fakeFingerprint by remember { mutableStateOf(false) }
    var graphicsLevel by remember { mutableStateOf("Medium") }

    var iconOpacity by remember { mutableStateOf(100f) }
    var glassBlur by remember { mutableStateOf(20f) }

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
                    "dock" -> "Dock Customization"
                    "icons" -> "App Icons Settings"
                    "glass" -> "Liquid Glass Settings"
                    else -> "Settings"
                },
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when (currentSubPage) {
            "dock" -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsGroup {
                        SettingsToggleRow("Show dock background", null, showDockBg, onShowDockBgChange)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Dock corner radius: ${dockRadius.toInt()}dp", color = Color.White, fontSize = 16.sp)
                            Slider(
                                value = dockRadius,
                                onValueChange = onDockRadiusChange,
                                valueRange = 8f..50f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                            )
                        }
                    }
                }
            }

            "icons" -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Icon opacity: ${iconOpacity.toInt()}%", color = Color.White, fontSize = 16.sp)
                            Slider(
                                value = iconOpacity,
                                onValueChange = { iconOpacity = it },
                                valueRange = 20f..100f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                            )
                        }
                    }
                }
            }

            "glass" -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsGroup {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Glass blur intensity: ${glassBlur.toInt()}", color = Color.White, fontSize = 16.sp)
                            Slider(
                                value = glassBlur,
                                onValueChange = { glassBlur = it },
                                valueRange = 0f..50f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
                            )
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    SettingsGroup {
                        SettingsToggleRow(
                            title = "Disable What's New",
                            subtitle = "Never show What's New screen after launcher updates",
                            checked = disableWhatsNew,
                            onCheckedChange = { disableWhatsNew = it }
                        )
                    }

                    SettingsSectionHeader("ACTIONS")
                    SettingsGroup {
                        SettingsNavRow("Swipe actions", "Customize gesture swipe behaviors") {}
                        SettingsDivider()
                        SettingsToggleRow(
                            title = "Show assistant",
                            subtitle = "Display assistant button next to search bar",
                            checked = showAssistant,
                            onCheckedChange = { showAssistant = it }
                        )
                    }

                    SettingsSectionHeader("SECURITY")
                    SettingsGroup {
                        SettingsToggleRow(
                            title = "Lock Screen",
                            subtitle = "Require passcode when opening launcher",
                            checked = lockScreen,
                            onCheckedChange = { lockScreen = it }
                        )
                        SettingsDivider()
                        SettingsToggleRow(
                            title = "Fake fingerprint scanner",
                            subtitle = "Add a fake fingerprint scanner to the lockscreen",
                            checked = fakeFingerprint,
                            onCheckedChange = { fakeFingerprint = it }
                        )
                    }

                    SettingsSectionHeader("CUSTOMIZATION")
                    SettingsGroup {
                        SettingsNavRow("App icons", "Opacity, shape, icon pack and icon corners") { currentSubPage = "icons" }
                        SettingsDivider()
                        SettingsNavRow("Dock", "Dock padding, gap and corner radius") { currentSubPage = "dock" }
                        SettingsDivider()
                        SettingsNavRow("Liquid Glass", "Adjust transparency, blur and lens refraction") { currentSubPage = "glass" }
                    }

                    SettingsSectionHeader("GRAPHIC")
                    SettingsGroup {
                        GraphicsLevelRow("Ultra", "Liquid Glass with all animations enabled", graphicsLevel) { graphicsLevel = it }
                        SettingsDivider()
                        GraphicsLevelRow("Medium", "Disables Liquid Glass (uses simple blur)", graphicsLevel) { graphicsLevel = it }
                        SettingsDivider()
                        GraphicsLevelRow("Low", "Disables all blur and heavy graphic effects", graphicsLevel) { graphicsLevel = it }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C1C1E))
            .padding(vertical = 4.dp),
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
fun SettingsNavRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 17.sp)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        }
        Text(text = "›", color = Color.White.copy(alpha = 0.3f), fontSize = 22.sp)
    }
}

@Composable
fun SettingsLinkRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color(0xFF0A84FF), fontSize = 17.sp, modifier = Modifier.weight(1f))
        Text(text = "›", color = Color.White.copy(alpha = 0.3f), fontSize = 22.sp)
    }
}

@Composable
fun SettingsValueRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 17.sp, modifier = Modifier.weight(1f))
        Text(text = value, color = Color.White.copy(alpha = 0.4f), fontSize = 17.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "‹ ›", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp)
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 17.sp)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0A84FF),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun GraphicsLevelRow(
    level: String,
    description: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(level) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = level, color = Color.White, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
        }
        if (selected == level) {
            Text(text = "✓", color = Color(0xFF0A84FF), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
