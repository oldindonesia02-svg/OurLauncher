package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditModeTopBar(
    onCancel: () -> Unit,
    onToggleVisibility: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillBg = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.20f),
            Color(0xFF141418).copy(alpha = 0.60f)
        )
    )
    val pillBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.50f),
            Color.White.copy(alpha = 0.15f)
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel Pill
        Box(
            modifier = Modifier
                .height(38.dp)
                .clip(CircleShape)
                .background(brush = pillBg)
                .border(0.8.dp, brush = pillBorder, shape = CircleShape)
                .clickable { onCancel() }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cancel",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Hide/Show Eye Button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(brush = pillBg)
                .border(0.8.dp, brush = pillBorder, shape = CircleShape)
                .clickable { onToggleVisibility() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👁", color = Color.White, fontSize = 15.sp)
        }

        // Done Blue Button
        Box(
            modifier = Modifier
                .height(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF0A84FF))
                .clickable { onDone() }
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Done",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EditModeBottomBar(
    onWallpaperClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onWidgetsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditModeActionItem(title = "Wallpaper", icon = "🖼", onClick = onWallpaperClick)
        EditModeActionItem(title = "Developer", icon = "🧊", onClick = onDeveloperClick)
        EditModeActionItem(title = "Widgets", icon = "▦", onClick = onWidgetsClick)
        EditModeActionItem(title = "Settings", icon = "⚙", onClick = onSettingsClick)
    }
}

@Composable
fun EditModeActionItem(
    title: String,
    icon: String,
    onClick: () -> Unit
) {
    val glassBg = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.22f),
            Color(0xFF16161B).copy(alpha = 0.65f)
        )
    )
    val glassBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.50f),
            Color.White.copy(alpha = 0.12f)
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(brush = glassBg)
                .border(0.9.dp, brush = glassBorder, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun IconSelectionBadge(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.55f))
            .border(
                1.dp,
                if (isSelected) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.45f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A84FF))
            )
        }
    }
}
