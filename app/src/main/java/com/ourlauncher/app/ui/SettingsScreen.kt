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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    var isEditMode by remember { mutableStateOf(false) }
    var isPreviewHidden by remember { mutableStateOf(false) }
    var showHomeSettings by remember { mutableStateOf(false) }
    var showDockPopup by remember { mutableStateOf(false) }
    var showFullSettings by remember { mutableStateOf(false) }

    var dockRadius by remember { mutableStateOf(settingsManager.dockRadius) }
    var showDockBg by remember { mutableStateOf(settingsManager.showDockBg) }
    var searchOffset by remember { mutableStateOf(settingsManager.searchOffset) }
    var selectedEffect by remember { mutableStateOf("Crossfade") }

    val dockApps = apps.take(4)
    val gridApps = apps.drop(4).take(11)

    if (showFullSettings) {
        SettingsScreen(
            onBack = { showFullSettings = false },
            dockRadius = dockRadius,
            onDockRadiusChange = {
                dockRadius = it
                settingsManager.dockRadius = it
            },
            showDockBg = showDockBg,
            onShowDockBgChange = {
                showDockBg = it
                settingsManager.showDockBg = it
            },
            searchOffset = searchOffset,
            onSearchOffsetChange = {
                searchOffset = it
                settingsManager.searchOffset = it
            }
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
                item {
                    LiquidGlassFolderItem(
                        onFolderClick = onOpenDrawer,
                        isEditMode = isEditMode && !isPreviewHidden
                    )
                }

                items(gridApps) { app ->
                    Box {
                        AppIcon(app = app, onClick = { if (!isEditMode) onAppClick(app) })
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
                    onWidgetsClick = { showDockPopup = true },
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

        if (showDockPopup) {
            DockCustomizationPopup(
                showDockBg = showDockBg,
                onShowDockBgChange = {
                    showDockBg = it
                    settingsManager.showDockBg = it
                },
                dockRadius = dockRadius,
                onDockRadiusChange = {
                    dockRadius = it
                    settingsManager.dockRadius = it
                },
                searchOffset = searchOffset,
                onSearchOffsetChange = {
                    searchOffset = it
                    settingsManager.searchOffset = it
                },
                onClose = { showDockPopup = false }
            )
        }

        if (showHomeSettings) {
            HomeScreenSettingsSheet(
                onDismiss = { showHomeSettings = false },
                onOpenMoreSettings = {
                    showHomeSettings = false
                    showFullSettings = true
                },
                selectedEffect = selectedEffect,
                onEffectSelect = { selectedEffect = it }
            )
        }
    }
}

@Composable
fun LiquidGlassFolderItem(
    onFolderClick: () -> Unit,
    isEditMode: Boolean
) {
    Box {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.08f))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.15f))
                    ),
                    RoundedCornerShape(18.dp)
                )
                .clickable { onFolderClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9D00FF))
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF5500))
                )
            }
        }

        if (isEditMode) {
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

@Composable
fun DockCustomizationPopup(
    showDockBg: Boolean,
    onShowDockBgChange: (Boolean) -> Unit,
    dockRadius: Float,
    onDockRadiusChange: (Float) -> Unit,
    searchOffset: Float,
    onSearchOffsetChange: (Float) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1C1C1E))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reset",
                    color = Color(0xFF0A84FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onDockRadiusChange(32f)
                        onShowDockBgChange(true)
                        onSearchOffsetChange(0f)
                    }
                )
                Text(
                    text = "Dock customization",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✕",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onClose() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2C2C2E))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Show dock background", color = Color.White, fontSize = 15.sp)
                    Text("Show or hide the dock's glass backdrop", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                }
                Switch(
                    checked = showDockBg,
                    onCheckedChange = onShowDockBgChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF0A84FF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2C2C2E))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dock corner radius", color = Color.White, fontSize = 15.sp)
                    Text("${((dockRadius / 50f) * 100).toInt()}%", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
                Slider(
                    value = dockRadius,
                    onValueChange = onDockRadiusChange,
                    valueRange = 8f..50f,
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2C2C2E))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dock vertical offset", color = Color.White, fontSize = 15.sp)
                    Text("${searchOffset.toInt()} dp", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
                Slider(
                    value = searchOffset,
                    onValueChange = onSearchOffsetChange,
                    valueRange = -30f..30f,
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color(0xFF0A84FF))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A84FF))
                    .clickable { onClose() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Apply", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
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
         
