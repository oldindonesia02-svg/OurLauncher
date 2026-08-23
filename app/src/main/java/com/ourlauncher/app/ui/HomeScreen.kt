package com.ourlauncher.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var isEditMode by remember { mutableStateOf(false) }
    var isPreviewHidden by remember { mutableStateOf(false) }

    val dockApps = apps.take(4)
    val gridApps = apps.drop(4).take(20)

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Long Press on empty screen enters Customization Mode!
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { isEditMode = true }
                )
            }
            // Swipe Up opens App Drawer
            .pointerInput(Unit) {
                detectVerticalDragGestures { _: PointerInputChange, dragAmount: Float ->
                    if (!isEditMode && dragAmount < -15f) onOpenDrawer()
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Space or Edit Mode Header
            if (isEditMode) {
                TopEditBar(
                    isPreviewHidden = isPreviewHidden,
                    onCancel = { isEditMode = false; isPreviewHidden = false },
                    onTogglePreview = { isPreviewHidden = !isPreviewHidden },
                    onDone = { isEditMode = false; isPreviewHidden = false }
                )
            } else {
                Spacer(modifier = Modifier.height(40.dp))
            }

            // App Icons Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(top = 20.dp, start = 16.dp, end = 16.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(gridApps, key = { it.packageName }) { app ->
                    Box {
                        AppIcon(app = app, onClick = {
                            if (!isEditMode) onAppClick(app)
                        })

                        // Small edit badge dot when in Edit Mode (Void Style)
                        if (isEditMode && !isPreviewHidden) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            )
                        }
                    }
                }
            }

            // Bottom Area (Swaps between Glass Dock and Customization Menu)
            if (isEditMode && !isPreviewHidden) {
                BottomCustomizationMenu(
                    onWallpaperClick = {},
                    onDeveloperClick = {},
                    onWidgetsClick = {},
                    onSettingsClick = {}
                )
            } else {
                VoidBottomBar(
                    dockApps = dockApps,
                    onAppClick = onAppClick,
                    onOpenDrawer = onOpenDrawer,
                    onSettingsClick = { isEditMode = true }
                )
            }
        }
    }
}

/**
 * Top Header in Customization Mode (Cancel | Eye | Done)
 */
@Composable
fun TopEditBar(
    isPreviewHidden: Boolean,
    onCancel: () -> Unit,
    onTogglePreview: () -> Unit,
    onDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.18f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .clickable { onCancel() }
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text("Cancel", color = Color.White, fontSize = 15.sp)
        }

        // Eye (Preview Toggle) Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                .clickable { onTogglePreview() },
            contentAlignment = Alignment.Center
        ) {
            Text(if (isPreviewHidden) "👁️" else "👁", fontSize = 16.sp)
        }

        // Done Pill (Bright Blue)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF007AFF))
                .clickable { onDone() }
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text("Done", color = Color.White, fontSize = 15.sp)
        }
    }
}

/**
 * Void Style Bottom Area (Search Pill + Cube Button + Liquid Glass Dock)
 */
@Composable
fun VoidBottomBar(
    dockApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        // Search Pill + Hex/Cube Settings Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Search Pill
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

            Spacer(modifier = Modifier.width(8.dp))

            // Small Hexagon / Cube Settings Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⬡", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ULTRA LIQUID GLASS DOCK
        val dockShape = RoundedCornerShape(32.dp)
        val glassBorderGradient = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.75f),
                Color.White.copy(alpha = 0.15f),
                Color.White.copy(alpha = 0.35f)
            )
        )
        val glassBgGradient = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.28f),
                Color.White.copy(alpha = 0.08f)
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

/**
 * Bottom Bar in Customization Mode (Wallpaper | Developer | Widgets | Settings)
 */
@Composable
fun BottomCustomizationMenu(
    onWallpaperClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onWidgetsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Small pill line handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.4f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomizationIconButton(icon = "🖼️", label = "Wallpaper", onClick = onWallpaperClick)
            CustomizationIconButton(icon = "🧊", label = "Developer", onClick = onDeveloperClick)
            CustomizationIconButton(icon = "🗂️", label = "Widgets", onClick = onWidgetsClick)
            CustomizationIconButton(icon = "⚙️", label = "Settings", onClick = onSettingsClick)
        }
    }
}

@Composable
fun CustomizationIconButton(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.08f))
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
