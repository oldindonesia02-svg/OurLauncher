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

/**
 * Phase 1: plain semi-transparent rounded bar holding up to 4 pinned apps.
 * The real "Liquid Glass" blur/vibrancy/depth effect is Phase 6 — this is
 * intentionally a flat placeholder so the rest of the layout is correct first.
 */
@Composable
fun Dock(
    pinnedApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
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
            AppIcon(app = app, onClick = { onAppClick(app) }, showLabel = false, iconSizeDp = 48)
        }
    }
}
