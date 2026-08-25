package com.ourlauncher.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

sealed class GridItem {
    abstract val id: String
    data class SingleApp(val app: AppInfo) : GridItem() {
        override val id: String get() = app.packageName
    }
    data class Folder(val folder: FolderInfo) : GridItem() {
        override val id: String get() = folder.id
    }
}

data class FolderInfo(
    val id: String,
    var name: String,
    val apps: MutableList<AppInfo>
)

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

@Composable
fun LiquidSearchAiCapsule(
    totalPages: Int,
    currentPage: Int,
    isScrollInProgress: Boolean = false,
    onSearchClick: () -> Unit,
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDots by remember { mutableStateOf(false) }

    LaunchedEffect(isScrollInProgress) {
        if (isScrollInProgress) {
            showDots = true
        } else {
            delay(1000L)
            showDots = false
        }
    }

    Crossfade(
        targetState = showDots,
        animationSpec = tween(240),
        label = "CapsuleMorph"
    ) { displayingDots ->
        if (displayingDots && totalPages > 1) {
            Box(
                modifier = modifier
                    .height(34.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.52f))
                    .border(0.8.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalPages) { index ->
                        val isSelected = index == currentPage
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 14.dp else 5.dp,
                            animationSpec = spring(dampingRatio = 0.75f, stiffness = 320f),
                            label = "dotWidth"
                        )
                        Box(
                            modifier = Modifier
                                .height(5.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color.White else Color.White.copy(alpha = 0.35f)
                                )
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = modifier
                    .height(34.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.52f))
                    .border(0.8.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                    .padding(start = 14.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSearchClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "search",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onAiClick() },
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
        }
    }
}
@Composable
fun FolderIcon(
    folder: FolderInfo,
    onClick: () -> Unit,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(settingsManager.iconSize.dp)
                .clip(RoundedCornerShape(settingsManager.iconCornerRadius.toInt()))
                .background(Color.White.copy(alpha = 0.22f))
                .border(0.8.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(settingsManager.iconCornerRadius.toInt()))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            val previewApps = folder.apps.take(4)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(previewApps) { app ->
                    val targetDrawable = getCustomDrawable(app.packageName) ?: app.icon
                    val cacheKey = "${app.packageName}_${targetDrawable?.hashCode() ?: 0}"
                    val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(settingsManager.iconCornerRadius.toInt() / 2))
                        )
                    }
                }
            }
        }
        if (settingsManager.showLabels) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = folder.name,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FolderPopup(
    folder: FolderInfo,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable?,
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, Rect) -> Unit,
    onRenameFolder: (String) -> Unit,
    onStartDragOut: (AppInfo, Offset) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1E1E1E).copy(alpha = 0.95f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = folder.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(folder.apps) { app ->
                        var bounds by remember { mutableStateOf(Rect()) }
                        AppIcon(
                            app = app,
                            onClick = { onAppClick(app) },
                            showLabel = true,
                            fontFamilyName = settingsManager.fontFamily,
                            iconSizeDp = settingsManager.iconSize,
                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                            iconOpacity = 1f,
                            customDrawable = getCustomDrawable(app.packageName),
                            onClickWithBounds = { b -> onAppClickWithBounds(app, b) },
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    val b = coords.boundsInRoot()
                                    bounds = Rect(b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt())
                                }
                                .pointerInput(app) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { offset ->
                                            onStartDragOut(app, Offset(bounds.left + offset.x, bounds.top + offset.y))
                                        },
                                        onDrag = { _, _ -> },
                                        onDragEnd = {},
                                        onDragCancel = {}
                                    )
                                }
                        )
                    }
                }
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

            Spacer(modifier = Modifier.height(6.dp))

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

            Spacer(modifier = Modifier.height(6.dp))

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
