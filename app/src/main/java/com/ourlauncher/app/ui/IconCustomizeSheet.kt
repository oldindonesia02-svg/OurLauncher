package com.ourlauncher.app.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import kotlin.math.roundToInt

data class IconPackInfo(
    val packageName: String,
    val name: String
)

data class ShapePreset(
    val name: String,
    val radiusPercent: Float
)

// Scanner for Play Store 3rd-Party Icon Packs
fun getInstalledIconPacks(context: Context): List<IconPackInfo> {
    val pm = context.packageManager
    val iconPacks = mutableListOf<IconPackInfo>()
    iconPacks.add(IconPackInfo("system_default", "System Default"))

    val intentActions = listOf(
        "org.adw.launcher.THEMES",
        "com.novalauncher.THEME",
        "com.gau.go.launcherex.theme",
        "com.teslacoilsw.launcher.THEME"
    )

    val seenPackages = mutableSetOf<String>()
    intentActions.forEach { action ->
        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(Intent(action), PackageManager.GET_META_DATA)
        for (info in resolveInfos) {
            val pkg = info.activityInfo.packageName
            if (pkg !in seenPackages) {
                seenPackages.add(pkg)
                val label = info.loadLabel(pm).toString()
                iconPacks.add(IconPackInfo(pkg, label))
            }
        }
    }
    return iconPacks
}

@Composable
fun IconCustomizeSheet(
    settingsManager: SettingsManager,
    onApply: () -> Unit = {},
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    val installedIconPacks = remember { getInstalledIconPacks(context) }

    var liveSize by remember { mutableFloatStateOf(settingsManager.iconSize) }
    var liveRadius by remember { mutableFloatStateOf(settingsManager.iconCornerRadius) }
    var liveOpacity by remember { mutableFloatStateOf(settingsManager.iconOpacity) }
    var liveShowLabel by remember { mutableStateOf(settingsManager.showLabels) }
    var selectedIconPack by remember { mutableStateOf("system_default") }
    var isMonochrome by remember { mutableStateOf(settingsManager.fontFamily.equals("Monospace", ignoreCase = true)) }

    val shapePresets = remember {
        listOf(
            ShapePreset("iOS Squircle", 38f),
            ShapePreset("HyperOS", 28f),
            ShapePreset("Circle", 50f),
            ShapePreset("Square", 0f),
            ShapePreset("Soft Square", 20f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Frosted Glass Modal matching Image 27187.png
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 36.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1B2836).copy(alpha = 0.96f),
                            Color(0xFF0F1822).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.75f),
                            Color(0xFF00E5FF).copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.10f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Top Action Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = "Back",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "Icons",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 1. THIRD-PARTY ICON PACKS
                Text(
                    text = "ICON PACK",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131F2A))
                        .padding(14.dp)
                ) {
                    if (installedIconPacks.isEmpty() || installedIconPacks.size == 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("System Default (No 3rd party pack found)", color = Color.White, fontSize = 14.sp)
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF00E5FF))
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(installedIconPacks) { pack ->
                                val isSelected = selectedIconPack == pack.packageName
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) Color(0xFF007BFF).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.12f),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            selectedIconPack = pack.packageName
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = pack.name,
                                        color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. ICON SIZE & SHAPE PRESETS
                Text(
                    text = "ICON SIZE & SHAPE",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131F2A))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Size Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Size", color = Color.White, fontSize = 15.sp)
                                Text("${liveSize.roundToInt()} dp", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = liveSize,
                                onValueChange = {
                                    liveSize = it
                                    settingsManager.iconSize = it
                                },
                                valueRange = 40f..85f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color(0xFF00E5FF),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                                )
                            )
                        }

                        // Corner Radius Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Corner Radius", color = Color.White, fontSize = 15.sp)
                                Text("${liveRadius.roundToInt()}%", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = liveRadius,
                                onValueChange = {
                                    liveRadius = it
                                    settingsManager.iconCornerRadius = it
                                },
                                valueRange = 0f..50f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color(0xFF00E5FF),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                                )
                            )
                        }

                        // Shape Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(shapePresets) { preset ->
                                val isSelected = liveRadius.roundToInt() == preset.radiusPercent.roundToInt()
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.15f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            liveRadius = preset.radiusPercent
                                            settingsManager.iconCornerRadius = preset.radiusPercent
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = preset.name,
                                        color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. LIQUID GLASS & OPACITY
                Text(
                    text = "LIQUID GLASS & OPACITY",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131F2A))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Glass Transparency", color = Color.White, fontSize = 15.sp)
                                Text("${(liveOpacity * 100).roundToInt()}%", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = liveOpacity,
                                onValueChange = {
                                    liveOpacity = it
                                    settingsManager.iconOpacity = it
                                },
                                valueRange = 0.2f..1.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color(0xFF00E5FF),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                                )
                            )
                        }

                        // Monochrome / Nothing OS Filter Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monochrome / B&W Filter", color = Color.White, fontSize = 15.sp)
                            Switch(
                                checked = isMonochrome,
                                onCheckedChange = {
                                    isMonochrome = it
                                    settingsManager.fontFamily = if (it) "Monospace" else "SF Pro"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF007BFF),
                                    uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }

                // 4. LABELS & TYPOGRAPHY
                Text(
                    text = "LABELS",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131F2A))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show App Labels", color = Color.White, fontSize = 15.sp)
                        Switch(
                            checked = liveShowLabel,
                            onCheckedChange = {
                                liveShowLabel = it
                                settingsManager.showLabels = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF007BFF),
                                uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00A2FF), Color(0xFF0066FF))
                            )
                        )
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Apply & Done",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
