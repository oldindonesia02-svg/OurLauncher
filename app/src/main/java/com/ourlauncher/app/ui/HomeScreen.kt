package com.ourlauncher.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    settingsManager: SettingsManager,
    onOpenDrawer: () -> Unit,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClick: (AppInfo) -> Unit = {},
    onAppClickWithBounds: (AppInfo, Rect?) -> Unit = { _, _ -> },
    onOpenSettings: () -> Unit = {},
    clearIconCache: () -> Unit = {},
    resumeTrigger: Long = 0L
) {
    val context = LocalContext.current
    val dockApps = remember(apps) { apps.take(4) }

    val pageSize = (settingsManager.gridColumns * settingsManager.gridRows).coerceAtLeast(1)
    val totalPages = if (apps.isEmpty()) 1 else (apps.size + pageSize - 1) / pageSize
    val pagerState = rememberPagerState(pageCount = { totalPages })

    val coroutineScope = rememberCoroutineScope()

    // Sheet States
    var isOverviewMode by remember { mutableStateOf(false) }
    val overviewAnim = remember { Animatable(0f) }

    var showHomeSettingsSheet by remember { mutableStateOf(false) }
    var showIconCustomizeSheet by remember { mutableStateOf(false) }
    var showSearchBarPositionSheet by remember { mutableStateOf(false) }
    var showGlassPlayground by remember { mutableStateOf(false) }
    var showLiquidBottomBar by remember { mutableStateOf(false) }

    var liveSearchOffset by remember { mutableStateOf(settingsManager.searchOffset) }
    var liveHideCapsule by remember { mutableStateOf(settingsManager.hideSearchCapsule) }

    val isAnySheetOpen = showHomeSettingsSheet || showIconCustomizeSheet ||
            showSearchBarPositionSheet || showGlassPlayground ||
            showLiquidBottomBar || isOverviewMode

    LaunchedEffect(isOverviewMode) {
        overviewAnim.animateTo(
            targetValue = if (isOverviewMode) 1f else 0f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
        )
    }

    BackHandler(enabled = isAnySheetOpen) {
        when {
            showLiquidBottomBar -> showLiquidBottomBar = false
            showGlassPlayground -> showGlassPlayground = false
            showSearchBarPositionSheet -> showSearchBarPositionSheet = false
            showIconCustomizeSheet -> showIconCustomizeSheet = false
            showHomeSettingsSheet -> showHomeSettingsSheet = false
            isOverviewMode -> isOverviewMode = false
        }
    }

    val ov = overviewAnim.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isAnySheetOpen) {
                if (!isAnySheetOpen) {
                    var totalVertical = 0f
                    detectVerticalDragGestures(
                        onDragStart = { totalVertical = 0f },
                        onVerticalDrag = { _, dragAmount -> totalVertical += dragAmount },
                        onDragEnd = {
                            if (totalVertical < -50f) {
                                onOpenDrawer()
                            }
                        }
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (showLiquidBottomBar) 0.95f else 1f
                    scaleY = if (showLiquidBottomBar) 0.95f else 1f
                }
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = !isAnySheetOpen,
                beyondBoundsPageCount = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        scaleX = 1f - (0.28f * ov)
                        scaleY = 1f - (0.28f * ov)
                    }
            ) { pageIndex ->
                val pageStart = pageIndex * pageSize
                val pageEnd = minOf(pageStart + pageSize, apps.size)
                val currentGridItems = if (pageStart < apps.size) apps.subList(pageStart, pageEnd) else emptyList()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isOverviewMode) {
                                Modifier
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                                    .clickable { isOverviewMode = false }
                            } else Modifier
                        )
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(settingsManager.gridColumns),
                        contentPadding = PaddingValues(top = 40.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
                        userScrollEnabled = false,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(pageIndex, isAnySheetOpen) {
                                if (!isAnySheetOpen) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            showLiquidBottomBar = true
                                        },
                                        onDrag = { change: PointerInputChange, _: Offset ->
                                            change.consume()
                                        }
                                    )
                                }
                            }
                    ) {
                        itemsIndexed(currentGridItems, key = { _, item -> item.packageName }) { _, app ->
                            AppIcon(
                                app = app,
                                onClick = { onAppClick(app) },
                                showLabel = settingsManager.showLabels,
                                fontFamilyName = settingsManager.fontFamily,
                                iconSizeDp = settingsManager.iconSize,
                                cornerRadiusPercent = settingsManager.iconCornerRadius,
                                iconOpacity = settingsManager.iconOpacity,
                                customDrawable = getCustomDrawable(app.packageName),
                                onClickWithBounds = { bounds -> onAppClickWithBounds(app, bounds) },
                                modifier = Modifier.width(82.dp)
                            )
                        }
                    }
                }
            }

            // Search Capsule
            AnimatedVisibility(
                visible = !liveHideCapsule && !isOverviewMode && !showLiquidBottomBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, liveSearchOffset.toInt()) }
                        .padding(bottom = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LiquidSearchAiCapsule(
                        pagerState = pagerState,
                        totalPages = totalPages,
                        onSearchClick = onOpenDrawer,
                        onAiClick = {}
                    )
                }
            }

            // Bottom Dock
            AnimatedVisibility(
                visible = !showLiquidBottomBar,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                LiquidGlassDock(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    dockApps.forEach { app ->
                        AppIcon(
                            app = app,
                            onClick = { onAppClick(app) },
                            showLabel = false,
                            fontFamilyName = settingsManager.fontFamily,
                            iconSizeDp = settingsManager.iconSize,
                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                            iconOpacity = settingsManager.iconOpacity,
                            customDrawable = getCustomDrawable(app.packageName),
                            onClickWithBounds = { bounds -> onAppClickWithBounds(app, bounds) },
                            modifier = Modifier.width(64.dp)
                        )
                    }
                }
            }
        }

                // 4-Tab Liquid Glass Bottom Bar (Motorola Style + Live Finger Drag Interaction)
        AnimatedVisibility(
            visible = showLiquidBottomBar,
            enter = fadeIn(tween(200)) + slideInVertically(
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 320f)
            ) { it },
            exit = fadeOut(tween(180)) + slideOutVertically(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f)
            ) { it }
        ) {
            HomeLiquidBottomBar(
                onOpenWidgets = {
                    showLiquidBottomBar = false
                },
                onOpenWallpapers = {
                    showLiquidBottomBar = false
                    try {
                        val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                        context.startActivity(Intent.createChooser(intent, "Choose Wallpaper"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onOpenHomeSettings = {
                    showLiquidBottomBar = false
                    coroutineScope.launch {
                        delay(120)
                        showHomeSettingsSheet = true
                    }
                },
                onOpenGeneralSettings = {
                    showLiquidBottomBar = false
                    coroutineScope.launch {
                        delay(120)
                        showGlassPlayground = true
                    }
                },
                onDismiss = { showLiquidBottomBar = false }
            )
        }

        // Home Settings Sheet
        if (showHomeSettingsSheet) {
            HomeScreenSettingsSheet(
                settingsManager = settingsManager,
                onOpenTransitionEffects = {
                    showHomeSettingsSheet = false
                    coroutineScope.launch {
                        delay(150)
                        showIconCustomizeSheet = true
                    }
                },
                onSetDefaultScreen = {
                    showHomeSettingsSheet = false
                    liveSearchOffset = settingsManager.searchOffset
                    liveHideCapsule = settingsManager.hideSearchCapsule
                    coroutineScope.launch {
                        delay(150)
                        showSearchBarPositionSheet = true
                    }
                },
                onRegenerateIcons = {
                    showHomeSettingsSheet = false
                    clearIconCache()
                },
                onOpenMoreSettings = {
                    showHomeSettingsSheet = false
                    coroutineScope.launch {
                        delay(150)
                        showGlassPlayground = true
                    }
                },
                onDismiss = { showHomeSettingsSheet = false }
            )
        }

        // Glass Playground
        if (showGlassPlayground) {
            GlassPlaygroundSheet(
                settingsManager = settingsManager,
                onDismiss = { showGlassPlayground = false }
            )
        }

        // Search Bar Position Sheet
        if (showSearchBarPositionSheet) {
            TopLiquidSearchBarPositionCard(
                currentOffset = liveSearchOffset,
                isCapsuleHidden = liveHideCapsule,
                onOffsetChange = { newOffset: Float -> liveSearchOffset = newOffset },
                onHideCapsuleChange = { newHidden: Boolean -> liveHideCapsule = newHidden },
                onOpenDockPosition = {
                    showSearchBarPositionSheet = false
                    onOpenSettings()
                },
                onApply = {
                    settingsManager.searchOffset = liveSearchOffset
                    settingsManager.hideSearchCapsule = liveHideCapsule
                },
                onDismiss = {
                    liveSearchOffset = settingsManager.searchOffset
                    liveHideCapsule = settingsManager.hideSearchCapsule
                    showSearchBarPositionSheet = false
                }
            )
        }

        // Icon Customize Sheet
        if (showIconCustomizeSheet) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                IconCustomizeSheet(
                    settingsManager = settingsManager,
                    onApply = { showIconCustomizeSheet = false },
                    onDismiss = { showIconCustomizeSheet = false }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Interactive Liquid Glass Bottom Bar (Finger Tracking & Physics)
// -------------------------------------------------------------
data class HomeActionItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun HomeLiquidBottomBar(
    onOpenWidgets: () -> Unit,
    onOpenWallpapers: () -> Unit,
    onOpenHomeSettings: () -> Unit,
    onOpenGeneralSettings: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onDismiss() }

    val actions = remember {
        listOf(
            HomeActionItem("Widgets", Icons.Rounded.Widgets),
            HomeActionItem("Wallpapers", Icons.Rounded.Image),
            HomeActionItem("Personalize", Icons.Rounded.Home),
            HomeActionItem("Home Settings", Icons.Rounded.Settings)
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val dragOffsetPx = remember { Animatable(0f) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }

    fun triggerAction(index: Int) {
        when (index) {
            0 -> onOpenWidgets()
            1 -> onOpenWallpapers()
            2 -> onOpenHomeSettings()
            3 -> onOpenGeneralSettings()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.42f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        BoxWithConstraints(
            modifier = modifier
                .padding(horizontal = 14.dp, vertical = 24.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color(0xFF00E5FF).copy(alpha = 0.35f),
                    ambientColor = Color.Black.copy(alpha = 0.6f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF132330).copy(alpha = 0.94f),
                            Color(0xFF09121A).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.4.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.75f),
                            Color(0xFF00E5FF).copy(alpha = 0.40f),
                            Color.White.copy(alpha = 0.12f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val tabCount = actions.size
            val tabWidthPx = totalWidthPx / tabCount
            val tabWidthDp = maxWidth / tabCount

            // Synchronize position on initial load
            LaunchedEffect(tabWidthPx) {
                if (!isDragging) {
                    dragOffsetPx.snapTo(selectedIndex * tabWidthPx)
                }
            }

            // Liquid Interactive Gesture Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(tabWidthPx) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newOffset = (dragOffsetPx.value + dragAmount)
                                    .coerceIn(0f, (tabCount - 1) * tabWidthPx)
                                coroutineScope.launch {
                                    dragOffsetPx.snapTo(newOffset)
                                }
                            },
                            onDragEnd = {
                                isDragging = false
                                val targetIndex = (dragOffsetPx.value / tabWidthPx).roundToInt()
                                    .coerceIn(0, tabCount - 1)
                                selectedIndex = targetIndex
                                coroutineScope.launch {
                                    dragOffsetPx.animateTo(
                                        targetValue = targetIndex * tabWidthPx,
                                        animationSpec = spring(dampingRatio = 0.68f, stiffness = 380f)
                                    )
                                    delay(100)
                                    triggerAction(targetIndex)
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                coroutineScope.launch {
                                    dragOffsetPx.animateTo(
                                        targetValue = selectedIndex * tabWidthPx,
                                        animationSpec = spring(dampingRatio = 0.72f, stiffness = 400f)
                                    )
                                }
                            }
                        )
                    }
            ) {
                val density = LocalDensity.current
                val currentOffsetDp = with(density) { dragOffsetPx.value.toDp() }

                // Live Liquid Sliding Bubble with Elastic Glass Effect
                Box(
                    modifier = Modifier
                        .offset(x = currentOffsetDp)
                        .width(tabWidthDp)
                        .fillMaxHeight()
                        .graphicsLayer {
                            // Dragging squish/stretch physics
                            scaleX = if (isDragging) 1.08f else 1f
                            scaleY = if (isDragging) 0.94f else 1f
                        }
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF00B4D8).copy(alpha = 0.40f),
                                    Color(0xFF0077B6).copy(alpha = 0.60f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    Color(0xFF00E5FF).copy(alpha = 0.35f)
                                )
                            ),
                            RoundedCornerShape(22.dp)
                        )
                )

                // 4 Tab Items (Icons + Labels)
                Row(modifier = Modifier.fillMaxSize()) {
                    actions.forEachIndexed { index, item ->
                        val isCurrentTab = (dragOffsetPx.value / tabWidthPx).roundToInt() == index
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedIndex = index
                                    coroutineScope.launch {
                                        dragOffsetPx.animateTo(
                                            targetValue = index * tabWidthPx,
                                            animationSpec = spring(dampingRatio = 0.68f, stiffness = 380f)
                                        )
                                        delay(100)
                                        triggerAction(index)
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isCurrentTab) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(if (isCurrentTab) 1.1f else 1f)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = item.title,
                                color = if (isCurrentTab) Color.White else Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp,
                                fontWeight = if (isCurrentTab) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
