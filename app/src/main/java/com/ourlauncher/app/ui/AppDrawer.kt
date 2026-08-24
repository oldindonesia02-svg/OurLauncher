package com.ourlauncher.app.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo

@Composable
fun AppDrawer(
    apps: List<AppInfo>,
    iconSize: Float = 54f,
    cornerRadiusPercent: Float = 25f,
    iconOpacity: Float = 1.0f,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, android.graphics.Rect) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val gridState = rememberLazyGridState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.isEmpty()) apps
        else apps.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 8f && gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0) {
                    onCloseDrawer()
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }
        }
    }

    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val glassBgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF141418).copy(alpha = 0.72f), Color(0xFF0A0A0D).copy(alpha = 0.88f))
    )
    val glassBorderGradient = Brush.verticalGradient(
        colors = listOf(Color.White.copy(alpha = 0.45f), Color.White.copy(alpha = 0.08f))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _: PointerInputChange, dragAmount: Float ->
                    if (dragAmount > 10f) onCloseDrawer()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp)
                .clip(sheetShape)
                .background(brush = glassBgGradient)
                .border(width = 1.dp, brush = glassBorderGradient, shape = sheetShape)
                .nestedScroll(nestedScrollConnection)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 42.dp, height = 5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.45f))
                    .clickable { onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(22.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    cursorBrush = SolidColor(Color(0xFF0A84FF)),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(text = "Search apps...", color = Color.White.copy(alpha = 0.4f), fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 40.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = filteredApps, key = { it.packageName }) { app ->
                    AppIcon(
                        app = app,
                        iconSizeDp = iconSize,
                        cornerRadiusPercent = cornerRadiusPercent,
                        iconOpacity = iconOpacity,
                        customDrawable = getCustomDrawable(app.packageName),
                        onClick = { onAppClick(app) },
                        onClickWithBounds = { bounds -> onAppClickWithBounds(app, bounds) }
                    )
                }
            }
        }
    }
}
