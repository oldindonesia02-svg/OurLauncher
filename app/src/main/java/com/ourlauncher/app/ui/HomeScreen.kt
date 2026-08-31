package com.ourlauncher.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.FolderInfo
import com.ourlauncher.app.GridItem
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.UUID
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    settingsManager: SettingsManager,
    onOpenDrawer: () -> Unit,
    dockApps: List<AppInfo> = emptyList(),
    gridItemsState: List<GridItem> = emptyList(),
    saveGridStructure: (List<GridItem>, SettingsManager) -> Unit = { _, _ -> },
    handleAppOpen: (AppInfo, Rect?) -> Unit = { app, _ -> },
    getCustomDrawable: (String) -> Drawable? = { null },
    onOpenSettings: () -> Unit = {},
    clearIconCache: () -> Unit = {},
    resumeTrigger: Long = 0L
) {
    val context = LocalContext.current
    val effectiveDockApps = remember(dockApps, apps) {
        if (dockApps.isNotEmpty()) dockApps else apps.take(4)
    }

    var gridItems by remember(gridItemsState, apps) {
        mutableStateOf(
            if (gridItemsState.isNotEmpty()) gridItemsState
            else apps.map { GridItem.SingleApp(it) }
        )
    }

    val pageSize = (settingsManager.gridColumns * settingsManager.gridRows).coerceAtLeast(1)
    val basePages = if (gridItems.isEmpty()) 1 else (gridItems.size + pageSize - 1) / pageSize
    val extraPagesCount = settingsManager.extraPages
    val totalPages = (basePages + extraPagesCount).coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { totalPages })

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

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

    // Drag States
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var draggedExternalApp by remember { mutableStateOf<AppInfo?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var targetHoverIndex by remember { mutableStateOf<Int?>(null) }
    val itemBoundsMap = remember { mutableStateMapOf<Int, Rect>() }

    var activeFolder by remember { mutableStateOf<FolderInfo?>(null) }
    var activeApp by remember { mutableStateOf<AppInfo?>(null) }
    var activeBounds by remember { mutableStateOf<Rect?>(null) }
    val animProgress = remember { Animatable(0f) }
    var animJob by remember { mutableStateOf<Job?>(null) }

    fun defaultOpenApp(app: AppInfo, bounds: Rect?) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sanitizeAndSaveFolders(items: List<GridItem>) {
        val sanitized = items.mapNotNull { item ->
            when (item) {
                is GridItem.SingleApp -> item
                is GridItem.Folder -> {
                    when (item.folder.apps.size) {
                        0 -> null
                        1 -> GridItem.SingleApp(item.folder.apps.first())
                        else -> item
                    }
                }
            }
        }
        gridItems = sanitized
        saveGridStructure(sanitized, settingsManager)
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
        Column(modifier = Modifier.fillMaxSize()) {
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
                val pageEnd = minOf(pageStart + pageSize, gridItems.size)
                val currentGridItems = if (pageStart < gridItems.size) gridItems.subList(pageStart, pageEnd) else emptyList()

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
                                        onDragStart = { startPos ->
                                            val found = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                                rect.contains(startPos.x.toInt(), startPos.y.toInt())
                                            }
                                            if (found != null && found.key < gridItems.size) {
                                                draggedIndex = found.key
                                                dragOffset = startPos
                                            } else {
                                                showLiquidBottomBar = true
                                            }
                                        },
                                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                                            change.consume()
                                            dragOffset += dragAmount

                                            val hovered = itemBoundsMap.entries.firstOrNull { (idx, rect) ->
                                                rect.contains(dragOffset.x.toInt(), dragOffset.y.toInt()) && idx != draggedIndex
                                            }
                                            targetHoverIndex = hovered?.key
                                        },
                                        onDragEnd = {
                                            if (draggedIndex != null && targetHoverIndex != null) {
                                                val from = draggedIndex!!
                                                val to = targetHoverIndex!!
                                                val sourceItem = gridItems[from]
                                                val targetItem = gridItems[to]

                                                if (sourceItem is GridItem.SingleApp && targetItem is GridItem.SingleApp) {
                                                    val newFolder = FolderInfo(
                                                        id = UUID.randomUUID().toString(),
                                                        name = "Folder",
                                                        apps = mutableListOf(targetItem.app, sourceItem.app)
                                                    )
                                                    val updated = gridItems.filterIndexed { index, _ -> index != from }.toMutableList()
                                                    val adjustedTo = if (from < to) to - 1 else to
                                                    updated[adjustedTo] = GridItem.Folder(newFolder)
                                                    sanitizeAndSaveFolders(updated)
                                                } else if (sourceItem is GridItem.SingleApp && targetItem is GridItem.Folder) {
                                                    targetItem.folder.apps.add(sourceItem.app)
                                                    val updated = gridItems.filterIndexed { index, _ -> index != from }
                                                    sanitizeAndSaveFolders(updated)
                                                } else {
                                                    val updated = gridItems.toMutableList()
                                                    Collections.swap(updated, from, to)
                                                    sanitizeAndSaveFolders(updated)
                                                }
                                            }
                                            draggedIndex = null
                                            targetHoverIndex = null
                                        },
                                        onDragCancel = {
                                            draggedIndex = null
                                            targetHoverIndex = null
                                        }
                                    )
                                }
                            }
                    ) {
                        itemsIndexed(currentGridItems, key = { _, item ->
                            when (item) {
                                is GridItem.SingleApp -> "app_${item.app.packageName}"
                                is GridItem.Folder -> "folder_${item.folder.id}"
                            }
                        }) { indexInPage, item ->
                            val globalIndex = pageStart + indexInPage
                            val isBeingDragged = draggedIndex == globalIndex
                            val isHovered = targetHoverIndex == globalIndex

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .onGloballyPositioned { coords ->
                                        val b = coords.boundsInRoot()
                                        itemBoundsMap[globalIndex] = Rect(
                                            b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt()
                                        )
                                    }
                                    .scale(if (isHovered) 1.08f else 1f)
                                    .alpha(if (isBeingDragged) 0.05f else 1f),
                                contentAlignment = Alignment.Center
                            ) {
                                when (item) {
                                    is GridItem.SingleApp -> {
                                        AppIcon(
                                            app = item.app,
                                            onClick = {
                                                if (handleAppOpen != { _: AppInfo, _: Rect? -> }) {
                                                    handleAppOpen(item.app, itemBoundsMap[globalIndex])
                                                } else {
                                                    defaultOpenApp(item.app, itemBoundsMap[globalIndex])
                                                }
                                            },
                                            showLabel = settingsManager.showLabels,
                                            fontFamilyName = settingsManager.fontFamily,
                                            iconSizeDp = settingsManager.iconSize,
                                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                                            iconOpacity = settingsManager.iconOpacity,
                                            customDrawable = getCustomDrawable(item.app.packageName),
                                            onClickWithBounds = { bounds ->
                                                if (handleAppOpen != { _: AppInfo, _: Rect? -> }) {
                                                    handleAppOpen(item.app, bounds)
                                                } else {
                                                    defaultOpenApp(item.app, bounds)
                                                }
                                            },
                                            modifier = Modifier.width(82.dp)
                                        )
                                    }
                                    is GridItem.Folder -> {
                                        FolderIcon(
                                            folder = item.folder,
                                            onClick = { activeFolder = item.folder },
                                            settingsManager = settingsManager,
                                            getCustomDrawable = getCustomDrawable,
                                            modifier = Modifier.width(82.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Search Capsule
            if (!liveHideCapsule && !isOverviewMode) {
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

            // Dock
            LiquidGlassDock(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                effectiveDockApps.forEach { app ->
                    AppIcon(
                        app = app,
                        onClick = { defaultOpenApp(app, null) },
                        showLabel = false,
                        fontFamilyName = settingsManager.fontFamily,
                        iconSizeDp = settingsManager.iconSize,
                        cornerRadiusPercent = settingsManager.iconCornerRadius,
                        iconOpacity = settingsManager.iconOpacity,
                        customDrawable = getCustomDrawable(app.packageName),
                        onClickWithBounds = { bounds -> defaultOpenApp(app, bounds) },
                        modifier = Modifier.width(64.dp)
                    )
                }
            }
        }

        // Drag Overlay
        val floatingApp = if (draggedIndex != null) {
            val item = gridItems.getOrNull(draggedIndex!!)
            if (item is GridItem.SingleApp) item.app else null
        } else draggedExternalApp

        if (floatingApp != null) {
            with(density) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (dragOffset.x - 40.dp.toPx()).toInt(),
                                (dragOffset.y - 40.dp.toPx()).toInt()
                            )
                        }
                        .size(80.dp)
                        .scale(1.15f)
                ) {
                    AppIcon(
                        app = floatingApp,
                        onClick = {},
                        showLabel = false,
                        iconSizeDp = settingsManager.iconSize,
                        cornerRadiusPercent = settingsManager.iconCornerRadius,
                        iconOpacity = 1f,
                        customDrawable = getCustomDrawable(floatingApp.packageName)
                    )
                }
            }
        }

        // Folder Popup
        if (activeFolder != null) {
            FolderPopup(
                folder = activeFolder!!,
                settingsManager = settingsManager,
                getCustomDrawable = getCustomDrawable,
                onAppClick = { app -> defaultOpenApp(app, null) },
                onAppClickWithBounds = { app, bounds -> defaultOpenApp(app, bounds) },
                onRenameFolder = { newName ->
                    activeFolder?.name = newName
                    saveGridStructure(gridItems, settingsManager)
                },
                onStartDragOut = { appToExtract ->
                    val folder = activeFolder
                    if (folder != null) {
                        folder.apps.remove(appToExtract)
                        val folderIndex = gridItems.indexOfFirst { it is GridItem.Folder && it.folder.id == folder.id }
                        targetHoverIndex = if (folderIndex >= 0) folderIndex else null
                        sanitizeAndSaveFolders(gridItems)
                    }
                    draggedExternalApp = appToExtract
                    dragOffset = Offset.Zero
                    activeFolder = null
                },
                onDismiss = { activeFolder = null }
            )
        }

        // 4-Tab Liquid Glass Bottom Bar
        if (showLiquidBottomBar) {
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
