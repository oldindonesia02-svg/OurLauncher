package com.ourlauncher.app.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    showLabels: Boolean,
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, android.graphics.Rect) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val dockApps = apps.take(4)
    val gridApps = apps.drop(4).take(20)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change: androidx.compose.ui.input.pointer.PointerInputChange, dragAmount: Float ->
                        if (dragAmount < -20) onOpenDrawer()
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
                        showLabel = showLabels,
                        onClickWithBounds = { bounds -> onAppClickWithBounds(app, bounds) }
                    )
                }
            }

            SearchPill(onClick = onOpenDrawer, modifier = Modifier.padding(bottom = 8.dp))
            Dock(pinnedApps = dockApps, onAppClick = onAppClick, onAppClickWithBounds = onAppClickWithBounds)
        }
    }
}
