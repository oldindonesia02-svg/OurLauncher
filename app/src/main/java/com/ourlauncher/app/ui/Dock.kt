package com.ourlauncher.app.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
    iconSize: Float = 54f,
    cornerRadiusPercent: Float = 25f,
    iconOpacity: Float = 1.0f,
    dockRadius: Float = 36f,
    showDockBg: Boolean = true,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClickWithBounds: ((AppInfo, android.graphics.Rect) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val glassShape = RoundedCornerShape(dockRadius.dp)

    // Frosted Liquid Glass Background Gradient
    val glassBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2C2C2E).copy(alpha = 0.65f),
            Color(0xFF141416).copy(alpha = 0.85f)
        )
    )

    // Glowing border with light refraction on top
    val glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.50f),
            Color.White.copy(alpha = 0.12f),
            Color.Black.copy(alpha = 0.30f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (showDockBg) {
                        Modifier
                            .clip(glassShape)
                            .background(brush = glassBg)
                            .border(width = 1.2.dp, brush = glassBorder, shape = glassShape)
                    } else Modifier
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            pinnedApps.take(4).forEach { app ->
                AppIcon(
                    app = app,
                    onClick = { onAppClick(app) },
                    showLabel = false,
                    iconSizeDp = iconSize,
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
}
