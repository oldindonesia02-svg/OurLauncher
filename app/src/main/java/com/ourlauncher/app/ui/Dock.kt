package com.ourlauncher.app.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo

@Composable
fun Dock(
    pinnedApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    cornerRadiusPercent: Float = 25f,
    iconOpacity: Float = 1.0f,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClickWithBounds: ((AppInfo, android.graphics.Rect) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val glassShape = RoundedCornerShape(28.dp)

    val glassBgGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.14f),
            Color.Black.copy(alpha = 0.28f)
        )
    )

    val glassBorderGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.55f),
            Color.White.copy(alpha = 0.10f)
        )
    )

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(glassShape)
            .background(brush = glassBgGradient)
            .border(width = 1.dp, brush = glassBorderGradient, shape = glassShape)
            .padding(vertical = 8.dp)
    ) {
        pinnedApps.take(4).forEach { app ->
            AppIcon(
                app = app,
                onClick = { onAppClick(app) },
                showLabel = false,
                iconSizeDp = 48,
                cornerRadiusPercent = cornerRadiusPercent,
                iconOpacity = iconOpacity,
                customDrawable = getCustomDrawable(app.packageName),
                onClickWithBounds = onAppClickWithBounds?.let { callback ->
                    { bounds: android.graphics.Rect -> callback(app, bounds) }
                }
            )
        }
    }
}
