package com.ourlauncher.app.ui

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.AppRepository
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.math.abs
import kotlin.math.roundToInt

fun triggerPullDownAction(action: String, context: Context, onOpenSettings: () -> Unit) {
    when (action) {
        "notifications" -> {
            try {
                val service = context.getSystemService("statusbar")
                val clz = Class.forName("android.app.StatusBarManager")
                clz.getMethod("expandNotificationsPanel").invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "system_control_center" -> {
            try {
                val service = context.getSystemService("statusbar")
                val clz = Class.forName("android.app.StatusBarManager")
                clz.getMethod("expandSettingsPanel").invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "builtin_control_center" -> {
            onOpenSettings()
        }
    }
}

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
            savedPackages.mapNotNull { appMap[it] }.toMutableList()
        } else {
            apps.drop(4).take(20).toMutableList()
        }
        mutableStateOf(initialList)
    }

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
                animProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 260, easing = posEasing)
                )
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
                animProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = settingsManager.animDuration.toInt(), easing = posEasing)
                )
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
                            if (abs(totalDragY) > abs(totalDragX)) {
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(top = 56.dp, start = 12.dp, end = 12.dp),
                    userScrollEnabled = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .pointerInput(Unit) {
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
                        }
                ) {
                    itemsIndexed(gridApps, key = { _, app -> app.packageName }) { index, app ->
                        val isBeingDragged = draggedIndex == index
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(vertical = 4.dp)
                                .onGloballyPositioned { coords ->
                                    val b = coords.boundsInRoot()
                                    itemBoundsMap[index] = Rect(
                                        b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt()
                                    )
                                }
                                .alpha(if (isBeingDragged) 0.05f else 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                app = app,
                                onClick = {
                                    contextMenuApp = null
                                    handleAppOpen(app, null)
                                },
                                showLabel = settingsManager.showLabels,
                                fontFamilyName = settingsManager.fontFamily,
                                iconSizeDp = settingsManager.iconSize,
                                cornerRadiusPercent = settingsManager.iconCornerRadius,
                                iconOpacity = settingsManager.iconOpacity,
                                customDrawable = getCustomDrawable(app.packageName),
                                onClickWithBounds = { bounds ->
                                    contextMenuApp = null
                                    handleAppOpen(app, bounds)
                                }
                            )
                        }
                    }
                }

                SearchPill(onClick = onOpenDrawer, modifier = Modifier.padding(bottom = 8.dp))

                Dock(
                    pinnedApps = dockApps,
                    settingsManager = settingsManager,
                    getCustomDrawable = getCustomDrawable,
                    onAppClick = {
                        contextMenuApp = null
                        handleAppOpen(it, null)
                    },
                    onAppClickWithBounds = { app, bounds ->
                        contextMenuApp = null
                        handleAppOpen(app, bounds)
                    }
                )
            }
        }

        // --- APP CONTEXT MENU POPUP ---
        if (contextMenuApp != null && draggedIndex == null) {
            val app = contextMenuApp!!
            with(density) {
                val menuX = (contextMenuPosition.x - 70.dp.toPx()).coerceIn(16.dp.toPx(), screenWidthPx - 180.dp.toPx())
                val menuY = (contextMenuPosition.y - 140.dp.toPx()).coerceAtLeast(60.dp.toPx())

                Box(
                    modifier = Modifier
                        .offset { IntOffset(menuX.roundToInt(), menuY.roundToInt()) }
                        .width(170.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF2C2C2E).copy(alpha = 0.92f),
                                    Color(0xFF1C1C1E).copy(alpha = 0.95f)
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                        .padding(6.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    contextMenuApp = null
                                    repository.openAppInfo(app.packageName)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ⓘ", color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("App info", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    contextMenuApp = null
                                    repository.uninstallApp(app.packageName)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🗑", color = Color(0xFFFF453A), fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Uninstall", color = Color(0xFFFF453A), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val updated = gridApps.filter { it.packageName != app.packageName }.toMutableList()
                                    gridApps = updated
                                    saveCurrentLayout(updated)
                                    contextMenuApp = null
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✕", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Remove", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // --- FLOATING DRAGGED ICON ---
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

        // --- DUAL OPEN & CLOSE OVERLAY ---
        if (activeApp != null && activeBounds != null && p > 0.005f) {
            val b = activeBounds!!
            val currentX = b.left * (1f - p)
            val currentY = b.top * (1f - p)
            val currentW = b.width() + (screenWidthPx - b.width()) * p
            val currentH = b.height() + (screenHeightPx - b.height()) * p
            val initialCornerPx = (b.width() * (settingsManager.iconCornerRadius / 100f))
            val currentRadius = initialCornerPx * (1f - p)

            with(density) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                        .size(currentW.toDp(), currentH.toDp())
                        .clip(RoundedCornerShape(currentRadius.toDp()))
                        .background(Color(0xFF141416))
                        .graphicsLayer { alpha = p.coerceIn(0.1f, 1f) },
                    contentAlignment = Alignment.Center
                ) {
                    val targetDrawable = getCustomDrawable(activeApp!!.packageName) ?: activeApp!!.icon
                    val cacheKey = "${activeApp!!.packageName}_${targetDrawable.hashCode()}"
                    val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .size((settingsManager.iconSize * 1.35f).dp)
                                .scale(1f + (0.35f * p))
                        )
                    }
                }
            }
        }
    }
}
