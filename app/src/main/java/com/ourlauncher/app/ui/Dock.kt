package com.ourlauncher.app.ui

import android.graphics.Rect
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
import com.ourlauncher.app.SettingsManager

@Composable
fun Dock(
    pinnedApps: List<AppInfo>,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: ((AppInfo, Rect) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val glassShape = RoundedCornerShape(settingsManager.dockRadius.dp)

    val glassAlpha = settingsManager.glassTransparency.coerceIn(0.05f, 0.85f)
    val borderAlpha = (settingsManager.glassRefractionAmount / 50f).coerceIn(0.15f, 0.95f)
    val topLightAlpha = (settingsManager.glassRefractionHeight / 50f).coerceIn(0.2f, 1.0f)

    val glassBg = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = (glassAlpha + 0.08f).coerceAtMost(0.9f)),
            Color(0xFF141416).copy(alpha = (1f - glassAlpha).coerceIn(0.2f, 0.85f))
        )
    )

    val glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = topLightAlpha),
            Color.White.copy(alpha = borderAlpha * 0.35f),
            Color.Black.copy(alpha = 0.5f)
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
                    if (settingsManager.showDockBg) {
                        Modifier
                            .clip(glassShape)
                            .background(brush = glassBg)
                            .border(width = 1.3.dp, brush = glassBorder, shape = glassShape)
                    } else Modifier
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            pinnedApps.take(4).forEach { app ->
                AppIcon(
                    app = app,
                    onClick = { onAppClick(app) },
                    showLabel = false,
                    fontFamilyName = settingsManager.fontFamily,
                    iconSizeDp = settingsManager.iconSize,
                    cornerRadiusPercent = settingsManager.iconCornerRadius,
                    iconOpacity = settingsManager.iconOpacity,
                    customDrawable = getCustomDrawable(app.packageName),
                    onClickWithBounds = onAppClickWithBounds?.let { callback ->
                        { bounds: Rect -> callback(app, bounds) }
                    }
                )
            }
        }
    }
}
