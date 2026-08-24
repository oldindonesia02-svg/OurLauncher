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
            var selectedIconPack by remember { mutableStateOf(settingsManager.iconPack) }

            LaunchedEffect(selectedIconPack) {
                iconPackManager.loadIconPack(selectedIconPack)
                clearIconCache()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        apps = apps,
                        settingsManager = settingsManager,
                        getCustomDrawable = { pkg -> iconPackManager.getCustomIcon(pkg) },
                        onAppClick = { app -> repository.launchApp(app) },
                        onAppClickWithBounds = { app, bounds -> repository.launchApp(app, bounds) },
                        onOpenDrawer = { screen = Screen.DRAWER },
                        onOpenSettings = { screen = Screen.SETTINGS }
                    )

                    Screen.DRAWER -> AppDrawer(
                        apps = apps,
                        iconSize = settingsManager.iconSize,
                        cornerRadiusPercent = settingsManager.iconCornerRadius,
                        iconOpacity = settingsManager.iconOpacity,
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
                        settingsManager = settingsManager,
                        installedIconPacks = installedPacks,
                        selectedIconPack = selectedIconPack,
                        onIconPackSelect = {
                            selectedIconPack = it
                            settingsManager.iconPack = it
                        }
                    )
                }
            }
        }
    }
}
