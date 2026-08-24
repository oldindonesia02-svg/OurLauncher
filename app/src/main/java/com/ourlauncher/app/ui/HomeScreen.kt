package com.ourlauncher.app.ui

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.AppRepository
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.UUID
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    settingsManager: SettingsManager,
    resumeTrigger: Long = 0L,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, Rect) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val dockApps = remember(apps) { apps.take(4) }

    var gridItems by remember(apps) {
        val initialList: List<GridItem> = apps.drop(4).map { GridItem.SingleApp(it) }
        mutableStateOf(initialList)
    }

    val pageSize = 20
    val totalPages = remember(gridItems) { maxOf(1, ceil(gridItems.size.toFloat() / pageSize).toInt()) }
    val pagerState = rememberPagerState(pageCount = { totalPages })

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var targetHoverIndex by remember { mutableStateOf<Int?>(null) }
    val itemBoundsMap = remember { mutableStateMapOf<Int, Rect>() }

    var contextMenuApp by remember { mutableStateOf<AppInfo?>(null) }
    var contextMenuPosition by remember { mutableStateOf(Offset.Zero) }
    var activeFolder by remember { mutableStateOf<FolderInfo?>(null) }

    var activeApp by remember { mutableStateOf<AppInfo?>(null) }
    var activeBounds by remember { mutableStateOf<Rect?>(null) }
    val animProgress = remember { Animatable(0f) }
    var animJob by remember { mutableStateOf<Job?>(null) }

    val posEasing = remember(settingsManager.posCurveX1, settingsManager.posCurveY1, settingsManager.posCurveX2, settingsManager.posCurveY2) {
        CubicBezierEasing(
            settingsManager.posCurveX1.coerceIn(0f, 1f),
            settingsManager.posCurveY1.coerceIn(0f, 1.5f),
            settingsManager.posCurveX2.coerceIn(0f, 1f),
            settingsManager.posCurveY2.coerceIn(0f, 1.5f)
        )
    }

    LaunchedEffect(resumeTrigger) {
        if (resumeTrigger > 0L && activeApp != null && activeBounds != null) {
            animJob?.cancel()
            animJob = coroutineScope.launch {
                animProgress.snapTo(1f)
                animProgress.animateTo(0f, tween(durationMillis = 260, easing = posEasing))
                activeApp = null
                activeBounds = null
            }
        }
    }

    fun handleAppOpen(app: AppInfo, bounds: Rect?) {
        if (!settingsManager.animEnabled || bounds == null) {
            onAppClick(app)
            return
        }
        animJob?.cancel()
        activeApp = app
        activeBounds = bounds
        animJob = coroutineScope.launch {
            animProgress.snapTo(0f)
            launch {
                animProgress.animateTo(1f, tween(settingsManager.animDuration.toInt(), easing = posEasing))
            }
            delay((settingsManager.animDuration * 0.72f).toLong())
            onAppClickWithBounds(app, bounds)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val p = animProgress.value

        val bgScale = if (activeApp != null && settingsManager.animAdvancedTexture) 1f - (0.05f * p) else 1f
        val bgAlpha = if (activeApp != null && settingsManager.animAdvancedTexture) 1f - (0.35f * p) else 1f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(bgScale)
                .alpha(bgAlpha)
                .pointerInput(Unit) {
                    var startX = 0f
                    var totalDragY = 0f
                    var totalDragX = 0f
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            contextMenuApp = null
                            startX = startOffset.x
                            totalDragY = 0f
                            totalDragX = 0f
                        },
                        onDrag = { _, dragAmount ->
                            totalDragY += dragAmount.y
                            totalDragX += dragAmount.x
                        },
                        onDragEnd = {
                            val threshold = 45f
                            if (abs(totalDragY) > abs(totalDragX) * 1.5f) {
                                if (totalDragY < -threshold) {
                                    onOpenDrawer()
                                } else if (totalDragY > threshold) {
                                    if (startX < screenWidthPx / 2f) {
                                        triggerPullDownAction(settingsManager.leftPullDownAction, context, onOpenSettings)
                                    } else {
                                        triggerPullDownAction(settingsManager.rightPullDownAction, context, onOpenSettings)
                                    }
                                }
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { contextMenuApp = null },
                        onLongPress = { onOpenSettings() }
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) { pageIndex ->
                    val pageStart = pageIndex * pageSize
                    val pageEnd = minOf(pageStart + pageSize, gridItems.size)
                    val currentGridItems = if (pageStart < gridItems.size) gridItems.subList(pageStart, pageEnd) else emptyList()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(top = 48.dp, start = 12.dp, end = 12.dp, bottom = 4.dp),
                        userScrollEnabled = false,
                        verticalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(pageIndex) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { startPos ->
                                        val found = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                            rect.contains(startPos.x.toInt(), startPos.y.toInt())
                                        }
                                        if (found != null && found.key < gridItems.size) {
                                            draggedIndex = found.key
                                            dragOffset = startPos
                                            val item = gridItems[found.key]
                                            if (item is GridItem.SingleApp) {
                                                contextMenuApp = item.app
                                                contextMenuPosition = startPos
                                            }
                                        }
                                    },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        dragOffset += amount
                                        contextMenuApp = null

                                        val hovered = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                            rect.contains(dragOffset.x.toInt(), dragOffset.y.toInt())
                                        }
                                        targetHoverIndex = if (hovered != null && hovered.key != draggedIndex) hovered.key else null
                                    },
                                    onDragEnd = {
                                        if (draggedIndex != null && targetHoverIndex != null) {
                                            val from = draggedIndex!!
                                            val to = targetHoverIndex!!
                                            val sourceItem = gridItems[from]
                                            val targetItem = gridItems[to]

                                            if (sourceItem is GridItem.SingleApp) {
                                                if (targetItem is GridItem.SingleApp) {
                                                    val newFolder = FolderInfo(
                                                        id = UUID.randomUUID().toString(),
                                                        name = "Folder",
                                                        apps = mutableListOf(targetItem.app, sourceItem.app)
                                                    )
                                                    val updated = gridItems.filterIndexed { idx, _ -> idx != from }.mapIndexed { idx, item ->
                                                        val adjustedTo = if (from < to) to - 1 else to
                                                        if (idx == adjustedTo) GridItem.Folder(newFolder) else item
                                                    }
                                                    gridItems = updated
                                                } else if (targetItem is GridItem.Folder) {
                                                    targetItem.folder.apps.add(sourceItem.app)
                                                    val updated = gridItems.filterIndexed { idx, _ -> idx != from }
                                                    gridItems = updated
                                                }
                                            } else {
                                                val updated = gridItems.toMutableList()
                                                Collections.swap(updated, from, to)
                                                gridItems = updated
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
                    ) {
                        itemsIndexed(currentGridItems, key = { _, item -> item.id }) { indexInPage, item ->
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
                                            onClick = { contextMenuApp = null; handleAppOpen(item.app, null) },
                                            showLabel = settingsManager.showLabels,
                                            fontFamilyName = settingsManager.fontFamily,
                                            iconSizeDp = settingsManager.iconSize,
                                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                                            iconOpacity = settingsManager.iconOpacity,
                                            customDrawable = getCustomDrawable(item.app.packageName),
                                            onClickWithBounds = { bounds: Rect -> contextMenuApp = null; handleAppOpen(item.app, bounds) },
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

                if (!settingsManager.hideSearchCapsule) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(0, settingsManager.searchOffset.roundToInt()) }
                            .padding(bottom = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LiquidSearchDotsCapsule(
                            totalPages = totalPages,
                            currentPage = pagerState.currentPage,
                            onSearchClick = onOpenDrawer
                        )
                    }
                }

                Dock(
                    pinnedApps = dockApps,
                    settingsManager = settingsManager,
                    getCustomDrawable = getCustomDrawable,
                    onAppClick = { contextMenuApp = null; handleAppOpen(it, null) },
                    onAppClickWithBounds = { app, bounds -> contextMenuApp = null; handleAppOpen(app, bounds) }
                )
            }
        }

        // Active Folder Liquid Popup
        if (activeFolder != null) {
            FolderPopup(
                folder = activeFolder!!,
                settingsManager = settingsManager,
                getCustomDrawable = getCustomDrawable,
                onAppClick = { app -> handleAppOpen(app, null) },
                onAppClickWithBounds = { app, bounds -> handleAppOpen(app, bounds) },
                onRenameFolder = { newName -> activeFolder?.name = newName },
                onDismiss = { activeFolder = null }
            )
        }

        if (contextMenuApp != null && draggedIndex == null) {
            val app = contextMenuApp!!
            HomeContextMenu(
                app = app,
                position = contextMenuPosition,
                screenWidthPx = screenWidthPx,
                repository = repository,
                onDismiss = { contextMenuApp = null },
                onRemove = {
                    val updated = gridItems.filterNot { it is GridItem.SingleApp && it.app.packageName == app.packageName }
                    gridItems = updated
                }
            )
        }

        if (draggedIndex != null && draggedIndex!! < gridItems.size) {
            val item = gridItems[draggedIndex!!]
            if (item is GridItem.SingleApp) {
                val app = item.app
                val targetDrawable = getCustomDrawable(app.packageName) ?: app.icon
                val cacheKey = "${app.packageName}_${targetDrawable?.hashCode() ?: 0}"
                val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

                with(density) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (dragOffset.x - (settingsManager.iconSize.dp.toPx() / 2)).roundToInt(),
                                    (dragOffset.y - (settingsManager.iconSize.dp.toPx() / 2) - 30.dp.toPx()).roundToInt()
                                )
                            }
                            .scale(1.15f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(settingsManager.iconSize.dp)
                                    .clip(RoundedCornerShape(settingsManager.iconCornerRadius.toInt()))
                            )
                        }
                    }
                }
            }
        }

        if (activeApp != null && activeBounds != null && p > 0.005f) {
            AppLaunchOverlay(
                activeApp = activeApp!!,
                activeBounds = activeBounds!!,
                progress = p,
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx,
                settingsManager = settingsManager,
                getCustomDrawable = getCustomDrawable
            )
        }
    }
} 
