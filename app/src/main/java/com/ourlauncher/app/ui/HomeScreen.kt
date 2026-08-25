package com.ourlauncher.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.SettingsManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    settingsManager: SettingsManager,
    onOpenSettings: () -> Unit,
    onOpenIconStudio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalPages = 2
    val pagerState = rememberPagerState(pageCount = { totalPages })

    var showQuickSettings by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
            }
        }

        if (!settingsManager.isSearchCapsuleHidden) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = (84 + settingsManager.searchBarOffset.toInt()).dp)
            ) {
                LiquidSearchAiCapsule(
                    pagerState = pagerState,
                    totalPages = totalPages,
                    onSearchClick = {
                        try {
                            val intent = Intent(Intent.ACTION_WEB_SEARCH)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                            context.startActivity(browserIntent)
                        }
                    },
                    onAiClick = {
                        launchGeminiAi(context)
                    }
                )
            }
        }

        if (showQuickSettings) {
            HomeQuickSettingsSheet(
                settingsManager = settingsManager,
                onOpenFullSettings = {
                    showQuickSettings = false
                    onOpenSettings()
                },
                onOpenIconCustomize = {
                    showQuickSettings = false
                    onOpenIconStudio()
                },
                onDismiss = { showQuickSettings = false }
            )
        }
    }
}
