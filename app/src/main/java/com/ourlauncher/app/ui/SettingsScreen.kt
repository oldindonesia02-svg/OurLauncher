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
import com.ourlauncher.app.IconPackInfo
import com.ourlauncher.app.SettingsManager
import kotlin.math.roundToInt

private data class CustomShapePreset(
    val name: String,
    val radiusPercent: Float
)

private fun scanDeviceIconPacks(context: Context): List<IconPackInfo> {
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

    // Always include System Default at first position
    val iconPacks = remember(installedIconPacks) {
        val list = mutableListOf(IconPackInfo("system_default", "System Default"))
        val scanned = if (installedIconPacks.isNotEmpty()) installedIconPacks else scanDeviceIconPacks(context)
        scanned.forEach { pack ->
            if (pack.packageName != "system_default" && list.none { it.packageName == pack.packageName }) {
                list.add(pack)
            }
        }
        list
    }

    // Icon Live States
    var liveSize by remember { mutableFloatStateOf(settingsManager.iconSize) }
    var liveRadius by remember { mutableFloatStateOf(settingsManager.iconCornerRadius) }
    var liveOpacity by remember { mutableFloatStateOf(settingsManager.iconOpacity) }
    var liveShowLabel by remember { mutableStateOf(settingsManager.showLabels) }
    var activePack by remember { mutableStateOf(selectedIconPack) }
    var isMonochrome by remember { mutableStateOf(settingsManager.fontFamily.equals("Monospace", ignoreCase = true)) }

    // Desktop Grid States
    var liveCols by remember { mutableIntStateOf(settingsManager.gridColumns) }
    var liveRows by remember { mutableIntStateOf(settingsManager.gridRows) }
    var liveSearchOffset by remember { mutableFloatStateOf(0f) }
    var hideSearch by remember { mutableStateOf(settingsManager.hideSearchCapsule) }

    // Dock Advanced States
    var dockIconCount by remember { mutableIntStateOf(4) }
    var isDockEnabled by remember { mutableStateOf(true) }
    var dockCornerRadius by remember { mutableFloatStateOf(28f) }
    var dockGlassOpacity by remember { mutableFloatStateOf(94f) }
    var dockSpecularGlow by remember { mutableStateOf(true) }

    // Liquid Glass Advanced States
    var blurIntensity by remember { mutableFloatStateOf(24f) }
    var glassTintAlpha by remember { mutableFloatStateOf(65f) }
    var specularHighlight by remember { mutableFloatStateOf(85f) }
    var borderWidth by remember { mutableFloatStateOf(1.3f) }
    var enableRainbowSheen by remember { mutableStateOf(true) }
    var enableFrostedNoise by remember { mutableStateOf(false) }

    // Animations & Gestures States
    var animationSpeed by remember { mutableStateOf("Smooth (300ms)") }
    var doubleTapAction by remember { mutableStateOf("Lock Screen") }

    val shapePresets = remember {
        listOf(
            CustomShapePreset("iOS Squircle", 38f),
            CustomShapePreset("HyperOS", 28f),
            CustomShapePreset("Circle", 50f),
            CustomShapePreset("Square", 0f),
            CustomShapePreset("Soft Square", 20f)
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
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .shadow(28.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFF00E5FF).copy(alpha = 0.35f))
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF162330).copy(alpha = 0.97f), Color(0xFF0D161F).copy(alpha = 0.99f))
                    )
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.80f), Color(0xFF00E5FF).copy(alpha = 0.40f), Color.Transparent)
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
                            "grid" -> "Desktop Grid"
                            "icons" -> "App Icons"
                            "dock" -> "Dock Settings"
                            "glass" -> "Liquid Glass & Blur"
                            "search" -> "Search Bar & AI"
                            "anim" -> "App Open Animation"
                            "gestures" -> "Gestures & Actions"
                            else -> "Launcher Settings"
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
                    Text("CUSTOMIZATION", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFF131F2A))
                    ) {
                        LocalSettingsItem("Desktop Grid", "Configure Columns & Rows") { currentSubPage = "grid" }
                        LocalItemDivider()
                        LocalSettingsItem("App icons", "Icon packs, Shape, Size & Lens Light") { currentSubPage = "icons" }
                        LocalItemDivider()
                        LocalSettingsItem("Dock", "Capacity, Size, Radius & Glass opacity") { currentSubPage = "dock" }
                        LocalItemDivider()
                        LocalSettingsItem("Liquid Glass", "Refraction blur, Tint & Specular sheen") { currentSubPage = "glass" }
                        LocalItemDivider()
                        LocalSettingsItem("Search Bar Position", "Offset position, AI pill & Search Engine") { currentSubPage = "search" }
                    }

                    Text("ANIMATIONS & BEHAVIOR", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFF131F2A))
                    ) {
                        LocalSettingsItem("App Open Animation", "Duration, Physics curves & Scale") { currentSubPage = "anim" }
                        LocalItemDivider()
                        LocalSettingsItem("Swipe actions", "Double tap lock, Gestures behaviors") { currentSubPage = "gestures" }
                    }
                }

                // 2. DESKTOP GRID SUB-PAGE
                if (currentSubPage == "grid") {
                    Text("GRID DENSITY", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(Pair(4, 4), Pair(4, 5), Pair(4, 6), Pair(5, 5), Pair(5, 6)).forEach { (c, r) ->
                            val isSelected = liveCols == c && liveRows == r
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
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

                // 3. APP ICONS SUB-PAGE
                if (currentSubPage == "icons") {
                    Text("ICON PACK", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(12.dp)) {
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
                                    Text(pack.label, color = if (isSelected) Color(0xFF00E5FF) else Color.White, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                    Text("ICON SIZE & SHAPE", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(16.dp)) {
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
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(16.dp)) {
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

                                // 4. DOCK SUB-PAGE
                if (currentSubPage == "dock") {
                    Text("DOCK TOGGLE", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Liquid Dock", color = Color.White, fontSize = 15.sp)
                        Switch(
                            checked = isDockEnabled,
                            onCheckedChange = { isDockEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007BFF))
                        )
                    }

                    Text("DOCK CAPACITY", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(4, 5, 6, 7).forEach { count ->
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
                                Text("$count", color = if (isSelected) Color(0xFF00E5FF) else Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text("DOCK DIMENSIONS & GLASS", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Dock Corner Radius", color = Color.White, fontSize = 15.sp)
                                Text("${dockCornerRadius.roundToInt()} dp", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = dockCornerRadius,
                                onValueChange = { dockCornerRadius = it },
                                valueRange = 8f..40f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Glass Transparency", color = Color.White, fontSize = 15.sp)
                                Text("${dockGlassOpacity.roundToInt()}%", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = dockGlassOpacity,
                                onValueChange = { dockGlassOpacity = it },
                                valueRange = 20f..100f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Specular Light Glow", color = Color.White, fontSize = 15.sp)
                                Switch(
                                    checked = dockSpecularGlow,
                                    onCheckedChange = { dockSpecularGlow = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007BFF))
                                )
                            }
                        }
                    }
                }

                // 5. LIQUID GLASS & BLUR SUB-PAGE (Comprehensive Pro Settings)
                if (currentSubPage == "glass") {
                    Text("REFRACTION & BLUR", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Blur Intensity
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Blur Intensity", color = Color.White, fontSize = 15.sp)
                                Text("${blurIntensity.roundToInt()} px", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = blurIntensity,
                                onValueChange = { blurIntensity = it },
                                valueRange = 5f..50f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            // Glass Tint Opacity
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Glass Tint Opacity", color = Color.White, fontSize = 15.sp)
                                Text("${glassTintAlpha.roundToInt()}%", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = glassTintAlpha,
                                onValueChange = { glassTintAlpha = it },
                                valueRange = 10f..95f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            // Specular Edge Highlight
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Specular Edge Highlight", color = Color.White, fontSize = 15.sp)
                                Text("${specularHighlight.roundToInt()}%", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = specularHighlight,
                                onValueChange = { specularHighlight = it },
                                valueRange = 10f..100f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            // Border Thickness
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Border Edge Width", color = Color.White, fontSize = 15.sp)
                                Text(String.format("%.1f dp", borderWidth), color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = borderWidth,
                                onValueChange = { borderWidth = it },
                                valueRange = 0.5f..3.0f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )

                            // Rainbow Sheen Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Rainbow Refraction Sheen", color = Color.White, fontSize = 15.sp)
                                Switch(
                                    checked = enableRainbowSheen,
                                    onCheckedChange = { enableRainbowSheen = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007BFF))
                                )
                            }

                            // Frosted Noise Texture Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Frosted Noise Texture", color = Color.White, fontSize = 15.sp)
                                Switch(
                                    checked = enableFrostedNoise,
                                    onCheckedChange = { enableFrostedNoise = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007BFF))
                                )
                            }
                        }
                    }
                }

                // 6. SEARCH BAR POSITION & AI SUB-PAGE
                if (currentSubPage == "search") {
                    Text("SEARCH PILL VISIBILITY", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Hide Search Capsule", color = Color.White, fontSize = 15.sp)
                        Switch(
                            checked = hideSearch,
                            onCheckedChange = {
                                hideSearch = it
                                settingsManager.hideSearchCapsule = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007BFF))
                        )
                    }

                    Text("VERTICAL OFFSET", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF131F2A)).padding(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Position Offset", color = Color.White, fontSize = 15.sp)
                                Text("${liveSearchOffset.roundToInt()} px", color = Color(0xFF00E5FF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = liveSearchOffset,
                                onValueChange = {
                                    liveSearchOffset = it
                                    settingsManager.searchOffset = it
                                },
                                valueRange = -100f..100f,
                                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF00E5FF))
                            )
                        }
                    }
                }

                // 7. APP OPEN ANIMATION SUB-PAGE
                if (currentSubPage == "anim") {
                    Text("ANIMATION SPEED & CURVES", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFF131F2A))
                    ) {
                        listOf("Fast (180ms)", "Smooth (300ms)", "Bouncy Spring (400ms)").forEach { speed ->
                            val isSelected = animationSpeed == speed
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { animationSpeed = speed }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(speed, color = if (isSelected) Color(0xFF00E5FF) else Color.White, fontSize = 15.sp)
                                if (isSelected) Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF00E5FF))
                            }
                        }
                    }
                }

                // 8. GESTURES & ACTIONS SUB-PAGE
                if (currentSubPage == "gestures") {
                    Text("DOUBLE TAP ACTION", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Color(0xFF131F2A))
                    ) {
                        listOf("Lock Screen", "Open Search", "None").forEach { action ->
                            val isSelected = doubleTapAction == action
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { doubleTapAction = action }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(action, color = if (isSelected) Color(0xFF00E5FF) else Color.White, fontSize = 15.sp)
                                if (isSelected) Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF00E5FF))
                            }
                        }
                    }
                }

                // Apply & Done Button
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
private fun LocalSettingsItem(title: String, subtitle: String, onClick: () -> Unit) {
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

@Composable
private fun LocalItemDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(0.6.dp)
            .background(Color.White.copy(alpha = 0.08f))
    )
}
