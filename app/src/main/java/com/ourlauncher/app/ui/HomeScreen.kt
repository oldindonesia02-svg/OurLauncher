package com.ourlauncher.app.ui

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, android.graphics.Rect) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val dockApps = apps.take(4)
    val gridApps = apps.drop(4).take(20)

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var animatingApp by remember { mutableStateOf<AppInfo?>(null) }
    var startBounds by remember { mutableStateOf<android.graphics.Rect?>(null) }
    val animProgress = remember { Animatable(0f) }

    // 4 Distinct Bézier Curve Easings
    val posEasing = remember(settingsManager.posCurveX1, settingsManager.posCurveY1, settingsManager.posCurveX2, settingsManager.posCurveY2) {
        CubicBezierEasing(settingsManager.posCurveX1.coerceIn(0f, 1f), settingsManager.posCurveY1, settingsManager.posCurveX2.coerceIn(0f, 1f), settingsManager.posCurveY2)
    }
    val widthEasing = remember(settingsManager.widthCurveX1, settingsManager.widthCurveY1, settingsManager.widthCurveX2, settingsManager.widthCurveY2) {
        CubicBezierEasing(settingsManager.widthCurveX1.coerceIn(0f, 1f), settingsManager.widthCurveY1, settingsManager.widthCurveX2.coerceIn(0f, 1f), settingsManager.widthCurveY2)
    }
    val heightEasing = remember(settingsManager.heightCurveX1, settingsManager.heightCurveY1, settingsManager.heightCurveX2, settingsManager.heightCurveY2) {
        CubicBezierEasing(settingsManager.heightCurveX1.coerceIn(0f, 1f), settingsManager.heightCurveY1, settingsManager.heightCurveX2.coerceIn(0f, 1f), settingsManager.heightCurveY2)
    }
    val cornerEasing = remember(settingsManager.cornerCurveX1, settingsManager.cornerCurveY1, settingsManager.cornerCurveX2, settingsManager.cornerCurveY2) {
        CubicBezierEasing(settingsManager.cornerCurveX1.coerceIn(0f, 1f), settingsManager.cornerCurveY1, settingsManager.cornerCurveX2.coerceIn(0f, 1f), settingsManager.cornerCurveY2)
    }

    fun startAppOpenAnimation(app: AppInfo, bounds: android.graphics.Rect?) {
        if (!settingsManager.animEnabled || bounds == null) {
            onAppClick(app)
            return
        }

        animatingApp = app
        startBounds = bounds

        coroutineScope.launch {
            animProgress.snapTo(0f)
            launch {
                animProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = settingsManager.animDuration.toInt())
                )
            }
            // Launch app right before animation completes for seamless visual handoff
            delay((settingsManager.animDuration * 0.85f).toLong())
            onAppClickWithBounds(app, bounds)

            delay(300)
            animProgress.snapTo(0f)
            animatingApp = null
        }
    }

    fun performSwipe(action: String) {
        when (action) {
            "drawer" -> onOpenDrawer()
            "settings" -> onOpenSettings()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val p = animProgress.value

        // Workspace background scales down and dims if Advanced Texture is on
        val bgScale = if (animatingApp != null && settingsManager.animAdvancedTexture) 1f - (0.06f * p) else 1f
        val bgAlpha = if (animatingApp != null && settingsManager.animAdvancedTexture) 1f - (0.35f * p) else 1f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(bgScale)
                .alpha(bgAlpha)
                .pointerInput(Unit) {
                    var totalDragX = 0f
                    var totalDragY = 0f
                    detectDragGestures(
                        onDragStart = { totalDragX = 0f; totalDragY = 0f },
                        onDrag = { _, dragAmount -> totalDragX += dragAmount.x; totalDragY += dragAmount.y },
                        onDragEnd = {
                            val threshold = 60f
                            if (abs(totalDragY) > abs(totalDragX)) {
                                if (totalDragY < -threshold) performSwipe(settingsManager.swipeUpAction)
                                else if (totalDragY > threshold) performSwipe(settingsManager.swipeDownAction)
                            } else {
                                if (totalDragX < -threshold) performSwipe(settingsManager.swipeLeftAction)
                                else if (totalDragX > threshold) performSwipe(settingsManager.swipeRightAction)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = { onOpenSettings() })
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(top = 64.dp, start = 16.dp, end = 16.dp),
                    userScrollEnabled = false,
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    items(gridApps) { app ->
                        AppIcon(
                            app = app,
                            onClick = { startAppOpenAnimation(app, null) },
                            showLabel = settingsManager.showLabels,
                            iconSizeDp = settingsManager.iconSize,
                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                            iconOpacity = settingsManager.iconOpacity,
                            customDrawable = getCustomDrawable(app.packageName),
                            onClickWithBounds = { bounds -> startAppOpenAnimation(app, bounds) }
                        )
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
                    onAppClick = { startAppOpenAnimation(it, null) },
                    onAppClickWithBounds = { app, bounds -> startAppOpenAnimation(app, bounds) }
                )
            }
        }

        // FULL SCREEN BÉZIER EXPANDING APP CARD OVERLAY
        if (animatingApp != null && startBounds != null) {
            val b = startBounds!!
            val tPos = posEasing.transform(p)
            val tW = widthEasing.transform(p)
            val tH = heightEasing.transform(p)
            val tCorner = cornerEasing.transform(p)

            val currentX = b.left * (1f - tPos)
            val currentY = b.top * (1f - tPos)
            val currentW = b.width() + (screenWidthPx - b.width()) * tW
            val currentH = b.height() + (screenHeightPx - b.height()) * tH
            val currentRadius = (28f * (1f - tCorner) + 42f * tCorner)

            with(density) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                        .size(currentW.toDp(), currentH.toDp())
                        .clip(RoundedCornerShape(currentRadius.dp))
                        .background(Color(0xFF18181B)),
                    contentAlignment = Alignment.Center
                ) {
                    val targetDrawable = getCustomDrawable(animatingApp!!.packageName) ?: animatingApp!!.icon
                    val cacheKey = "${animatingApp!!.packageName}_${targetDrawable.hashCode()}"
                    val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

                    if (bitmap != null) {
                        val iconScale = (1f + (0.35f * p))
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .size(settingsManager.iconSize.dp)
                                .scale(iconScale)
                        )
                    }
                }
            }
        }
    }
}
