package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

/* ─────────────────────────────────────────────
   HOME SCREEN SETTINGS  (bottom sheet popup)
   ───────────────────────────────────────────── */
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
            // Handle bar
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )

            // Title row
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

            // Group 1
            SettingsGroup {
                SettingsNavRow(title = "Transition effects")
                SettingsDivider()
                SettingsNavRow(title = "Set default screen")
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

            // Group 2
            SettingsGroup {
                SettingsLinkRow(title = "Regenerate all icons", onClick = {})
                SettingsDivider()
                SettingsLinkRow(title = "More settings", onClick = onOpenMoreSettings)
            }
        }
    }
}

/* ─────────────────────────────────────────────
   FULL SETTINGS SCREEN
   ───────────────────────────────────────────── */
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    var disableWhatsNew by remember { mutableStateOf(false) }
    var showAssistant by remember { mutableStateOf(true) }
    var lockScreen by remember { mutableStateOf(false) }
    var fakeFingerprint by remember { mutableStateOf(false) }
    var graphicsLevel by remember { mutableStateOf("Medium") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top bar
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
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", color = Color(0xFF0A84FF), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("Search settings", color = Color.White.copy(alpha = 0.4f), fontSize = 15.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )

            SettingsGroup {
                SettingsToggleRow(
                    title = "Disable What's New",
                    subtitle = "Never show What's New screen after launcher updates",
                    checked = disableWhatsNew,
                    onCheckedChange = { disableWhatsNew = it }
                )
            }

            SettingsSectionHeader("SOUNDS AND VIBRATION")
            SettingsGroup {
                SettingsNavRow(
                    title = "Sounds and vibration",
                    subtitle = "Switch feedback and vibration settings"
                )
            }

            SettingsSectionHeader("ACTIONS")
            SettingsGroup {
                SettingsNavRow(
                    title = "Swipe actions",
                    subtitle = "Customize gesture swipe behaviors"
                )
                SettingsDivider()
                SettingsNavRow(
                    title = "Control center",
                    subtitle = "Configure control center layout and options"
                )
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
                SettingsDivider()
                SettingsNavRow(
                    title = "Customize Lock Screen",
                    subtitle = "Test stretching lock screen clock"
                )
                SettingsDivider()
                SettingsNavRow(
                    title = "Services",
                    subtitle = "Manage additional services and integrations"
                )
                SettingsDivider()
                SettingsNavRow(
                    title = "Passcode",
                    subtitle = "Set up passcode for folder protection"
                )
            }

            SettingsSectionHeader("CUSTOMIZATION")
            SettingsGroup {
                SettingsNavRow(title = "Appearance", subtitle = "Dark theme and custom fonts")
                SettingsDivider()
                SettingsNavRow(title = "App icons", subtitle = "Opacity, shape, icon pack and icon corners")
                SettingsDivider()
                SettingsNavRow(title = "App Open Animation", subtitle = "Toggle animation, duration and custom bezier curves")
                SettingsDivider()
                SettingsNavRow(title = "Dock", subtitle = "Dock padding, gap and corner radius")
                SettingsDivider()
                SettingsNavRow(title = "Highlights", subtitle = "Highlight style and light direction")
                SettingsDivider()
                SettingsNavRow(title = "Liquid Glass", subtitle = "Adjust transparency, blur and lens refraction")
                SettingsDivider()
                SettingsNavRow(title = "Search Bar Position", subtitle = "Adjust the vertical offset of the search pill")
            }

            SettingsSectionHeader("GRAPHIC")
            SettingsGroup {
                GraphicsLevelRow(
                    level = "Ultra",
                    description = "Liquid Glass with all animations and full graphics enabled",
                    selected = graphicsLevel,
                    onSelect = { graphicsLevel = it }
                )
                SettingsDivider()
                GraphicsLevelRow(
                    level = "High",
                    description = "Full graphics with all animations, but disables backdrop blur on folders",
                    selected = graphicsLevel,
                    onSelect = { graphicsLevel = it }
                )
                SettingsDivider()
                GraphicsLevelRow(
                    level = "Medium",
                    description = "Disables Liquid Glass (uses simple blur instead) and reduces GPU usage for highlights",
                    selected = graphicsLevel,
                    onSelect = { graphicsLevel = it }
                )
                SettingsDivider()
                GraphicsLevelRow(
                    level = "Low",
                    description = "Disables all blur, liquid glass, highlights, and heavy graphic effects",
                    selected = graphicsLevel,
                    onSelect = { graphicsLevel = it }
                )
                SettingsDivider()
                SettingsNavRow(
                    title = "Other graphic settings",
                    subtitle = "Configure uninstall & remove animation effects"
                )
            }

            SettingsSectionHeader("ABOUT")
            SettingsGroup {
                SettingsNavRow(title = "OurLauncher", subtitle = "Version 1.0.0")
                SettingsDivider()
                SettingsNavRow(title = "Updates", subtitle = "Check for new versions")
                SettingsDivider()
                SettingsNavRow(title = "Support Development", subtitle = "Buy us a coffee ☕")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2C2C2E))
                    .clickable { }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Reset Settings",
                    color = Color(0xFFFF453A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

/* ─────────────────────────────────────────────
   REUSABLE SETTINGS UI PIECES
   ───────────────────────────────────────────── */

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
fun SettingsLinkRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color(0xFF0A84FF),
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )
        Text(text = "›", color = Color.White.copy(alpha = 0.3f), fontSize = 22.sp)
    }
}

@Composable
fun SettingsValueRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
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
            Text(
                text = "✓",
                color = Color(0xFF0A84FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
