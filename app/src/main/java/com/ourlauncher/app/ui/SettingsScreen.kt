package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    onOpenDesktopGrid: () -> Unit = {},
    onOpenAppIcons: () -> Unit = {},
    onOpenAppAnimation: () -> Unit = {},
    onOpenDock: () -> Unit = {},
    onOpenLiquidGlass: () -> Unit = {},
    onOpenSearchBarPosition: () -> Unit = {},
    onOpenSwipeActions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val cardBg = Color(0xFF141416).copy(alpha = 0.95f)
    val cardShape = RoundedCornerShape(22.dp)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0E))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = Color(0xFF0A84FF),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "CUSTOMIZATION",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(cardBg)
                .padding(vertical = 4.dp)
        ) {
            SettingsNavigationRow(
                title = "Desktop Grid",
                subtitle = "Configure Columns & Rows (4x5, 5x5, 5x6)",
                onClick = onOpenDesktopGrid
            )
            SettingsDivider()

            SettingsNavigationRow(
                title = "App icons",
                subtitle = "Themes, Lens Light, Shape & Size",
                onClick = onOpenAppIcons
            )
            SettingsDivider()

            SettingsNavigationRow(
                title = "App Open Animation",
                subtitle = "Duration & Bezier Curves",
                onClick = onOpenAppAnimation
            )
            SettingsDivider()

            SettingsNavigationRow(
                title = "Dock",
                subtitle = "Padding, Gap and Corner Radius",
                onClick = onOpenDock
            )
            SettingsDivider()

            SettingsNavigationRow(
                title = "Liquid Glass",
                subtitle = "Adjust transparency, blur and lens refraction",
                onClick = onOpenLiquidGlass
            )
            SettingsDivider()

            SettingsNavigationRow(
                title = "Search Bar Position",
                subtitle = "Adjust the vertical offset of the search pill",
                onClick = onOpenSearchBarPosition
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ACTIONS",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(cardBg)
                .padding(vertical = 4.dp)
        ) {
            SettingsNavigationRow(
                title = "Swipe actions",
                subtitle = "Customize gesture swipe behaviors",
                onClick = onOpenSwipeActions
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.50f),
                fontSize = 12.sp
            )
        }

        Text(
            text = "›",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
