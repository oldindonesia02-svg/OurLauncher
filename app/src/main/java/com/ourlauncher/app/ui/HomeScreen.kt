package com.ourlauncher.app.ui

import android.app.WallpaperManager
import android.content.Intent
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    var isEditMode by remember { mutableStateOf(false) }
    var isPreviewHidden by remember { mutableStateOf(false) }
    var showHomeSettings by remember { mutableStateOf(false) }
    var showFullSettings by remember { mutableStateOf(false) }

    // Live Customization Settings States
    var dockRadius by remember { mutableStateOf(32f) }
    var showDockBg by remember { mutableStateOf(true) }
    var searchOffset by remember { mutableStateOf(0f) }

    val dockApps = apps.take(4)
    val gridApps = apps.drop(4).take(12)

    if (showFullSettings) {
        SettingsScreen(
            onBack = { showFullSettings = false },
            dockRadius = dockRadius,
            onDockRadiusChange = { newRadius -> dockRadius = newRadius },
            showDockBg = showDockBg,
            onShowDockBgChange = { newBg -> showDockBg = newBg }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { isEditMode = true })
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _: PointerInputChange, dragAmount: Float ->
                    if (!isEditMode && dragAmount < -15f) onOpenDrawer()
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            if (isEditMode) {
                TopEditBar(
                    isPreviewHidden = isPreviewHidden,
                    onCancel = { isEditMode = false; isPreviewHidden = false },
                    onTogglePreview = { isPreviewHidden = !isPreviewHidden },
                    onDone = { isEditMode = false; isPreviewHidden = false }
                )
            } else {
                Spacer(modifier = Modifier.height(44.dp))
            }

            // Clean 4x3 Grid Layout
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(gridApps) { app ->
                    Box {
                        AppIcon(app = app, onClick = {
                            if (!isEditMode) onAppClick(app)
                        })
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

            if (isEditMode && !isPreviewHidden) {
                BottomCustomizationMenu(
                    onWallpaperClick = {
                        try {
                            val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                            context.startActivity(Intent.createChooser(intent, "Select Wallpaper"))
                        } catch (e: Exception) {
                            val intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                            context.startActivity(intent)
                        }
                    },
                    onDeveloperClick = {},
                    onWidgetsClick = {},
                    onSettingsClick = { showHomeSettings = true }
                )
            } else {
                VoidBottomBar(
                    dockApps = dockApps,
                    onAppClick = onAppClick,
                    onOpenDrawer = onOpenDrawer,
                    onSettingsClick = { isEditMode = true },
                    dockRadius = dockRadius,
                    showDockBg = showDockBg,
                    searchOffset = searchOffset
                )
            }
        }

        if (showHomeSettings) {
            HomeScreenSettingsSheet(
                onDismiss = { showHomeSettings = false },
                onOpenMoreSettings = {
                    showHomeSettings = false
                    showFullSettings = true
                }
            )
        }
    }
}

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

@Composable
fun VoidBottomBar(
    dockApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit,
    onSettingsClick: () -> Unit,
    dockRadius: Float,
    showDockBg: Boolean,
    searchOffset: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = searchOffset.dp)
        ) {
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
                Text("search", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("⬡", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val dockShape = RoundedCornerShape(dockRadius.dp)
        val glassBorder = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.75f), Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.35f))
        )
        val glassBg = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.08f))
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(dockShape)
                .then(
                    if (showDockBg) {
                        Modifier
                            .background(brush = glassBg)
                            .border(width = 1.2.dp, brush = glassBorder, shape = dockShape)
                    } else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dockApps.forEach { app ->
                    AppIcon(app = app, onClick = { onAppClick(app) }, showLabel = false, iconSizeDp = 48)
                }
            }
        }
    }
}

@Composable
fun BottomCustomizationMenu(
    onWallpaperClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onWidgetsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
    ) {
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
            CustomizationIconButton("🖼️", "Wallpaper", onWallpaperClick)
            CustomizationIconButton("🧊", "Developer", onDeveloperClick)
            CustomizationIconButton("🗂️", "Widgets", onWidgetsClick)
            CustomizationIconButton("⚙️", "Settings", onSettingsClick)
        }
    }
}

@Composable
fun CustomizationIconButton(icon: String, label: String, onClick: () -> Unit) {
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
            Text(icon, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}
