package com.ourlauncher.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.toImageBitmap

/**
 * One tappable app icon + label, used on both the home screen grid and the app drawer.
 * `iconCornerRadius` is exposed now (even though it's hardcoded at call sites) because
 * Phase 5 (icon customization: shape/corner-radius/opacity) will thread a user setting here.
 */
@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    showLabel: Boolean = true,
    iconSizeDp: Int = 56,
    iconCornerRadius: Int = 16,
    labelColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(app.packageName) { app.icon.toImageBitmap() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = app.label,
            modifier = Modifier
                .size(iconSizeDp.dp)
                .clip(RoundedCornerShape(iconCornerRadius.dp))
        )
        if (showLabel) {
            Text(
                text = app.label,
                color = labelColor,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
