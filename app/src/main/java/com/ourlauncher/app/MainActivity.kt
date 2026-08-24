package com.ourlauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ourlauncher.app.ui.AppDrawer
import com.ourlauncher.app.ui.HomeScreen
import com.ourlauncher.app.ui.SettingsScreen
import com.ourlauncher.app.ui.clearIconCache

private enum class Screen { HOME, DRAWER, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppRepository(this)
        val settingsManager = SettingsManager(this)
        val iconPackManager = IconPackManager(this)

        setContent {
            var screen by remember { mutableStateOf(Screen.HOME) }
            val apps = remember { repository.getInstalledApps() }
            val installedPacks = remember { iconPackManager.getInstalledIconPacks() }

            var showLabels by remember { mutableStateOf(settingsManager.showLabels) }
            var dockRadius by remember { mutableStateOf(settingsManager.dockRadius) }
            var showDockBg by remember { mutableStateOf(settingsManager.showDockBg) }
            var searchOffset by remember { mutableStateOf(settingsManager.searchOffset) }

            var iconSize by remember { mutableStateOf(settingsManager.iconSize) }
            var iconCornerRadius by remember { mutableStateOf(settingsManager.iconCornerRadius) }
            var iconOpacity by remember { mutableStateOf(settingsManager.iconOpacity) }
            var selectedIconPack by remember { mutableStateOf(settingsManager.iconPack) }

            var swipeUp by remember { mutableStateOf(settingsManager.swipeUpAction) }
            var swipeDown by remember { mutableStateOf(settingsManager.swipeDownAction) }
            var swipeLeft by remember { mutableStateOf(settingsManager.swipeLeftAction) }
            var swipeRight by remember { mutableStateOf(settingsManager.swipeRightAction) }

            LaunchedEffect(selectedIconPack) {
                iconPackManager.loadIconPack(selectedIconPack)
                clearIconCache()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        apps = apps,
                        showLabels = showLabels,
                        iconSize = iconSize,
                        cornerRadiusPercent = iconCornerRadius,
                        iconOpacity = iconOpacity,
                        getCustomDrawable = { pkg -> iconPackManager.getCustomIcon(pkg) },
                        onAppClick = { app -> repository.launchApp(app) },
                        onAppClickWithBounds = { app, bounds -> repository.launchApp(app, bounds) },
                        onOpenDrawer = { screen = Screen.DRAWER },
                        onOpenSettings = { screen = Screen.SETTINGS },
                        swipeUp = swipeUp,
                        swipeDown = swipeDown,
                        swipeLeft = swipeLeft,
                        swipeRight = swipeRight
                    )

                    Screen.DRAWER -> AppDrawer(
                        apps = apps,
                        iconSize = iconSize,
                        cornerRadiusPercent = iconCornerRadius,
                        iconOpacity = iconOpacity,
                        getCustomDrawable = { pkg -> iconPackManager.getCustomIcon(pkg) },
                        onAppClick = { app ->
                            repository.launchApp(app)
                            screen = Screen.HOME
                        },
                        onAppClickWithBounds = { app, bounds ->
                            repository.launchApp(app, bounds)
                            screen = Screen.HOME
                        },
                        onCloseDrawer = { screen = Screen.HOME }
                    )

                    Screen.SETTINGS -> SettingsScreen(
                        onBack = { screen = Screen.HOME },
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
                        },
                        iconSize = iconSize,
                        onIconSizeChange = {
                            iconSize = it
                            settingsManager.iconSize = it
                        },
                        iconCornerRadius = iconCornerRadius,
                        onIconCornerRadiusChange = {
                            iconCornerRadius = it
                            settingsManager.iconCornerRadius = it
                        },
                        iconOpacity = iconOpacity,
                        onIconOpacityChange = {
                            iconOpacity = it
                            settingsManager.iconOpacity = it
                        },
                        installedIconPacks = installedPacks,
                        selectedIconPack = selectedIconPack,
                        onIconPackSelect = {
                            selectedIconPack = it
                            settingsManager.iconPack = it
                        },
                        swipeUp = swipeUp,
                        onSwipeUpChange = {
                            swipeUp = it
                            settingsManager.swipeUpAction = it
                        },
                        swipeDown = swipeDown,
                        onSwipeDownChange = {
                            swipeDown = it
                            settingsManager.swipeDownAction = it
                        },
                        swipeLeft = swipeLeft,
                        onSwipeLeftChange = {
                            swipeLeft = it
                            settingsManager.swipeLeftAction = it
                        },
                        swipeRight = swipeRight,
                        onSwipeRightChange = {
                            swipeRight = it
                            settingsManager.swipeRightAction = it
                        }
                    )
                }
            }
        }
    }
}
