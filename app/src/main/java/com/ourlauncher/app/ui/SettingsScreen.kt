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
import androidx.compose.ui.graphics.Brush
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
                .clickable(enabled = false) {} // block dismiss when tapping sheet
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
                SettingsNavRow("Transition effects") {}
                SettingsDivider()
                SettingsNavRow("Set default screen") {}
                SettingsDivider()
                SettingsValueRow("Show label", if (showLabel) "On" else "Off") {
                    showLabel = !showLabel
                }
                SettingsDivider()
                SettingsToggleRow("Liquid folder", liquidFolder) { liquidFolder = it }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Group 2
            SettingsGroup {
                SettingsLinkRow("Regenerate all icons") {}
                SettingsDivider()
                SettingsLinkRow("More settings") { onOpenMoreSettings() }
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
            // Back button
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

            // Search bar
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

            // ── General ──
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
                SettingsNavRow("Sounds and vibration", "Switch feedback and vibration settings") {}
            }

            SettingsSectionHeader("ACTIONS")
            SettingsGroup {
                SettingsNavRow("Swipe actions", "Customize gesture swipe behaviors") {}
                SettingsDivider()
                SettingsNavRow("Control center", "Configure control center layout and options") {}
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
                SettingsNavRow("Customize Lock Screen", "Test stretching lock screen clock") {}
                SettingsDivider()
                SettingsNavRow("Services", "Manage additional services and integrations") {}
                SettingsDivider()
                SettingsNavRow("Passcode", "Set up passcode for folder protection") {}
            }

            SettingsSectionHeader("CUSTOMIZATION")
            SettingsGroup {
                SettingsNavRow("Appearance", "Dark theme and custom fonts") {}
                SettingsDivider()
                SettingsNavRow("App icons", "Opacity, shape, icon pack and icon corners") {}
                SettingsDivider()
                SettingsNavRow("App Open Animation", "Toggle animation, duration and custom bezier curves") {}
                SettingsDivider()
                SettingsNavRow("Dock", "Dock padding, gap and corner radius") {}
                SettingsDivider()
                SettingsNavRow("Highlights", "Highlight style and light direction") {}
                SettingsDivider()
                SettingsNavRow("Liquid Glass", "Adjust transparency, blur and lens refraction") {}
                SettingsDivider()
                SettingsNavRow("Search Bar Position", "Adjust the vertical offset of the search pill") {}
            }

            SettingsSectionHeader("GRAPHIC")
            SettingsGroup {
                GraphicsLevelRow("Ultra", "Liquid Glass with all animations and full graphics enabled", graphicsLevel) { graphicsLevel = it }
                SettingsDivider()
                GraphicsLevelRow("High", "Full graphics with all animations, but disables backdrop blur on folders", graphicsLevel) { graphicsLevel = it }
                SettingsDivider()
                GraphicsLevelRow("Medium", "Disables Liquid Glass (uses simple blur instead) and reduces GPU usage for highlights", graphicsLevel) { graphicsLevel = it }
                SettingsDivider()
                GraphicsLevelRow("Low", "Disables all blur, liquid glass, highlights, and heavy graphic effects", graphicsLevel) { graphicsLevel = it }
                SettingsDivider()
                SettingsNavRow("Other graphic settings", "Configure uninstall & remove animation effects") {}
            }

            SettingsSectionHeader("ABOUT")
            SettingsGroup {
                SettingsNavRow("OurLauncher", "Version 1.0.0") {}
                SettingsDivider()
                SettingsNavRow("Updates", "Check for new versions") {}
                SettingsDivider()
                SettingsNavRow("Support Development", "Buy us a coffee ☕") {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2C2C2E))
                    .clickable { /* reset later */ }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Reset Settings", color = Color(0xFFFF453A), fontSize = 17.sp, fontWeight = FontWeight.Medium)
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
fun SettingsNavRow(title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 17.sp)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        }
        Text("›", color = Color.White.copy(alpha = 0.3f), fontSize = 22.sp)
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
        Text(title, color = Color(0xFF0A84FF), fontSize = 17.sp, modifier = Modifier.weight(1f))
        Text("›", color = Color.White.copy(alpha = 0.3f), fontSize = 22.sp)
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
        Text(title, color = Color.White, fontSize = 17.sp, modifier = Modifier.weight(1f))
        Text(value, color = Color.White.copy(alpha = 0.4f), fontSize = 17.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text("‹ ›", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp)
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
            Text(title, color = Color.White, fontSize = 17.sp)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
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
            Text(level, color = Color.White, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(description, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
        }
        if (selected == level) {
            Text("✓", color = Color(0xFF0A84FF), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
