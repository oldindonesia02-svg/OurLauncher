package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo

@Composable
fun AppDrawer(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val gridState = rememberLazyGridState()

    // Ultra-sensitive Instant Swipe Down Connection
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // If user drags DOWN (> 8f) and grid is at top, close immediately!
                if (available.y > 8f && gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0) {
                    onCloseDrawer()
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }
        }
    }

    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    // Frosted Translucent Liquid Glass Background Gradient (Wallpaper shines through)
    val glassBgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF141418).copy(alpha = 0.72f), // Glass top sheen
            Color(0xFF0A0A0D).copy(alpha = 0.88f)  // Soft dark glass bottom
        )
    )

    // iOS Style Glossy Top Border Reflection
    val glassBorderGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.08f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _: PointerInputChange, dragAmount: Float ->
                    if (dragAmount > 10f) onCloseDrawer()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp)
                .clip(sheetShape)
                .background(brush = glassBgGradient)
                .border(width = 1.dp, brush = glassBorderGradient, shape = sheetShape)
                .nestedScroll(nestedScrollConnection)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Pull Handle Line
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 42.dp, height = 5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.45f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Glass Search Bar
            Box(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(22.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Search apps...",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fast App Drawer Grid
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 40.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = apps,
                    key = { app -> app.packageName }
                ) { app ->
                    AppIcon(app = app, onClick = { onAppClick(app) })
                }
            }
        }
    }
}
