package com.ourlauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ourlauncher.app.ui.AppDrawerScreen
import com.ourlauncher.app.ui.HomeScreen

/**
 * Single-activity launcher. Phase 1: a home screen (grid + dock) that can be
 * swiped up (or tap the search pill) into a full app drawer.
 *
 * Note: launchMode="singleTask" in the manifest means pressing Home again while
 * already here just re-delivers onNewIntent — that's standard launcher behavior.
 */
class MainActivity : ComponentActivity() {

    private lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appRepository = AppRepository(applicationContext)

        setContent {
            MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(background = Color.Transparent)) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    var showDrawer by remember { mutableStateOf(false) }
                    val apps = remember { appRepository.getAllApps() }

                    if (showDrawer) {
                        AppDrawerScreen(
                            apps = apps,
                            onAppClick = { app ->
                                appRepository.launchApp(app)
                                showDrawer = false
                            },
                            onDismiss = { showDrawer = false }
                        )
                    } else {
                        HomeScreen(
                            apps = apps,
                            onAppClick = { app -> appRepository.launchApp(app) },
                            onOpenDrawer = { showDrawer = true }
                        )
                    }
                }
            }
        }
    }
}
