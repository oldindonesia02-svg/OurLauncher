package com.ourlauncher.app.ui

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    settingsManager: SettingsManager,
    resumeTrigger: Long = 0L,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, android.graphics.Rect) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val dockApps = remember(apps) { apps.take(4) }
    var gridApps by remember(apps) { mutableStateOf(apps.drop(4).take(20).toMutableList()) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Drag State
    var draggedAppIndex by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    val itemBoundsMap = remember { mutableStateMapOf<Int, android.graphics.Rect>() }

    // Animation States
    var activeApp by remember { mutableStateOf<AppInfo?>(null) }
    var activeBounds by remember { mutableStateOf<android.graphics.Rect?>(null) }
    var isAppOpen by remember { mutableStateOf(false) }
    val animProgress = remember { Animatable(0f) }

    // Bézier Easing
    val posEasing = remember(settingsManager.posCurveX1, settingsManager.posCurveY1, settingsManager.posCurveX2, settingsManager.posCurveY2) {
        CubicBezierEasing(settingsManager.posCurveX1.coerceIn(0f, 1f), settingsManager.posCurveY1, settingsManager.posCurveX2.coerceIn(0f, 1f), settingsManager.posCurveY2)
    }

    // Trigger Closing Animation on returning to Home Screen
    LaunchedEffect(resumeTrigger) {
        if (resumeTrigger > 0L && isAppOpen && activeBounds != null) {
            animProgress.snapTo(1f)
            animProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 280, easing = posEasing)
            )
            isAppOpen = false
            activeApp = null
        }
    }

    fun launchAppWithAnim(app: AppInfo, bounds: android.graphics.Rect?) {
        if (!settingsManager.animEnabled || bounds == null) {
            onAppClick(app)
            return
        }

        activeApp = app
        activeBounds = bounds
        isAppOpen = true

        coroutineScope.launch {
            animProgress.snapTo(0f)
            launch {
                animProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = settingsManager.animDuration.toInt(), easing = posEasing)
                )
            }
            delay((settingsManager.animDuration * 0.70f).toLong())
            onAppClickWithBounds(app, bounds)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val p = animProgress.value

        val bgScale = if (activeApp != null && settingsManager.animAdvancedTexture) 1f - (0.05f * p) else 1f
        val bgAlpha = if (activeApp != null && settingsManager.animAdvancedTexture) 1f - (0.30f * p) else 1f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(bgScale)
                .alpha(bgAlpha)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { startOffset ->
                            val found = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                rect.contains(startOffset.x.toInt(), startOffset.y.toInt())
                            }
                            if (found != null) {
                                draggedAppIndex = found.key
                                dragPosition = startOffset
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragPosition += dragAmount

                            val hovered = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                rect.contains(dragPosition.x.toInt(), dragPosition.y.toInt())
                            }
                            if (hovered != null && draggedAppIndex != null && hovered.key != draggedAppIndex) {
                                val from = draggedAppIndex!!
                                val to = hovered.key
                                val list = gridApps.toMutableList()
                                Collections.swap(list, from, to)
                                gridApps = list
                                draggedAppIndex = to
                            }
                        },
                        onDragEnd = { draggedAppIndex = null },
                        onDragCancel = { draggedAppIndex = null }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = { onOpenSettings() })
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(top = 56.dp, start = 16.dp, end = 16.dp),
                    userScrollEnabled = false,
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    itemsIndexed(gridApps, key = { _, app -> app.packageName }) { index, app ->
                        val isBeingDragged = draggedAppIndex == index
                        Box(
                            modifier = Modifier
                                .height(88.dp)
                                .onGloballyPositioned { coords ->
                                    val b = coords.boundsInRoot()
                                    itemBoundsMap[index] = android.graphics.Rect(
                                        b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt()
                                    )
                                }
                                .alpha(if (isBeingDragged) 0.05f else 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                app = app,
                                onClick = { launchAppWithAnim(app, null) },
                                showLabel = settingsManager.showLabels,
                                iconSizeDp = settingsManager.iconSize,
                                cornerRadiusPercent = settingsManager.iconCornerRadius,
                                iconOpacity = settingsManager.iconOpacity,
                                customDrawable = getCustomDrawable(app.packageName),
                                onClickWithBounds = { bounds -> launchAppWithAnim(app, bounds) }
                            )
                        }
                    }
                }

                SearchPill(onClick = onOpenDrawer, modifier = Modifier.padding(bottom = 8.dp))

                Dock(
                    pinnedApps = dockApps,
                    iconSize = settingsManager.iconSize,
                    cornerRadiusPercent = settingsManager.iconCornerRadius,
                    iconOpacity = settingsManager.iconOpacity,
                    dockRadius = settingsManager.dockRadius,
                    showDockBg = settingsManager.showDockBg,
                    getCustomDrawable = getCustomDrawable,
                    onAppClick = { launchAppWithAnim(it, null) },
                    onAppClickWithBounds = { app, bounds -> launchAppWithAnim(app, bounds) }
                )
            }
        }

        // --- FLOATING ICON WHEN DRAGGED ---
        if (draggedAppIndex != null && draggedAppIndex!! < gridApps.size) {
            val app = gridApps[draggedAppIndex!!]
            val targetDrawable = getCustomDrawable(app.packageName) ?: app.icon
            val cacheKey = "${app.packageName}_${targetDrawable.hashCode()}"
            val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

            with(density) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (dragPosition.x - (settingsManager.iconSize.dp.toPx() / 2)).roundToInt(),
                                (dragPosition.y - (settingsManager.iconSize.dp.toPx() / 2) - 30.dp.toPx()).roundToInt()
                            )
                        }
                        .scale(1.15f),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .size(settingsManager.iconSize.dp)
                                .clip(RoundedCornerShape(settingsManager.iconCornerRadius.toInt()))
                        )
                    }
                }
            }
        }

        // --- GPU-ACCELERATED DUAL OPEN & CLOSE EXPANDING CARD ---
        if (activeApp != null && activeBounds != null && p > 0.01f) {
            val b = activeBounds!!
            val currentX = b.left * (1f - p)
            val currentY = b.top * (1f - p)
            val currentW = b.width() + (screenWidthPx - b.width()) * p
            val currentH = b.height() + (screenHeightPx - b.height()) * p
            val currentRadius = (28f * (1f - p) + 36f * p)

            with(density) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                        .size(currentW.toDp(), currentH.toDp())
                        .clip(RoundedCornerShape(currentRadius.dp))
                        .background(Color(0xFF18181B))
                        .graphicsLayer { alpha = p.coerceIn(0.1f, 1f) },
                    contentAlignment = Alignment.Center
                ) {
                    val targetDrawable = getCustomDrawable(activeApp!!.packageName) ?: activeApp!!.icon
                    val cacheKey = "${activeApp!!.packageName}_${targetDrawable.hashCode()}"
                    val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .size((settingsManager.iconSize * 1.25f).dp)
                                .scale(1f + (0.3f * p))
                        )
                    }
                }
            }
        }
    }
}
