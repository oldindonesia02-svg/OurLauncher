package com.ourlauncher.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

fun triggerPullDownAction(action: String, context: Context, onOpenSettings: () -> Unit) {
    when (action) {
        "Notifications" -> {
            try {
                val service = context.getSystemService("statusbar")
                val statusBarManager = Class.forName("android.app.StatusBarManager")
                val expand = statusBarManager.getMethod("expandNotificationsPanel")
                expand.invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "Quick Settings" -> {
            try {
                val service = context.getSystemService("statusbar")
                val statusBarManager = Class.forName("android.app.StatusBarManager")
                val expand = statusBarManager.getMethod("expandSettingsPanel")
                expand.invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "Settings" -> onOpenSettings()
        "AI Assistant" -> launchGeminiAi(context)
        else -> {}
    }
}

fun launchGeminiAi(context: Context) {
    val packages = listOf(
        "com.google.android.apps.bard",
        "com.google.android.googlequicksearchbox"
    )
    for (pkg in packages) {
        val intent = context.packageManager.getLaunchIntentForPackage(pkg)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        }
    }
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gemini.google.com"))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "AI Assistant not available", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiquidSearchAiCapsule(
    pagerState: PagerState,
    totalPages: Int,
    onSearchClick: () -> Unit,
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.isScrollInProgress) {
        if (pagerState.isScrollInProgress) {
            isScrolling = true
        } else {
            delay(850L)
            isScrolling = false
        }
    }

    val morphProgress by animateFloatAsState(
        targetValue = if (isScrolling && totalPages > 1) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 380f),
        label = "morphProgress"
    )

    val dotsCalculatedWidth = ((totalPages * 16) + 38).coerceIn(84, 180).dp
    val capsuleWidth by animateDpAsState(
        targetValue = if (morphProgress > 0.45f) dotsCalculatedWidth else 146.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 380f),
        label = "capsuleWidth"
    )

    Box(
        modifier = modifier
            .width(capsuleWidth)
            .height(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.60f))
            .border(0.8.dp, Color.White.copy(alpha = 0.22f), CircleShape)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. Distinct Search & AI Buttons (Rest State) ---
        Row(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = (1f - (morphProgress * 2.5f)).coerceIn(0f, 1f)
                    scaleX = 1f - (morphProgress * 0.15f)
                    scaleY = 1f - (morphProgress * 0.15f)
                }
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.16f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = morphProgress < 0.1f
                    ) { onSearchClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "search",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.4.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable(enabled = morphProgress < 0.1f) { onAiClick() },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(13.dp)) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val path = Path().apply {
                        moveTo(cx, 0f)
                        quadraticBezierTo(cx, cy, size.width, cy)
                        quadraticBezierTo(cx, cy, cx, size.height)
                        quadraticBezierTo(cx, cy, 0f, cy)
                        quadraticBezierTo(cx, cy, cx, 0f)
                        close()
                    }
                    drawPath(path, color = Color.White)
                }
            }
        }

        // --- 2. Finger Liquid Worm Animation (Swipe State) ---
        if (totalPages > 1) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = ((morphProgress - 0.25f) / 0.75f).coerceIn(0f, 1f)
                    }
                    .padding(horizontal = 14.dp)
            ) {
                val totalWidth = size.width
                val centerY = size.height / 2f
                val spacing = if (totalPages > 1) totalWidth / (totalPages - 1) else 0f
                val dotRadius = 2.5.dp.toPx()

                for (i in 0 until totalPages) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.32f),
                        radius = dotRadius,
                        center = Offset(i * spacing, centerY)
                    )
                }

                val page = pagerState.currentPage
                val fraction = pagerState.currentPageOffsetFraction

                val wormHeight = 5.dp.toPx()
                val minPillWidth = 12.dp.toPx()
                val stretch = abs(fraction) * spacing * 0.75f

                val leftX = if (fraction >= 0) {
                    (page * spacing) + (fraction * spacing * 0.25f)
                } else {
                    (page * spacing) + (fraction * spacing)
                }

                val currentPillWidth = minPillWidth + stretch

                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(leftX - (minPillWidth / 2f), centerY - (wormHeight / 2f)),
                    size = Size(currentPillWidth, wormHeight),
                    cornerRadius = CornerRadius(wormHeight / 2f, wormHeight / 2f)
                )
            }
        }
    }
}
@Composable
fun TopLiquidSearchBarPositionCard(
    currentOffset: Float,
    isCapsuleHidden: Boolean,
    onOffsetChange: (Float) -> Unit,
    onHideCapsuleChange: (Boolean) -> Unit,
    onOpenDockPosition: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF141416).copy(alpha = 0.88f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(26.dp))
            .padding(18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reset",
                    color = Color(0xFF0A84FF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        onOffsetChange(0f)
                        onHideCapsuleChange(false)
                    }
                )
                Text(
                    text = "Search Bar Position",
                    color = Color.White,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✕",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "VERTICAL OFFSET",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Slider(
                    value = currentOffset,
                    onValueChange = { onOffsetChange(it) },
                    valueRange = -150f..150f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF0A84FF),
                        activeTrackColor = Color(0xFF0A84FF),
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${currentOffset.toInt()} px",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hide search capsule",
                            color = Color.White,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hides the capsule on home screen",
                            color = Color.White.copy(alpha = 0.55f),
                            fontSize = 11.5.sp
                        )
                    }
                    Switch(
                        checked = isCapsuleHidden,
                        onCheckedChange = { onHideCapsuleChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0A84FF),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.25f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Change dock position?",
                color = Color(0xFF0A84FF),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onOpenDockPosition() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0A84FF))
                    .clickable {
                        onApply()
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Apply",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Dock(
    pinnedApps: List<AppInfo>,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable?,
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, Rect) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .border(0.8.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pinnedApps.forEach { app ->
                AppIcon(
                    app = app,
                    onClick = { onAppClick(app) },
                    showLabel = false,
                    fontFamilyName = settingsManager.fontFamily,
                    iconSizeDp = settingsManager.iconSize,
                    cornerRadiusPercent = settingsManager.iconCornerRadius,
                    iconOpacity = settingsManager.iconOpacity,
                    customDrawable = getCustomDrawable(app.packageName),
                    onClickWithBounds = { bounds -> onAppClickWithBounds(app, bounds) },
                    modifier = Modifier.width(60.dp)
                )
            }
        }
    }
}

