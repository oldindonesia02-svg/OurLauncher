package com.ourlauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    var showLabel by remember { mutableStateOf(settingsManager.showLabels) }
    var liquidFolder by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom Sheet Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFF1E2024))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(horizontal = 24.dp, vertical = 18.dp)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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

                // Header Row (Close 'X' + Title)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Home screen settings",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // 1. Transition effects
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenTransitionEffects() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Transition effects", color = Color.White, fontSize = 16.sp)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                }

                // 2. Set default screen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSetDefaultScreen() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set default screen", color = Color.White, fontSize = 16.sp)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                }

                // 3. Show label (Toggle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show label", color = Color.White, fontSize = 16.sp)
                    Switch(
                        checked = showLabel,
                        onCheckedChange = {
                            showLabel = it
                            settingsManager.showLabels = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF007BFF),
                            uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }

                // 4. Liquid folder (Toggle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Liquid folder", color = Color.White, fontSize = 16.sp)
                    Switch(
                        checked = liquidFolder,
                        onCheckedChange = { liquidFolder = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF007BFF),
                            uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Regenerate all icons (Blue accent)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onRegenerateIcons() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Regenerate all icons", color = Color(0xFF2980FF), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color(0xFF2980FF).copy(alpha = 0.7f))
                }

                // 6. More settings (Blue accent -> Opens Settings Video 27062.mp4)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenMoreSettings() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("More settings", color = Color(0xFF2980FF), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color(0xFF2980FF).copy(alpha = 0.7f))
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
