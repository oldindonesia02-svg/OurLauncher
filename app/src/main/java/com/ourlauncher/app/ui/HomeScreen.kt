package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo

/**
 * Phase 1 home screen: single page, fixed (non-scrolling) grid of apps + glass dock at bottom.
 */
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val dockApps = apps.take(4)
    val gridApps = apps.drop(4).take(20)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(onVerticalDrag = { _: PointerInputChange, dragAmount: Float ->
                    if (dragAmount < -20) onOpenDrawer()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            // App Grid Space
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

            // 🧪 Liquid Glass Search Pill
            LiquidGlassBox(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                cornerRadius = 30
            ) {
                SearchPill(onClick = onOpenDrawer, modifier = Modifier.fillMaxWidth())
            }

            // 🧪 Liquid Glass Dock Container
            LiquidGlassBox(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                cornerRadius = 28
            ) {
                Dock(pinnedApps = dockApps, onAppClick = onAppClick)
            }
        }
    }
}

/**
 * Reusable iOS Liquid Glass Container Component
 */
@Composable
fun LiquidGlassBox(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 28,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius.dp)

    // iOS Glossy Edge Reflection Gradient
    val glassBorder = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.45f), // Top light shine
            Color.White.copy(alpha = 0.08f), // Transparent center
            Color.White.copy(alpha = 0.25f)  // Bottom soft shine
        )
    )

    // Semi-transparent Frosted Glass Background Tint
    val glassBackground = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.05f)
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = glassBackground)
            .border(width = 1.dp, brush = glassBorder, shape = shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}
