package com.ourlauncher.app.ui

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawer(
    apps: List<AppInfo>,
    settingsManager: SettingsManager = SettingsManager(LocalContext.current),
    getCustomDrawable: (String) -> Drawable? = { null },
    iconSize: Float = settingsManager.iconSize,
    cornerRadiusPercent: Float = settingsManager.iconCornerRadius,
    iconOpacity: Float = settingsManager.iconOpacity,
    showLabel: Boolean = settingsManager.showLabels,
    fontFamily: String = settingsManager.fontFamily,
    onAppClick: (AppInfo) -> Unit = {},
    onAppClickWithBounds: (AppInfo, Rect) -> Unit = { app, _ -> onAppClick(app) },
    onCloseDrawer: () -> Unit = {}
) {
    BackHandler { onCloseDrawer() }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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

    val columns = settingsManager.gridColumns
    val rows = settingsManager.gridRows + 1 
    val appsPerPage = columns * rows
    val totalPages = maxOf(1, ceil(filteredApps.size.toFloat() / appsPerPage).toInt())
    val pagerState = rememberPagerState(pageCount = { totalPages })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    radius = 1200f
                )
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Black.copy(alpha = 0.35f)
                    )
                )
            )
            .graphicsLayer {
                translationY = drawerOffsetY.value
                alpha = (1f - (abs(drawerOffsetY.value) / 1000f)).coerceIn(0.2f, 1f)
            }
            .pointerInput(Unit) {
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = { totalDragY = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragY += dragAmount.y
                        coroutineScope.launch {
                            drawerOffsetY.snapTo(totalDragY * 0.75f)
                        }
                    },
                    onDragEnd = {
                        if (totalDragY > 150f || totalDragY < -150f) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            coroutineScope.launch {
                                drawerOffsetY.animateTo(if (totalDragY > 0) 1200f else -1200f, tween(200))
                                onCloseDrawer()
                            }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔍", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(text = "Search apps...", color = Color.White.copy(alpha = 0.4f), fontSize = 15.sp)
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Normal),
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

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val start = page * appsPerPage
                val end = minOf(start + appsPerPage, filteredApps.size)
                val pageApps = if (start < filteredApps.size) filteredApps.subList(start, end) else emptyList()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    userScrollEnabled = false,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pageApps, key = { it.packageName }) { app ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppIcon(
                                app = app,
                                onClick = { onAppClick(app) },
                                showLabel = showLabel,
                                fontFamilyName = fontFamily,
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
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(totalPages) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}
