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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppRepository(this)

        setContent {
            var isDrawerOpen by remember { mutableStateOf(false) }
            val apps = remember { repository.getInstalledApps() }

            Box(modifier = Modifier.fillMaxSize()) {
                HomeScreen(
                    apps = apps,
                    onAppClick = { app -> repository.launchApp(app) },
                    onOpenDrawer = { isDrawerOpen = true }
                )

                if (isDrawerOpen) {
                    AppDrawer(
                        apps = apps,
                        onAppClick = { app ->
                            repository.launchApp(app)
                            isDrawerOpen = false
                        },
                        onCloseDrawer = { isDrawerOpen = false }
                    )
                }
            }
        }
    }
}
