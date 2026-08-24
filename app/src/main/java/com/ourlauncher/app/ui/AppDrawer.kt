package com.ourlauncher.app.ui

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    apps: List<AppInfo>,
    settingsManager: SettingsManager = SettingsManager(LocalContext.current),
    getCustomDrawable: (String) -> Drawable? = { null },
    iconSize: Float = settingsManager.iconSize,
    iconSizeDp: Float = iconSize,
    cornerRadiusPercent: Float = settingsManager.iconCornerRadius,
    iconCornerRadius: Float = cornerRadiusPercent,
    iconOpacity: Float = settingsManager.iconOpacity,
    showLabel: Boolean = settingsManager.showLabels,
    showLabels: Boolean = showLabel,
    fontFamily: String = settingsManager.fontFamily,
    fontFamilyName: String = fontFamily,
    onAppClick: (AppInfo) -> Unit = {},
    onAppClickWithBounds: (AppInfo, Rect) -> Unit = { app, _ -> onAppClick(app) },
    onCloseDrawer: () -> Unit = {}
) {
    BackHandler { onCloseDrawer() }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val gridState = rememberLazyGridState()

    var searchQuery by remember { mutableStateOf("") }
    val drawerOffsetY = remember { Animatable(0f) }

    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.isBlank()) {
            apps.sortedBy { it.label.lowercase() }
        } else {
            apps.filter {
                it.label.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
            }.sortedBy { it.label.lowercase() }
        }
    }

    val alphabet = remember { ('A'..'Z').toList() }
    val alphabetIndexMap = remember(filteredApps) {
        val map = mutableMapOf<Char, Int>()
        filteredApps.forEachIndexed { index, app ->
            val firstChar = app.label.firstOrNull()?.uppercaseChar() ?: '#'
            if (!map.containsKey(firstChar) && firstChar in 'A'..'Z') {
                map[firstChar] = index
            }
        }
        map
    }

    var activeScrollLetter by remember { mutableStateOf<Char?>(null) }

    // --- NATURAL NESTED SCROLL PULL-DOWN DISMISS ---
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // If drawer is already pulled down, pull it further or back
                if (drawerOffsetY.value > 0f && available.y < 0) {
                    val consumed = available.y
                    coroutineScope.launch {
                        drawerOffsetY.snapTo((drawerOffsetY.value + consumed).coerceAtLeast(0f))
                    }
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // When at the top of grid, pull down the whole drawer
                if (available.y > 0 && gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0) {
                    coroutineScope.launch {
                        drawerOffsetY.snapTo(drawerOffsetY.value + (available.y * 0.75f))
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (drawerOffsetY.value > 220f || available.y > 1000f) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onCloseDrawer()
                    return available
                } else if (drawerOffsetY.value > 0f) {
                    drawerOffsetY.animateTo(0f, tween(200))
                    return available
                }
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0E))
            .graphicsLayer {
                translationY = drawerOffsetY.value
                alpha = (1f - (drawerOffsetY.value / 1200f)).coerceIn(0.2f, 1f)
            }
            .nestedScroll(nestedScrollConnection)
            .pointerInput(Unit) {
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = { totalDragY = 0f },
                    onDrag = { _, dragAmount ->
                        totalDragY += dragAmount.y
                        if (totalDragY > 0) {
                            coroutineScope.launch {
                                drawerOffsetY.snapTo(totalDragY * 0.75f)
                            }
                        }
                    },
                    onDragEnd = {
                        if (drawerOffsetY.value > 220f) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onCloseDrawer()
                        } else {
                            coroutineScope.launch {
                                drawerOffsetY.animateTo(0f, tween(200))
                            }
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp)
        ) {
            // --- TOP LIQUID SEARCH BAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.14f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔍",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search apps...",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 15.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(Color(0xFF0A84FF)),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    if (filteredApps.isNotEmpty()) {
                                        onAppClick(filteredApps.first())
                                    }
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { searchQuery = "" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            // --- APPS GRID & A-Z FAST SCROLL ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(top = 12.dp, start = 12.dp, end = 36.dp, bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = filteredApps,
                        key = { _, app -> app.packageName }
                    ) { _, app ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                app = app,
                                onClick = { onAppClick(app) },
                                showLabel = showLabel,
                                fontFamilyName = fontFamilyName,
                                iconSizeDp = iconSize,
                                cornerRadiusPercent = cornerRadiusPercent,
                                iconOpacity = iconOpacity,
                                customDrawable = getCustomDrawable(app.packageName),
                                onClickWithBounds = { bounds -> onAppClickWithBounds(app, bounds) },
                                modifier = Modifier.width(80.dp)
                            )
                        }
                    }
                }

                // --- A-Z ALPHABET SCROLLER ---
                if (searchQuery.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                            .fillMaxHeight(0.85f)
                            .width(28.dp)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onDragStart = { offset ->
                                        val itemHeight = size.height / alphabet.size
                                        val index = (offset.y / itemHeight).toInt().coerceIn(0, alphabet.size - 1)
                                        val char = alphabet[index]
                                        activeScrollLetter = char
                                        alphabetIndexMap[char]?.let { targetIndex ->
                                            coroutineScope.launch { gridState.scrollToItem(targetIndex) }
                                        }
                                    },
                                    onVerticalDrag = { change, _ ->
                                        val itemHeight = size.height / alphabet.size
                                        val index = (change.position.y / itemHeight).toInt().coerceIn(0, alphabet.size - 1)
                                        val char = alphabet[index]
                                        activeScrollLetter = char
                                        alphabetIndexMap[char]?.let { targetIndex ->
                                            coroutineScope.launch { gridState.scrollToItem(targetIndex) }
                                        }
                                    },
                                    onDragEnd = { activeScrollLetter = null },
                                    onDragCancel = { activeScrollLetter = null }
                                )
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            alphabet.forEach { char ->
                                val hasApps = alphabetIndexMap.containsKey(char)
                                Text(
                                    text = char.toString(),
                                    fontSize = 9.5.sp,
                                    fontWeight = if (hasApps) FontWeight.Bold else FontWeight.Normal,
                                    color = if (hasApps) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.clickable(enabled = hasApps) {
                                        alphabetIndexMap[char]?.let { targetIndex ->
                                            coroutineScope.launch { gridState.animateScrollToItem(targetIndex) }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // --- LETTER POPUP INDICATOR ---
                if (activeScrollLetter != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF2C2C2E).copy(alpha = 0.95f),
                                        Color(0xFF1C1C1E).copy(alpha = 0.98f)
                                    )
                                )
                            )
                            .border(1.5.dp, Color(0xFF0A84FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeScrollLetter.toString(),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
