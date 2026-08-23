package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo

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
                detectVerticalDragGestures { _: PointerInputChange, dragAmount: Float ->
                    if (dragAmount < -20) onOpenDrawer() // Swipe UP opens drawer
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. App Grid (Top Area)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(top = 64.dp, start = 16.dp, end = 16.dp),
                userScrollEnabled = false,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(gridApps) { app ->
                    AppIcon(app = app, onClick = { onAppClick(app) })
                }
            }

            // 2. Void Style Bottom Area
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Small Pill Search
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .clickable { onOpenDrawer() }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text("search", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Glass Dock
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        dockApps.forEach { app ->
                            AppIcon(app = app, onClick = { onAppClick(app) })
                        }
                    }
                }
            }
        }
    }
}
