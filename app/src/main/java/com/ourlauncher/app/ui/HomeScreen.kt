package com.ourlauncher.app.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    settingsManager: SettingsManager,
    onOpenSettings: () -> Unit,
    onOpenIconStudio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var showQuickSettings by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }

    // ১. ডিভাইসের সব ইনস্টলড অ্যাপ লোড করা
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val apps = resolveInfos.map { resolveInfo ->
                AppInfo(
                    label = resolveInfo.loadLabel(pm).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = resolveInfo.loadIcon(pm)
                )
            }.sortedBy { it.label.lowercase() }
            installedApps = apps
        }
    }

    // প্রতি পেজে ২০টি করে অ্যাপ (৪x৫ গ্রিড) এবং নিচের ডকের জন্য ৪টি ফেভারিট অ্যাপ
    val appsPerPage = 20
    val dockApps = remember(installedApps) { installedApps.take(4) }
    val homeApps = remember(installedApps) { installedApps.drop(4) }
    val totalPages = remember(homeApps) {
        val pages = (homeApps.size + appsPerPage - 1) / appsPerPage
        if (pages > 0) pages else 1
    }
    val pagerState = rememberPagerState(pageCount = { totalPages })

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // সোয়াইপ ডাউন অ্যাকশন
                        if (dragAmount.y > 45) {
                            val screenWidth = context.resources.displayMetrics.widthPixels
                            if (change.position.x < screenWidth / 2) {
                                triggerPullDownAction(settingsManager.leftPullDownAction, context, onOpenSettings)
                            } else {
                                triggerPullDownAction(settingsManager.rightPullDownAction, context, onOpenSettings)
                            }
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp)
        ) {
            // ==================== APP GRID PAGER ====================
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val startIdx = pageIndex * appsPerPage
                val endIdx = (startIdx + appsPerPage).coerceAtMost(homeApps.size)
                val pageApps = if (startIdx < homeApps.size) homeApps.subList(startIdx, endIdx) else emptyList()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(pageApps, key = { it.packageName }) { app ->
                        AppItemView(
                            app = app,
                            settingsManager = settingsManager,
                            isEditMode = isEditMode,
                            onClick = {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            },
                            onLongClick = {
                                showQuickSettings = true
                            }
                        )
                    }
                }
            }

            // ==================== LIQUID SEARCH CAPSULE ====================
            if (!settingsManager.isSearchCapsuleHidden && !isEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = settingsManager.searchBarOffset.dp)
                        .padding(bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LiquidSearchAiCapsule(
                        pagerState = pagerState,
                        totalPages = totalPages,
                        onSearchClick = {
                            try {
                                val searchIntent = Intent(Intent.ACTION_WEB_SEARCH)
                                context.startActivity(searchIntent)
                            } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")))
                            }
                        },
                        onAiClick = { launchGeminiAi(context) }
                    )
                }
            }

            // ==================== BOTTOM DOCK ====================
            if (!isEditMode && dockApps.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .offset(y = settingsManager.dockOffset.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(settingsManager.dockRadius.dp))
                            .background(
                                if (settingsManager.showDockBg) Color.White.copy(alpha = 0.08f) else Color.Transparent
                            )
                            .padding(
                                horizontal = settingsManager.dockPadding.dp,
                                vertical = 8.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dockApps.forEach { app ->
                            AppItemView(
                                app = app,
                                settingsManager = settingsManager,
                                isEditMode = false,
                                onClick = {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    }
                                },
                                onLongClick = { showQuickSettings = true }
                            )
                        }
                    }
                }
            }
        }

        // ==================== EDIT MODE OVERLAY ====================
        AnimatedVisibility(
            visible = isEditMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                EditModeTopBar(
                    onCancel = { isEditMode = false },
                    onToggleVisibility = { },
                    onDone = { isEditMode = false },
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                EditModeBottomBar(
                    onWallpaperClick = { },
                    onDeveloperClick = { },
                    onWidgetsClick = { },
                    onSettingsClick = {
                        isEditMode = false
                        showQuickSettings = true
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // ==================== QUICK SETTINGS SHEET ====================
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItemView(
    app: AppInfo,
    settingsManager: SettingsManager,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            val bitmap = remember(app.icon) {
                app.icon?.toBitmap(128, 128)?.asImageBitmap()
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(settingsManager.iconSize.dp)
                        .clip(RoundedCornerShape(settingsManager.iconCornerRadius.dp))
                )
            }

            if (isEditMode) {
                IconSelectionBadge(
                    isSelected = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                )
            }
        }

        if (settingsManager.showLabels) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
