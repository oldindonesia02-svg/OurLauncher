package com.ourlauncher.app.ui

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointerInput
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo

/**
 * Phase 1 home screen: single page, fixed (non-scrolling) grid of apps + dock at bottom.
 *
 * Not yet implemented (later phases): multiple horizontal pages, drag-to-reorder,
 * folders, widgets. Swiping up anywhere on the background opens the app drawer,
 * same as tapping the search pill — mirrors standard Android launcher behavior.
 */
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val dockApps = apps.take(4)
    val gridApps = apps.drop(4).take(20) // first "page" placeholder; paging comes in Phase 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -20) onOpenDrawer()
                }
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
                    AppIcon(app = app, onClick = { onAppClick(app) })
                }
            }

            SearchPill(onClick = onOpenDrawer, modifier = Modifier.padding(bottom = 8.dp))
            Dock(pinnedApps = dockApps, onAppClick = onAppClick)
        }
    }
}
