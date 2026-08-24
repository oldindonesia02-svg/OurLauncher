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
import androidx.compose.ui.graphics.graphicsLayer
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

    var gridApps by remember(apps) {
        val savedPackages = settingsManager.homeGridApps
        val initialList = if (savedPackages.isNotEmpty()) {
            val appMap = apps.associateBy { it.packageName }
            val loaded = savedPackages.mapNotNull { appMap[it] }.toMutableList()
            val remaining = apps.drop(4).filter { app -> !savedPackages.contains(app.packageName) }
            (loaded + remaining).toMutableList()
        } else {
            apps.drop(4).toMutableList()
        }
        mutableStateOf(initialList)
    }

    val pageSize = 20
    val totalPages = remember(gridApps) { maxOf(1, ceil(gridApps.size.toFloat() / pageSize).toInt()) }
    val pagerState = rememberPagerState(pageCount = { totalPages })

    val chunkedPages = remember(gridApps) { gridApps.chunked(pageSize) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val itemBoundsMap = remember { mutableStateMapOf<Int, Rect>() }

    var contextMenuApp by remember { mutableStateOf<AppInfo?>(null) }
    var contextMenuPosition by remember { mutableStateOf(Offset.Zero) }

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

    fun saveCurrentLayout(updatedList: List<AppInfo>) {
        settingsManager.homeGridApps = updatedList.map { it.packageName }
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

                // --- 120HZ ZERO-LAG HORIZONTAL PAGER ---
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { pageIndex ->
                    val pageApps = if (pageIndex < chunkedPages.size) chunkedPages[pageIndex] else emptyList()
                    val pageStart = pageIndex * pageSize

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 52.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
                            .graphicsLayer {
                                val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                                alpha = 1f - (abs(pageOffset) * 0.35f).coerceIn(0f, 1f)
                                scaleX = 1f - (abs(pageOffset) * 0.05f).coerceIn(0f, 1f)
                                scaleY = scaleX
                            }
                            .pointerInput(pageIndex) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { startPos ->
                                        val found = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                            rect.contains(startPos.x.toInt(), startPos.y.toInt())
                                        }
                                        if (found != null && found.key < gridApps.size) {
                                            draggedIndex = found.key
                                            dragOffset = startPos
                                            contextMenuApp = gridApps[found.key]
                                            contextMenuPosition = startPos
                                        }
                                    },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        dragOffset += amount
                                        contextMenuApp = null

                                        val hovered = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                            rect.contains(dragOffset.x.toInt(), dragOffset.y.toInt())
                                        }
                                        if (hovered != null && draggedIndex != null && hovered.key != draggedIndex && hovered.key < gridApps.size) {
                                            val from = draggedIndex!!
                                            val to = hovered.key
                                            val list = gridApps.toMutableList()
                                            Collections.swap(list, from, to)
                                            gridApps = list
                                            draggedIndex = to
                                            saveCurrentLayout(list)
                                        }
                                    },
                                    onDragEnd = { draggedIndex = null },
                                    onDragCancel = { draggedIndex = null }
                                )
                            },
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val rows = remember(pageApps) { pageApps.chunked(4) }
                        rows.forEachIndexed { rowIndex, rowApps ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                rowApps.forEachIndexed { colIndex, app ->
                                    val globalIndex = pageStart + (rowIndex * 4) + colIndex
                                    val isBeingDragged = draggedIndex == globalIndex

                                    Box(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .onGloballyPositioned { coords ->
                                                val b = coords.boundsInRoot()
                                                itemBoundsMap[globalIndex] = Rect(
                                                    b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt()
                                                )
                                            }
                                            .alpha(if (isBeingDragged) 0.05f else 1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppIcon(
                                            app = app,
                                            onClick = { contextMenuApp = null; handleAppOpen(app, null) },
                                            showLabel = settingsManager.showLabels,
                                            fontFamilyName = settingsManager.fontFamily,
                                            iconSizeDp = settingsManager.iconSize,
                                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                                            iconOpacity = settingsManager.iconOpacity,
                                            customDrawable = getCustomDrawable(app.packageName),
                                            onClickWithBounds = { bounds -> contextMenuApp = null; handleAppOpen(app, bounds) }
                                        )
                                    }
                                }
                                repeat(4 - rowApps.size) {
                                    Spacer(modifier = Modifier.size(settingsManager.iconSize.dp))
                                }
                            }
                        }
                        repeat(5 - rows.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // --- LIQUID FLOATING SEARCH & DOTS CAPSULE ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LiquidSearchDotsCapsule(
                        totalPages = totalPages,
                        currentPage = pagerState.currentPage,
                        onSearchClick = onOpenDrawer
                    )
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

        if (contextMenuApp != null && draggedIndex == null) {
            val app = contextMenuApp!!
            HomeContextMenu(
                app = app,
                position = contextMenuPosition,
                screenWidthPx = screenWidthPx,
                repository = repository,
                onDismiss = { contextMenuApp = null },
                onRemove = {
                    val updated = gridApps.filter { it.packageName != app.packageName }.toMutableList()
                    gridApps = updated
                    saveCurrentLayout(updated)
                }
            )
        }

        if (draggedIndex != null && draggedIndex!! < gridApps.size) {
            val app = gridApps[draggedIndex!!]
            val targetDrawable = getCustomDrawable(app.packageName) ?: app.icon
            val cacheKey = "${app.packageName}_${targetDrawable.hashCode()}"
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
