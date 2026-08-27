package com.ourlauncher.app.ui

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.AppRepository
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Collections
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.roundToInt

fun saveGridStructure(items: List<GridItem>, settingsManager: SettingsManager) {
    try {
        val jsonArray = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            when (item) {
                is GridItem.SingleApp -> {
                    obj.put("type", "app")
                    obj.put("package", item.app.packageName)
                }
                is GridItem.Folder -> {
                    obj.put("type", "folder")
                    obj.put("id", item.folder.id)
                    obj.put("name", item.folder.name)
                    val appsArray = JSONArray()
                    item.folder.apps.forEach { appsArray.put(it.packageName) }
                    obj.put("apps", appsArray)
                }
            }
            jsonArray.put(obj)
        }
        settingsManager.homeGridStructure = jsonArray.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadGridStructure(rawJson: String, apps: List<AppInfo>): List<GridItem> {
    val appMap = apps.associateBy { it.packageName }
    if (rawJson.isBlank()) {
        return apps.drop(4).map { GridItem.SingleApp(it) }
    }
    return try {
        val jsonArray = JSONArray(rawJson)
        val loadedItems = mutableListOf<GridItem>()
        val usedPackages = mutableSetOf<String>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val type = obj.optString("type")
            if (type == "app") {
                val pkg = obj.optString("package")
                appMap[pkg]?.let {
                    loadedItems.add(GridItem.SingleApp(it))
                    usedPackages.add(pkg)
                }
            } else if (type == "folder") {
                val id = obj.optString("id", UUID.randomUUID().toString())
                val name = obj.optString("name", "Folder")
                val appsArr = obj.optJSONArray("apps") ?: JSONArray()
                val folderApps = mutableListOf<AppInfo>()
                for (j in 0 until appsArr.length()) {
                    val pkg = appsArr.getString(j)
                    appMap[pkg]?.let {
                        folderApps.add(it)
                        usedPackages.add(pkg)
                    }
                }
                if (folderApps.isNotEmpty()) {
                    loadedItems.add(GridItem.Folder(FolderInfo(id, name, folderApps)))
                }
            }
        }

        val dockPackages = apps.take(4).map { it.packageName }.toSet()
        val remaining = apps.drop(4).filter { it.packageName !in usedPackages && it.packageName !in dockPackages }
        loadedItems.addAll(remaining.map { GridItem.SingleApp(it) })
        loadedItems
    } catch (e: Exception) {
        apps.drop(4).map { GridItem.SingleApp(it) }
    }
}

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
        mutableStateOf(loadGridStructure(settingsManager.homeGridStructure, apps))
    }

    val columns = settingsManager.gridColumns
    val rows = settingsManager.gridRows
    val pageSize = remember(columns, rows) { columns * rows }

    var extraPagesCount by remember { mutableStateOf(0) }
    val basePages = remember(gridItems, pageSize) { maxOf(1, ceil(gridItems.size.toFloat() / pageSize).toInt()) }
    val totalPages = basePages + extraPagesCount
    val pagerState = rememberPagerState(pageCount = { totalPages })

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var isOverviewMode by remember { mutableStateOf(false) }
    val overviewAnim = remember { Animatable(0f) }

    var showHomeSettingsSheet by remember { mutableStateOf(false) }
    var showIconCustomizeSheet by remember { mutableStateOf(false) }
    var showSearchBarPositionSheet by remember { mutableStateOf(false) }

    var liveSearchOffset by remember { mutableStateOf(settingsManager.searchOffset) }
    var liveHideCapsule by remember { mutableStateOf(settingsManager.hideSearchCapsule) }

    LaunchedEffect(isOverviewMode) {
        overviewAnim.animateTo(
            targetValue = if (isOverviewMode) 1f else 0f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 320f)
        )
    }

    BackHandler(enabled = isOverviewMode || showHomeSettingsSheet || showIconCustomizeSheet || showSearchBarPositionSheet) {
        when {
            showSearchBarPositionSheet -> showSearchBarPositionSheet = false
            showIconCustomizeSheet -> showIconCustomizeSheet = false
            showHomeSettingsSheet -> showHomeSettingsSheet = false
            isOverviewMode -> isOverviewMode = false
        }
    }

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

    val posEasing = remember(settingsManager.posCurveX1, settingsManager.posCurveY1, settingsManager.posCurveX2, settingsManager.posCurveY2) {
        CubicBezierEasing(
            settingsManager.posCurveX1.coerceIn(0f, 1f),
            settingsManager.posCurveY1.coerceIn(0f, 1.5f),
            settingsManager.posCurveX2.coerceIn(0f, 1f),
            settingsManager.posCurveY2.coerceIn(0f, 1.5f)
        )
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
        if (isOverviewMode || showHomeSettingsSheet || showIconCustomizeSheet || showSearchBarPositionSheet) return
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
        val ov = overviewAnim.value

        val bgScale = if (activeApp != null && settingsManager.animAdvancedTexture) 1f - (0.05f * p) else 1f
        val bgAlpha = if (activeApp != null && settingsManager.animAdvancedTexture) 1f - (0.35f * p) else 1f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(bgScale)
                .alpha(bgAlpha)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom: Float, _ ->
                        if (zoom < 0.88f && !isOverviewMode) {
                            isOverviewMode = true
                        }
                    }
                }
                .pointerInput(Unit) {
                    var totalVertical = 0f
                    var startX = 0f
                    detectVerticalDragGestures(
                        onDragStart = { offset: Offset ->
                            startX = offset.x
                            totalVertical = 0f
                        },
                        onVerticalDrag = { _: PointerInputChange, dragAmount: Float ->
                            totalVertical += dragAmount
                        },
                        onDragEnd = {
                            if (!isOverviewMode && !showHomeSettingsSheet && !showIconCustomizeSheet && !showSearchBarPositionSheet) {
                                if (totalVertical < -50f) {
                                    onOpenDrawer()
                                } else if (totalVertical > 50f) {
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
                        onTap = {
                            if (isOverviewMode) isOverviewMode = false
                            showHomeSettingsSheet = false
                            showIconCustomizeSheet = false
                            showSearchBarPositionSheet = false
                        },
                        onLongPress = {
                            if (!isOverviewMode) {
                                showHomeSettingsSheet = true
                            }
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = !isOverviewMode,
                    beyondBoundsPageCount = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = 1f - (0.28f * ov)
                            scaleY = 1f - (0.28f * ov)
                        }
                ) { pageIndex: Int ->
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
                                        .border(1.5.dp, Color(0xFF0A84FF).copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                                        .clickable { isOverviewMode = false }
                                } else Modifier
                            )
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            contentPadding = PaddingValues(top = 48.dp, start = 12.dp, end = 12.dp, bottom = 4.dp),
                            userScrollEnabled = false,
                            verticalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(pageIndex) {
                                    if (!isOverviewMode) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { startPos: Offset ->
                                                val found = itemBoundsMap.entries.firstOrNull { (_, rect: Rect) ->
                                                    rect.contains(startPos.x.toInt(), startPos.y.toInt())
                                                }
                                                if (found != null && found.key < gridItems.size) {
                                                    draggedIndex = found.key
                                                    dragOffset = startPos
                                                }
                                            },
                                            onDrag = { change: PointerInputChange, amount: Offset ->
                                                change.consume()
                                                dragOffset += amount

                                                val hovered = itemBoundsMap.entries.firstOrNull { (_, rect: Rect) ->
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
                                                            val updated = gridItems.filterIndexed { idx: Int, _ -> idx != from }.mapIndexed { idx: Int, item: GridItem ->
                                                                val adjustedTo = if (from < to) to - 1 else to
                                                                if (idx == adjustedTo) GridItem.Folder(newFolder) else item
                                                            }
                                                            sanitizeAndSaveFolders(updated)
                                                        } else if (targetItem is GridItem.Folder) {
                                                            targetItem.folder.apps.add(sourceItem.app)
                                                            val updated = gridItems.filterIndexed { idx: Int, _ -> idx != from }
                                                            sanitizeAndSaveFolders(updated)
                                                        }
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
                            itemsIndexed(currentGridItems, key = { _: Int, item: GridItem -> item.id }) { indexInPage: Int, item: GridItem ->
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
                                                onClick = { handleAppOpen(item.app, null) },
                                                showLabel = settingsManager.showLabels,
                                                fontFamilyName = settingsManager.fontFamily,
                                                iconSizeDp = settingsManager.iconSize,
                                                cornerRadiusPercent = settingsManager.iconCornerRadius,
                                                iconOpacity = settingsManager.iconOpacity,
                                                customDrawable = getCustomDrawable(item.app.packageName),
                                                onClickWithBounds = { bounds: Rect -> handleAppOpen(item.app, bounds) },
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

                if (!liveHideCapsule && !isOverviewMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(0, liveSearchOffset.roundToInt()) }
                            .padding(bottom = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LiquidSearchAiCapsule(
                            pagerState = pagerState,
                            totalPages = totalPages,
                            onSearchClick = onOpenDrawer,
                            onAiClick = { launchGeminiAi(context) }
                        )
                    }
                }

                LiquidGlassDock(
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    dockApps.forEach { app ->
                        AppIcon(
                            app = app,
                            onClick = { handleAppOpen(app, null) },
                            showLabel = false,
                            fontFamilyName = settingsManager.fontFamily,
                            iconSizeDp = settingsManager.iconSize,
                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                            iconOpacity = settingsManager.iconOpacity,
                            customDrawable = getCustomDrawable(app.packageName),
                            onClickWithBounds = { bounds: Rect -> handleAppOpen(app, bounds) },
                            modifier = Modifier.width(64.dp)
                        )
                    }
                }
            }
        }

        if (activeFolder != null) {
            FolderPopup(
                folder = activeFolder!!,
                settingsManager = settingsManager,
                getCustomDrawable = getCustomDrawable,
                onAppClick = { app: AppInfo -> handleAppOpen(app, null) },
                onAppClickWithBounds = { app: AppInfo, bounds: Rect -> handleAppOpen(app, bounds) },
                onRenameFolder = { newName: String ->
                    activeFolder?.name = newName
                    saveGridStructure(gridItems, settingsManager)
                },
                onStartDragOut = { appToExtract: AppInfo, initialPos: Offset ->
                    val folder = activeFolder
                    if (folder != null) {
                        folder.apps.remove(appToExtract)
                        val folderIndex = gridItems.indexOfFirst { it is GridItem.Folder && it.folder.id == folder.id }
                        targetHoverIndex = if (folderIndex >= 0) folderIndex + 1 else null
                        sanitizeAndSaveFolders(gridItems)
                    }
                    draggedExternalApp = appToExtract
                    dragOffset = initialPos
                    activeFolder = null
                },
                onDismiss = { activeFolder = null }
            )
        }

        if (showHomeSettingsSheet) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                HomeScreenSettingsSheet(
                    settingsManager = settingsManager,
                    onOpenTransitionEffects = {
                        showHomeSettingsSheet = false
                        showIconCustomizeSheet = true
                    },
                    onSetDefaultScreen = {
                        showHomeSettingsSheet = false
                        liveSearchOffset = settingsManager.searchOffset
                        liveHideCapsule = settingsManager.hideSearchCapsule
                        showSearchBarPositionSheet = true
                    },
                    onRegenerateIcons = {
                        showHomeSettingsSheet = false
                        clearIconCache()
                    },
                    onOpenMoreSettings = {
                        showHomeSettingsSheet = false
                        onOpenSettings()
                    },
                    onDismiss = { showHomeSettingsSheet = false }
                )
            }
        }

        if (showSearchBarPositionSheet) {
            TopLiquidSearchBarPositionCard(
                currentOffset = liveSearchOffset,
                isCapsuleHidden = liveHideCapsule,
                onOffsetChange = { newOffset: Float ->
                    liveSearchOffset = newOffset
                },
                onHideCapsuleChange = { newHidden: Boolean ->
                    liveHideCapsule = newHidden
                },
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

        val floatingApp = if (draggedIndex != null && draggedIndex!! < gridItems.size) {
            val item = gridItems[draggedIndex!!]
            if (item is GridItem.SingleApp) item.app else null
        } else draggedExternalApp

        if (floatingApp != null) {
            val targetDrawable = getCustomDrawable(floatingApp.packageName) ?: floatingApp.icon
            val cacheKey = "${floatingApp.packageName}_${targetDrawable?.hashCode() ?: 0}"
            val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

            with(density) {
                Box(
                    modifier = Modifier
                        .offset {
                            // BUG FIXED: Extra -30.dp offset remove kora hoyeche
                            IntOffset(
                                (dragOffset.x - (settingsManager.iconSize.dp.toPx() / 2)).roundToInt(),
                                (dragOffset.y - (settingsManager.iconSize.dp.toPx() / 2)).roundToInt()
                            )
                        }
                        .scale(1.15f)
                        // BUG FIXED: Notun touch listener add kora hoyeche app ta drop korar jonno
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffset += dragAmount
                                    val hovered = itemBoundsMap.entries.firstOrNull { (_, rect) ->
                                        rect.contains(dragOffset.x.toInt(), dragOffset.y.toInt())
                                    }
                                    targetHoverIndex = hovered?.key
                                },
                                onDragEnd = {
                                    if (targetHoverIndex != null) {
                                        val to = targetHoverIndex!!
                                        val updated = gridItems.toMutableList()
                                        updated.add(to, GridItem.SingleApp(floatingApp))
                                        sanitizeAndSaveFolders(updated)
                                    } else {
                                        val updated = gridItems.toMutableList()
                                        updated.add(GridItem.SingleApp(floatingApp))
                                        sanitizeAndSaveFolders(updated)
                                    }
                                    draggedExternalApp = null
                                    targetHoverIndex = null
                                    draggedIndex = null
                                }
                            )
                        },
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
        
