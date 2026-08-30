package com.ourlauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

@Composable
fun HomeScreenSettingsSheet(
    settingsManager: SettingsManager,
    onOpenTransitionEffects: () -> Unit,
    onSetDefaultScreen: () -> Unit,
    onRegenerateIcons: () -> Unit,
    onOpenMoreSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    var gridRows by remember { mutableFloatStateOf(settingsManager.gridRows.toFloat()) }
    var iconSize by remember { mutableFloatStateOf(settingsManager.iconSize.toFloat()) }
    var showLabel by remember { mutableStateOf(settingsManager.showLabels) }
    var liquidFolder by remember { mutableStateOf(settingsManager.liquidFolderEnabled) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Central Main Liquid Glass Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF162A3B).copy(alpha = 0.82f),
                            Color(0xFF0C1924).copy(alpha = 0.90f)
                        )
                    )
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color(0xFF00E5FF).copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Title
                Text(
                    text = "Home Screen",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )

                // Sub-Card 1: Sliders Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Desktop Grid", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        Text("${gridRows.toInt()} Rows", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    LiquidGlassSlider(
                        value = gridRows,
                        onValueChange = { gridRows = it },
                        valueRange = 4f..8f
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Icon Size", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        Text("${iconSize.toInt()} dp", color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    LiquidGlassSlider(
                        value = iconSize,
                        onValueChange = { iconSize = it },
                        valueRange = 48f..80f
                    )
                }

                // Sub-Card 2: Quick Settings Links
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetDefaultScreen() }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Set default screen", color = Color.White, fontSize = 14.sp)
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.08f)))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenTransitionEffects() }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Customize Icons", color = Color.White, fontSize = 14.sp)
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                    }
                }

                // Sub-Card 3: Toggles
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    LiquidGlassToggle(
                        title = "Show label",
                        checked = showLabel,
                        onCheckedChange = { showLabel = it }
                    )

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.08f)))

                    LiquidGlassToggle(
                        title = "Liquid folder",
                        checked = liquidFolder,
                        onCheckedChange = { liquidFolder = it }
                    )
                }

                // Sub-Card 4: Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                            .clickable { onOpenMoreSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("More", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00A2FF), Color(0xFF0072FF))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            .clickable {
                                settingsManager.gridRows = gridRows.toInt()
                                settingsManager.iconSize = iconSize.toInt()
                                settingsManager.showLabels = showLabel
                                settingsManager.liquidFolderEnabled = liquidFolder
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Apply", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
