package com.ourlauncher.app.ui

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()

    var animatingApp by remember { mutableStateOf<AppInfo?>(null) }
    var startBounds by remember { mutableStateOf<android.graphics.Rect?>(null) }
    var animProgress by remember { mutableStateOf(0f) }

    val easing = remember(
        settingsManager.posCurveX1,
        settingsManager.posCurveY1,
        settingsManager.posCurveX2,
        settingsManager.posCurveY2
    ) {
        CubicBezierEasing(
            settingsManager.posCurveX1.coerceIn(0f, 1f),
            settingsManager.posCurveY1.coerceIn(0f, 1.5f),
            settingsManager.posCurveX2.coerceIn(0f, 1f),
            settingsManager.posCurveY2.coerceIn(0f, 1.5f)
        )
    }

    val animatedScale by animateFloatAsState(
        targetValue = animProgress,
        animationSpec = tween(
            durationMillis = settingsManager.animDuration.toInt(),
            easing = easing
        ),
        label = "appOpen"
    )

    fun launchWithAnimation(app: AppInfo, bounds: android.graphics.Rect?) {
        if (!settingsManager.animEnabled || bounds == null) {
            if (bounds != null) onAppClickWithBounds(app, bounds) else onAppClick(app)
            return
        }

        animatingApp = app
        startBounds = bounds
        animProgress = 1f

        coroutineScope.launch {
            delay(settingsManager.animDuration.toLong())
            onAppClickWithBounds(app, bounds)
            delay(150)
            animProgress = 0f
            animatingApp = null
        }
    }

    fun performSwipe(action: String) {
        when (action) {
            "drawer" -> onOpenDrawer()
            "settings" -> onOpenSettings()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var totalDragX = 0f
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDrag = { _, dragAmount ->
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                    },
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
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(gridApps) { app ->
                    AppIcon(
                        app = app,
                        onClick = { launchWithAnimation(app, null) },
                        showLabel = settingsManager.showLabels,
                        iconSizeDp = settingsManager.iconSize,
                        cornerRadiusPercent = settingsManager.iconCornerRadius,
                        iconOpacity = settingsManager.iconOpacity,
                        customDrawable = getCustomDrawable(app.packageName),
                        onClickWithBounds = { bounds -> launchWithAnimation(app, bounds) }
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
                onAppClick = { launchWithAnimation(it, null) },
                onAppClickWithBounds = { app, bounds -> launchWithAnimation(app, bounds) }
            )
        }

        // Seamless App Open Expanding Layer
        if (animatingApp != null && startBounds != null) {
            val b = startBounds!!
            val currWidth = b.width() + (screenWidth * 2.5f - b.width()) * animatedScale
            val currHeight = b.height() + (screenHeight * 2.5f - b.height()) * animatedScale
            val currX = b.left - (b.left * animatedScale)
            val currY = b.top - (b.top * animatedScale)

            Box(
                modifier = Modifier
                    .offset { IntOffset(currX.roundToInt(), currY.roundToInt()) }
                    .size(currWidth.dp, currHeight.dp)
                    .clip(RoundedCornerShape(((1f - animatedScale) * 24).dp))
                    .background(Color(0xFF141416).copy(alpha = animatedScale.coerceIn(0.2f, 0.95f)))
            )
        }
    }
}
