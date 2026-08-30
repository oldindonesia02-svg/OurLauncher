package com.ourlauncher.app.ui

import android.content.pm.ApplicationInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager

@Composable
fun AppDrawerScreen(
    apps: List<AppInfo>,
    settingsManager: SettingsManager,
    onAppClick: (AppInfo) -> Unit,
    onCloseDrawer: () -> Unit
) {
    BackHandler { onCloseDrawer() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    // Category Definitions
    val categories = remember {
        listOf(
            DrawerCategoryItem("All", Icons.Rounded.Apps),
            DrawerCategoryItem("Social", Icons.Rounded.Chat),
            DrawerCategoryItem("Games", Icons.Rounded.SportsEsports),
            DrawerCategoryItem("Tools", Icons.Rounded.Build)
        )
    }

    // Filter apps based on Tab & Search Query
    val filteredApps = remember(apps, searchQuery, selectedCategoryIndex) {
        val pm = context.packageManager
        apps.filter { app ->
            val matchesSearch = app.label.contains(searchQuery, ignoreCase = true)
            val matchesCategory = when (selectedCategoryIndex) {
                1 -> { // Social / Communication
                    val pkg = app.packageName.lowercase()
                    pkg.contains("whatsapp") || pkg.contains("telegram") || pkg.contains("facebook") ||
                            pkg.contains("instagram") || pkg.contains("twitter") || pkg.contains("messenger")
                }
                2 -> { // Games
                    try {
                        val appInfo = pm.getApplicationInfo(app.packageName, 0)
                        appInfo.category == ApplicationInfo.CATEGORY_GAME
                    } catch (e: Exception) {
                        false
                    }
                }
                3 -> { // Tools & Utilities
                    val pkg = app.packageName.lowercase()
                    pkg.contains("settings") || pkg.contains("tool") || pkg.contains("calc") ||
                            pkg.contains("camera") || pkg.contains("file") || pkg.contains("browser")
                }
                else -> true // All
            }
            matchesSearch && matchesCategory
        }
    }

    // 100% Transparent Drawer Container (Wallpaper Remains Clearly Visible)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.15f)) // Ultra-light pass-through tint
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Liquid Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                LiquidDrawerSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            // Apps Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(settingsManager.gridColumns),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppIcon(
                        app = app,
                        onClick = { onAppClick(app) },
                        showLabel = settingsManager.showLabels,
                        fontFamilyName = settingsManager.fontFamily,
                        iconSizeDp = settingsManager.iconSize,
                        cornerRadiusPercent = settingsManager.iconCornerRadius,
                        iconOpacity = settingsManager.iconOpacity,
                        modifier = Modifier.width(82.dp)
                    )
                }
            }
        }

        // Pinned Liquid Bottom Category Tabs (Image 2 Look)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            DrawerLiquidBottomTabs(
                categories = categories,
                selectedIndex = selectedCategoryIndex,
                onTabSelected = { selectedCategoryIndex = it }
            )
        }
    }
}
