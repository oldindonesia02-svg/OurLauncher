package com.ourlauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ourlauncher.app.ui.*

class MainActivity : ComponentActivity() {
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)

        setContent {
            var currentScreen by remember { mutableStateOf("home") }
            var showSearchBarPositionDialog by remember { mutableStateOf(false) }
            var showIconStudioSheet by remember { mutableStateOf(false) }

            BackHandler(enabled = currentScreen != "home") {
                if (currentScreen == "liquid_glass" || currentScreen == "swipe_actions") {
                    currentScreen = "settings"
                } else {
                    currentScreen = "home"
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (currentScreen) {
                    "home" -> {
                        HomeScreen(
                            settingsManager = settingsManager,
                            onOpenSettings = { currentScreen = "settings" },
                            onOpenIconStudio = { showIconStudioSheet = true }
                        )
                    }

                    "settings" -> {
                        SettingsScreen(
                            settingsManager = settingsManager,
                            onBack = { currentScreen = "home" },
                            onOpenAppIcons = { showIconStudioSheet = true },
                            onOpenLiquidGlass = { currentScreen = "liquid_glass" },
                            onOpenSwipeActions = { currentScreen = "swipe_actions" },
                            onOpenSearchBarPosition = { showSearchBarPositionDialog = true }
                        )
                    }

                    "liquid_glass" -> {
                        LiquidGlassScreen(
                            settingsManager = settingsManager,
                            onBack = { currentScreen = "settings" }
                        )
                    }

                    "swipe_actions" -> {
                        SwipeActionsScreen(
                            settingsManager = settingsManager,
                            onBack = { currentScreen = "settings" }
                        )
                    }
                }

                if (showSearchBarPositionDialog) {
                    TopLiquidSearchBarPositionCard(
                        currentOffset = settingsManager.searchBarOffset,
                        isCapsuleHidden = settingsManager.isSearchCapsuleHidden,
                        onOffsetChange = { settingsManager.searchBarOffset = it },
                        onHideCapsuleChange = { settingsManager.isSearchCapsuleHidden = it },
                        onOpenDockPosition = { },
                        onApply = { showSearchBarPositionDialog = false },
                        onDismiss = { showSearchBarPositionDialog = false }
                    )
                }

                if (showIconStudioSheet) {
                    IconStudioSheet(
                        settingsManager = settingsManager,
                        onApply = { showIconStudioSheet = false },
                        onDismiss = { showIconStudioSheet = false }
                    )
                }
            }
        }
    }
}