@Composable
fun HomeQuickSettingsSheet(
    settingsManager: SettingsManager,
    onOpenFullSettings: () -> Unit,
    onOpenIconCustomize: () -> Unit,
    onOpenSearchBarPosition: () -> Unit,
    onDismiss: () -> Unit
) {
    var showLabels by remember { mutableStateOf(settingsManager.showLabels) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.98f))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenIconCustomize() }
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Customize App Icons", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text("›", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenSearchBarPosition() }
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Search Bar Position", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text("›", color = Color.White.copy(alpha = 0.5f), fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        showLabels = !showLabels
                        settingsManager.showLabels = showLabels
                    }
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show App Labels", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(if (showLabels) "On" else "Off", color = if (showLabels) Color(0xFF0A84FF) else Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onDismiss()
                        onOpenFullSettings()
                    }
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("More Settings", color = Color(0xFF0A84FF), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text("›", color = Color(0xFF0A84FF), fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun IconCustomizeSheet(
    settingsManager: SettingsManager,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    var iconSize by remember { mutableStateOf(settingsManager.iconSize) }
    var cornerRadius by remember { mutableStateOf(settingsManager.iconCornerRadius) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color(0xFF1C1C1E).copy(alpha = 0.98f))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Customize Icons", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            Text("Icon Size: ${iconSize.toInt()} dp", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Slider(
                value = iconSize,
                onValueChange = {
                    iconSize = it
                    settingsManager.iconSize = it
                },
                valueRange = 40f..80f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("Corner Radius: ${cornerRadius.toInt()} %", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Slider(
                value = cornerRadius,
                onValueChange = {
                    cornerRadius = it
                    settingsManager.iconCornerRadius = it
                },
                valueRange = 0f..50f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF0A84FF), activeTrackColor = Color(0xFF0A84FF))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0A84FF))
                    .clickable {
                        onApply()
                        onDismiss()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Apply", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

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
                .graphicsLayer { alpha = progress.coerceIn(0f, 1f) },
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
                        .size((settingsManager.iconSize * (1f + 0.35f * progress)).dp)
                        .clip(RoundedCornerShape((settingsManager.iconCornerRadius * (1f - progress)).toInt()))
                )
            }
        }
    }
}
