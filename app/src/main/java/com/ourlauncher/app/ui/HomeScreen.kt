package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
                    if (dragAmount < -15f) onOpenDrawer() // Swipe UP opens drawer
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(top = 60.dp, start = 16.dp, end = 16.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(gridApps, key = { it.packageName }) { app ->
                    AppIcon(app = app, onClick = { onAppClick(app) })
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                // Glass Search Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.08f))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.15f))
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .clickable { onOpenDrawer() }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(text = "search", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 🧪 ULTRA LIQUID GLASS DOCK (iOS 18 / Void Style Refraction)
                val dockShape = RoundedCornerShape(32.dp)
                
                // Multi-layered glossy light reflections
                val glassBorderGradient = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.75f), // Top light shine edge
                        Color.White.copy(alpha = 0.15f), // Subtle middle edge
                        Color.White.copy(alpha = 0.35f)  // Soft bottom highlight
                    )
                )
                val glassBgGradient = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.28f), // Glass top sheen
                        Color.White.copy(alpha = 0.08f)  // Translucent body
                    )
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(dockShape)
                        .background(brush = glassBgGradient)
                        .border(width = 1.2.dp, brush = glassBorderGradient, shape = dockShape)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        dockApps.forEach { app ->
                            AppIcon(app = app, onClick = { onAppClick(app) }, showLabel = false, iconSizeDp = 52)
                        }
                    }
                }
            }
        }
    }
}
