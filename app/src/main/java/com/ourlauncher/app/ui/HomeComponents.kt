package com.ourlauncher.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.floor
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
    var showDots by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.isScrollInProgress, pagerState.currentPageOffsetFraction) {
        if (pagerState.isScrollInProgress || abs(pagerState.currentPageOffsetFraction) > 0.01f) {
            showDots = true
        } else {
            delay(550L)
            showDots = false
        }
    }

    val isSwiping = showDots && totalPages > 1
    val dotsWidth = ((totalPages * 18) + 36).coerceIn(80, 180).dp
    val capsuleWidth by animateDpAsState(
        targetValue = if (isSwiping) dotsWidth else 146.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "capsuleWidth"
    )

    // সোয়াইপ অবস্থার লিকুইড গ্লাস গ্র্যাডিয়েন্ট (Red Mark)
    val liquidGlassDotsBg = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.20f),
            Color(0xFF0F0F14).copy(alpha = 0.35f)
        )
    )
    val liquidGlassDotsBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.60f),
            Color.White.copy(alpha = 0.12f)
        )
    )

    // স্থির অবস্থার বাটন গ্লাস গ্র্যাডিয়েন্ট (Green Mark)
    val buttonGlassBg = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.18f),
            Color(0xFF141418).copy(alpha = 0.45f)
        )
    )
    val buttonGlassBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.10f)
        )
    )

    // মেইন কন্টেইনার সম্পূর্ণ ট্রান্সপারেন্ট (কোনো কালো ডক নেই)
    Box(
        modifier = modifier
            .width(capsuleWidth)
            .height(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isSwiping,
            animationSpec = tween(140),
            label = "SearchOrDotsMorph"
        ) { swiping ->
            if (swiping) {
                // সোয়াইপ অবস্থা: লিকুইড গ্লাস ডট ক্যাপসুল + ফিঙ্গার ওয়ার্ম অ্যানিমেশন
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(brush = liquidGlassDotsBg)
                        .border(0.9.dp, brush = liquidGlassDotsBorder, shape = CircleShape)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val totalWidth = size.width
                        val centerY = size.height / 2f
                        val spacing = if (totalPages > 1) totalWidth / (totalPages - 1) else 0f
                        val dotRadius = 2.8.dp.toPx()
                        val wormHeight = 5.6.dp.toPx()

                        // ব্যাকগ্রাউন্ড ডটস
                        for (i in 0 until totalPages) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.35f),
                                radius = dotRadius,
                                center = Offset(i * spacing, centerY)
                            )
                        }

                        // আঙুলের সাথে ১:১ লিকুইড স্ট্রেচ ফিজিক্স
                        val continuousPos = (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                            .coerceIn(0f, (totalPages - 1).toFloat())
                        val base = floor(continuousPos).toInt()
                        val fraction = continuousPos - base

                        val headProgress = (fraction / 0.65f).coerceIn(0f, 1f)
                        val tailProgress = ((fraction - 0.35f) / 0.65f).coerceIn(0f, 1f)

                        val smoothHead = headProgress * headProgress * (3f - 2f * headProgress)
                        val smoothTail = tailProgress * tailProgress * (3f - 2f * tailProgress)

                        val leftCenter = (base + smoothTail) * spacing
                        val rightCenter = (base + smoothHead) * spacing

                        val wormLeft = leftCenter - dotRadius
                        val wormRight = rightCenter + dotRadius
                        val wormWidth = (wormRight - wormLeft).coerceAtLeast(wormHeight)

                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(wormLeft, centerY - (wormHeight / 2f)),
                            size = Size(wormWidth, wormHeight),
                            cornerRadius = CornerRadius(wormHeight / 2f, wormHeight / 2f)
                        )
                    }
                }
            } else {
                // স্থির অবস্থা: ভাসমান গ্লাস Search ও AI বাটন (পেছনে কালো ডক ছাড়া)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(CircleShape)
                            .background(brush = buttonGlassBg)
                            .border(0.9.dp, brush = buttonGlassBorder, shape = CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onSearchClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "search",
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.35.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(brush = buttonGlassBg)
                            .border(0.9.dp, brush = buttonGlassBorder, shape = CircleShape)
                            .clickable { onAiClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(13.5.dp)) {
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
    val glassBg = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.14f),
            Color(0xFF0A0A0D).copy(alpha = 0.88f)
        )
    )
    val glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.55f),
            Color.White.copy(alpha = 0.10f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 44.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(brush = glassBg)
            .border(1.dp, glassBorder, RoundedCornerShape(26.dp))
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

            Spacer(modifier = Modifier.height(14.dp))

            // Embedded Live Preview Window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
            ) {
                if (!isCapsuleHidden) {
                    val previewOffsetDp = (currentOffset / 150f * 20f).dp
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = previewOffsetDp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(82.dp)
                                .height(26.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                                .border(0.8.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("search", color = Color.White.copy(alpha = 0.9f), fontSize = 11.5.sp)
                        }

                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                                .border(0.8.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✦", color = Color.White, fontSize = 11.sp)
                        }
                    }
                } else {
                    Text(
                        text = "Capsule hidden",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
