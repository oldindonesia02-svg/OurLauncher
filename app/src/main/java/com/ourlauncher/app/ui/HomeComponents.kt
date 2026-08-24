package com.ourlauncher.app.ui

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.AppRepository
import com.ourlauncher.app.SettingsManager
import kotlin.math.roundToInt

fun triggerPullDownAction(action: String, context: Context, onOpenSettings: () -> Unit) {
    when (action) {
        "notifications" -> {
            try {
                val service = context.getSystemService("statusbar")
                val clz = Class.forName("android.app.StatusBarManager")
                clz.getMethod("expandNotificationsPanel").invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "system_control_center" -> {
            try {
                val service = context.getSystemService("statusbar")
                val clz = Class.forName("android.app.StatusBarManager")
                clz.getMethod("expandSettingsPanel").invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "builtin_control_center" -> onOpenSettings()
    }
}

// --- LIQUID SEARCH BAR WITH AI ICON & PAGE INDICATOR ---
@Composable
fun LiquidSearchAiCapsule(
    totalPages: Int,
    currentPage: Int,
    onSearchClick: () -> Unit,
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .wrapContentWidth()
            .height(34.dp)
            .clip(pillShape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.22f), Color.Black.copy(alpha = 0.45f))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.40f), Color.White.copy(alpha = 0.08f))
                ),
                pillShape
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Search Trigger
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSearchClick() }
            ) {
                Text(
                    text = "search",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // AI Floating Diamond/Orb Button
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable { onAiClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✦", color = Color(0xFF64D2FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            if (totalPages > 1) {
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.width(1.dp).height(10.dp).background(Color.White.copy(alpha = 0.25f)))
                Spacer(modifier = Modifier.width(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(totalPages) { index ->
                        val isSelected = currentPage == index
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(if (isSelected) 10.dp else 4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White.copy(alpha = 0.95f)
                                    else Color.White.copy(alpha = 0.35f)
                                )
                        )
                    }
                }
            }
        }
    }
}

// --- AI VOICE LISTENING BOTTOM BAR ---
@Composable
fun AiListeningBar(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onDismiss() }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(29.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1C1C1E).copy(alpha = 0.95f), Color(0xFF000000).copy(alpha = 0.98f))
                )
            )
            .border(
                1.2.dp,
                Brush.horizontalGradient(
                    listOf(Color(0xFF0A84FF), Color(0xFFBF5AF2), Color(0xFF64D2FF))
                ),
                RoundedCornerShape(29.dp)
            )
            .clickable { onDismiss() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pulsing glowing orb
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .scale(waveScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(Color(0xFF64D2FF), Color(0xFF0A84FF), Color.Transparent))
                        )
                )

                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Listening...",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "✕",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onDismiss() }
            )
        }
    }
}

// --- LIVE ICON CUSTOMIZER BOTTOM SHEET ---
@Composable
fun IconCustomizeSheet(
    settingsManager: SettingsManager,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(settingsManager.iconTheme) }
    var cornerRadius by remember { mutableStateOf(settingsManager.iconCornerRadius) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2C2C2E).copy(alpha = 0.96f), Color(0xFF141416).copy(alpha = 0.98f))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)),
                RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.width(36.dp).height(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
            Spacer(modifier = Modifier.height(14.dp))

            Text("Customize", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(18.dp))

            // Theme preview cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val themes = listOf(
                    "standard" to "Standard",
                    "dark" to "Dark",
                    "transparent" to "Transparent",
                    "tinted" to "Tinted"
                )
                themes.forEach { (key, title) ->
                    val isSelected = selectedTheme == key
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                            .border(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedTheme = key
                                settingsManager.iconTheme = key
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(cornerRadius.toInt().coerceIn(0, 50)))
                                .background(
                                    when (key) {
                                        "dark" -> Color(0xFF3A3A3C)
                                        "transparent" -> Color.White.copy(alpha = 0.12f)
                                        "tinted" -> Color(0xFF0A84FF).copy(alpha = 0.4f)
                                        else -> Color(0xFFE5E5EA)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✦", color = Color.White, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(title, color = Color.White, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live Corner Radius Slider
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Corner Radius: ${cornerRadius.toInt()}%", color = Color.White.copy(alpha = 0.85f), fontSize = 13.5.sp)
            }
            Slider(
                value = cornerRadius,
                onValueChange = {
                    cornerRadius = it
                    settingsManager.iconCornerRadius = it
                },
                valueRange = 0f..50f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Blue Apply Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(Color(0xFF0A84FF))
                    .clickable {
                        onApply()
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Apply", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// --- HOME SCREEN QUICK SETTINGS SHEET ---
@Composable
fun HomeQuickSettingsSheet(
    settingsManager: SettingsManager,
    onOpenFullSettings: () -> Unit,
    onOpenIconCustomize: () -> Unit,
    onDismiss: () -> Unit
) {
    var showLabels by remember { mutableStateOf(settingsManager.showLabels) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2C2C2E).copy(alpha = 0.96f), Color(0xFF141416).copy(alpha = 0.98f))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)),
                RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally).width(36.dp).height(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
            Spacer(modifier = Modifier.height(14.dp))

            Text("Home screen settings", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenIconCustomize() }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Customize App Icons", color = Color.White, fontSize = 14.5.sp)
                Text("›", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp)
            }

            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(Color.White.copy(alpha = 0.1f)))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        showLabels = !showLabels
                        settingsManager.showLabels = showLabels
                    }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show label", color = Color.White, fontSize = 14.5.sp)
                Text(if (showLabels) "On" else "Off", color = Color(0xFF0A84FF), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(Color.White.copy(alpha = 0.1f)))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onDismiss()
                        onOpenFullSettings()
                    }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("More settings", color = Color(0xFF0A84FF), fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                Text("›", color = Color(0xFF0A84FF), fontSize = 20.sp)
            }
        }
    }
}

// --- APP LAUNCH OVERLAY ---
@Composable
fun AppLaunchOverlay(
    activeApp: AppInfo,
    activeBounds: Rect,
    progress: Float,
    screenWidthPx: Float,
    screenHeightPx: Float,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable?
) {
    val density = LocalDensity.current
    val currentX = activeBounds.left * (1f - progress)
    val currentY = activeBounds.top * (1f - progress)
    val currentW = activeBounds.width() + (screenWidthPx - activeBounds.width()) * progress
    val currentH = activeBounds.height() + (screenHeightPx - activeBounds.height()) * progress
    val initialCornerPx = (activeBounds.width() * (settingsManager.iconCornerRadius / 100f))
    val currentRadius = initialCornerPx * (1f - progress)

    with(density) {
        Box(
            modifier = Modifier
                .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                .size(currentW.toDp(), currentH.toDp())
                .clip(RoundedCornerShape(currentRadius.toDp()))
                .background(Color(0xFF141416))
                .graphicsLayer { alpha = progress.coerceIn(0.1f, 1f) },
            contentAlignment = Alignment.Center
        ) {
            val targetDrawable = getCustomDrawable(activeApp.packageName) ?: activeApp.icon
            val cacheKey = "${activeApp.packageName}_${targetDrawable?.hashCode() ?: 0}"
            val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .size((settingsManager.iconSize * 1.35f).dp)
                        .scale(1f + (0.35f * progress))
                )
            }
        }
    }
}
