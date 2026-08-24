package com.ourlauncher.app.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlin.math.abs

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
                        onClick = { onAppClick(app) },
                        showLabel = settingsManager.showLabels,
                        iconSizeDp = settingsManager.iconSize,
                        cornerRadiusPercent = settingsManager.iconCornerRadius,
                        iconOpacity = settingsManager.iconOpacity,
                        customDrawable = getCustomDrawable(app.packageName),
                        onClickWithBounds = { bounds -> onAppClickWithBounds(app, bounds) }
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
                onAppClick = onAppClick,
                onAppClickWithBounds = onAppClickWithBounds
            )
        }
    }
}
