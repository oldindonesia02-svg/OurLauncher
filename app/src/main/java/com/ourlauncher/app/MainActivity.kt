package com.ourlauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ourlauncher.app.ui.AppDrawer
import com.ourlauncher.app.ui.HomeScreen
import com.ourlauncher.app.ui.SettingsScreen

private enum class Screen { HOME, DRAWER, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppRepository(this)
        val settingsManager = SettingsManager(this)

        setContent {
            var screen by remember { mutableStateOf(Screen.HOME) }
            val apps = remember { repository.getInstalledApps() }

            var showLabels by remember { mutableStateOf(settingsManager.showLabels) }
            var dockRadius by remember { mutableStateOf(settingsManager.dockRadius) }
            var showDockBg by remember { mutableStateOf(settingsManager.showDockBg) }
            var searchOffset by remember { mutableStateOf(settingsManager.searchOffset) }

            Box(modifier = Modifier.fillMaxSize()) {
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        apps = apps,
                        showLabels = showLabels,
                        onAppClick = { app -> repository.launchApp(app) },
                        onAppClickWithBounds = { app, bounds -> repository.launchApp(app, bounds) },
                        onOpenDrawer = { screen = Screen.DRAWER },
                        onOpenSettings = { screen = Screen.SETTINGS }
                    )
                    Screen.DRAWER -> AppDrawer(
                        apps = apps,
                        onAppClick = { app ->
                            repository.launchApp(app)
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
                        }
                    )
                }
            }
        }
    }
}
