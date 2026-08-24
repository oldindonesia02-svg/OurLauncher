package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.AppInfo

@Composable
fun Dock(
    pinnedApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: ((AppInfo, android.graphics.Rect) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
            .padding(vertical = 8.dp)
    ) {
        pinnedApps.take(4).forEach { app ->
            AppIcon(
                app = app,
                onClick = { onAppClick(app) },
                showLabel = false,
                iconSizeDp = 48,
                onClickWithBounds = onAppClickWithBounds?.let { callback ->
                    { bounds: android.graphics.Rect -> callback(app, bounds) }
                }
            )
        }
    }
}
