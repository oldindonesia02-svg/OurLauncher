package com.ourlauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import kotlin.math.roundToInt

data class GridOption(val cols: Int, val rows: Int, val label: String)

@Composable
fun PersonalizeLayoutSheet(
    settingsManager: SettingsManager,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    var liveCols by remember { mutableIntStateOf(settingsManager.gridColumns) }
    var liveRows by remember { mutableIntStateOf(settingsManager.gridRows) }
    var liveIconSize by remember { mutableFloatStateOf(settingsManager.iconSize) }
    var liveCornerRadius by remember { mutableFloatStateOf(settingsManager.iconCornerRadius) }
    var liveSearchOffset by remember { mutableFloatStateOf(settingsManager.searchOffset) }

    val gridOptions = remember {
        listOf(
            GridOption(4, 4, "4 × 4"),
            GridOption(4, 5, "4 × 5"),
            GridOption(4, 6, "4 × 6"),
            GridOption(5, 5, "5 × 5"),
            GridOption(5, 6, "5 × 6")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.50f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF16222F).copy(alpha = 0.98f),
                            Color(0xFF0C141C).copy(alpha = 0.99f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color(0xFF00E5FF).copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(horizontal = 22.dp, vertical = 18.dp)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Top Handle Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(42.dp)
                        .height(4.5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.35f))
                )

                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Screen Layout & Grid",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 1. Grid Size Chips
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Grid Density",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gridOptions.forEach { opt ->
                            val isSelected = liveCols == opt.cols && liveRows == opt.rows
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFF007BFF).copy(alpha = 0.35f)
                                        else Color.White.copy(alpha = 0.06f)
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        liveCols = opt.cols
                                        liveRows = opt.rows
                                        settingsManager.gridColumns = opt.cols
                                        settingsManager.gridRows = opt.rows
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt.label,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // 2. Icon Size Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Icon Size", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                        Text("${liveIconSize.roundToInt()} dp", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = liveIconSize,
                        onValueChange = {
                            liveIconSize = it
                            settingsManager.iconSize = it
                        },
                        valueRange = 45f..75f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF007BFF),
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }

                // 3. Corner Radius Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Icon Corner Radius", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                        Text("${liveCornerRadius.roundToInt()}%", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = liveCornerRadius,
                        onValueChange = {
                            liveCornerRadius = it
                            settingsManager.iconCornerRadius = it
                        },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF007BFF),
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }

                // 4. Search Bar Height Position Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Search Bar Position", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                        Text("${liveSearchOffset.roundToInt()} px", color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = liveSearchOffset,
                        onValueChange = {
                            liveSearchOffset = it
                            settingsManager.searchOffset = it
                        },
                        valueRange = -120f..80f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E5FF),
                            activeTrackColor = Color(0xFF007BFF),
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00A2FF), Color(0xFF0066FF))
                            )
                        )
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Done",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
