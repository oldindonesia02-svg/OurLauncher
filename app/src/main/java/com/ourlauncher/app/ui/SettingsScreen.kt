package com.ourlauncher.app.ui

import android.content.Context
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
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit = {},
    onDismiss: () -> Unit = onBack,
    installedIconPacks: List<IconPackInfo> = emptyList(),
    selectedIconPack: String = "system_default",
    onIconPackSelect: (String) -> Unit = {}
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    var currentSubPage by remember { mutableStateOf("main") }

    val iconPacks = remember(installedIconPacks) {
        if (installedIconPacks.isNotEmpty()) installedIconPacks else getInstalledIconPacks(context)
    }

    var liveSize by remember { mutableFloatStateOf(settingsManager.iconSize) }
    var liveRadius by remember { mutableFloatStateOf(settingsManager.iconCornerRadius) }
    var liveOpacity by remember { mutableFloatStateOf(settingsManager.iconOpacity) }
    var liveShowLabel by remember { mutableStateOf(settingsManager.showLabels) }
    var activePack by remember { mutableStateOf(selectedIconPack) }
    var isMonochrome by remember { mutableStateOf(settingsManager.fontFamily.equals("Monospace", ignoreCase = true)) }

    var liveCols by remember { mutableIntStateOf(settingsManager.gridColumns) }
    var liveRows by remember { mutableIntStateOf(settingsManager.gridRows) }

    var dockIconCount by remember { mutableIntStateOf(4) }

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
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 36.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFF00E5FF).copy(alpha = 0.3f))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF162330).copy(alpha = 0.96f), Color(0xFF0D161F).copy(alpha = 0.98f))
                    )
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.75f), Color(0xFF00E5FF).copy(alpha = 0.35f), Color.Transparent)
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
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
                            .clickable {
                                if (currentSubPage != "main") currentSubPage = "main" else onDismiss()
                            },
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
                        text = when (currentSubPage) {
                            "icons" -> "Icons"
                            "grid" -> "Desktop Grid"
                            "dock" -> "Dock"
                            else -> "Settings"
                        },
                        color = Color.White,
                        fontSize = 19.sp,
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

                // 1. MAIN MENU
                if (currentSubPage == "main") {
                    Text(
                        text = "CUSTOMIZATION",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF131F2A)),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        SettingsNavRow("Desktop Grid", "Configure Columns & Rows") { currentSubPage = "grid" }
                        SettingsNavRow("App icons", "Icon packs, Shape, Size & Lens Light") { currentSubPage = "icons" }
                        SettingsNavRow("Dock", "Capacity, Padding & Corner Radius") { currentSubPage = "dock" }
                    }
                }

                // 2. ICONS MENU
                if (currentSubPage == "icons") {
                    Text("ICON PACK", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF131F2A))
                            .padding(12.dp)
                    ) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(iconPacks) { pack ->
                                val isSelected = activePack == pack.packageName
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF007BFF).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f))
                                        .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            activePack = pack.packageName
                                            onIconPackSelect(pack.packageName)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(pack.name, color = if (isSelected) Color(0xFF00E5FF) else Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Text("ICON SIZE & SHAPE", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF131F2A))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(shapePresets) { preset ->
                                    val isSelected = liveRadius.roundToInt() == preset.radiusPercent.roundToInt()
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f))
                                            .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                            .clickable {
                                                liveRadius = preset.radiusPercent
                                                settingsManager.iconCornerRadius = preset.radiusPercent
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(preset.name, color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    Text("GLASS & LABELS", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF131F2A))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Glass Specular Sheen", color = Color.White, fontSize = 15.sp)
                                Text("${(liveOpacity * 100).roundToInt()}%", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = liveOpacity,
                                onValueChange = {
                                    liveOpacity = it
                                    settingsManager.iconOpacity = it
                                },
                                valueRange = 0.2f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Monochrome B&W", color = Color.White, fontSize = 15.sp)
                                Switch(
                                    checked = isMonochrome,
                                    onCheckedChange = {
                                        isMonochrome = it
                                        settingsManager.fontFamily = if (it) "Monospace" else "SF Pro"
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007BFF))
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Show Labels", color = Color.White, fontSize = 15.sp)
                                Switch(
                                    checked = liveShowLabel,
                                    onCheckedChange = {
                                        liveShowLabel = it
                                        settingsManager.showLabels = it
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007BFF))
                                )
                            }
                        }
                    }
                }

                // 3. GRID MENU
                if (currentSubPage == "grid") {
                    Text("GRID SIZE", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(Pair(4, 4), Pair(4, 5), Pair(4, 6), Pair(5, 5), Pair(5, 6)).forEach { (c, r) ->
                            val isSelected = liveCols == c && liveRows == r
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF007BFF).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f))
                                    .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        liveCols = c
                                        liveRows = r
                                        settingsManager.gridColumns = c
                                        settingsManager.gridRows = r
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$c × $r", color = if (isSelected) Color(0xFF00E5FF) else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 4. DOCK MENU
                if (currentSubPage == "dock") {
                    Text("DOCK CAPACITY", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(4, 5, 6).forEach { count ->
                            val isSelected = dockIconCount == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) Color(0xFF007BFF).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f))
                                    .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                    .clickable { dockIconCount = count },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$count Apps", color = if (isSelected) Color(0xFF00E5FF) else Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF00A2FF), Color(0xFF0066FF))))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Apply & Done", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsNavRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
        Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
    }
}
