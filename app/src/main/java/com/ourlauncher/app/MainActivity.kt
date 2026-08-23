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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppRepository(this)

        setContent {
            var isDrawerOpen by remember { mutableStateOf(false) }
            val apps = remember { repository.getInstalledApps() }

            Box(modifier = Modifier.fillMaxSize()) {
                // Main Home Screen
                HomeScreen(
                    apps = apps,
                    onAppClick = { app -> repository.launchApp(app.packageName) },
                    onOpenDrawer = { isDrawerOpen = true }
                )

                // App Drawer overlay (shows when swiped up)
                if (isDrawerOpen) {
                    AppDrawer(
                        apps = apps,
                        onAppClick = { app ->
                            repository.launchApp(app.packageName)
                            isDrawerOpen = false
                        },
                        onCloseDrawer = { isDrawerOpen = false } // Swiping DOWN closes drawer!
                    )
                }
            }
        }
    }
}
