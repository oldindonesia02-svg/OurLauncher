package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
fun SwipeActionsScreen(
    onBack: () -> Unit,
    settingsManager: SettingsManager
) {
    var selectedSide by remember { mutableStateOf("left") } // "left" or "right"
    var leftAction by remember { mutableStateOf(settingsManager.leftPullDownAction) }
    var rightAction by remember { mutableStateOf(settingsManager.rightPullDownAction) }

    val currentAction = if (selectedSide == "left") leftAction else rightAction

    val actionOptions = listOf(
        "nothing" to "Nothing",
        "notifications" to "System Notification Bar",
        "system_control_center" to "System Control Center",
        "builtin_control_center" to "Built-in Control Center"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 18.dp, end = 18.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Light)
            }
        }

        Text(
            text = "Swipe actions",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )

        // --- INTERACTIVE PHONE MOCKUP ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(290.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF0F1015))
                    .border(2.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(32.dp))
                    .padding(6.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Half: Divided into Left (Green) and Right (Red) interactive zones
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                    ) {
                        // LEFT ZONE
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(topStart = 26.dp))
                                .background(
                                    if (selectedSide == "left") Color(0xFF00A2FF).copy(alpha = 0.40f)
                                    else Color(0xFF00A2FF).copy(alpha = 0.20f)
                                )
                                .border(
                                    width = if (selectedSide == "left") 2.dp else 1.dp,
                                    color = if (selectedSide == "left") Color(0xFF34C759) else Color(0xFF34C759).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(topStart = 26.dp)
                                )
                                .clickable { selectedSide = "left" }
                                .padding(6.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "2:32",
                                    color = Color(0xFF55E7B4),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                )
                            }
                        }

                        // RIGHT ZONE
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(topEnd = 26.dp))
                                .background(
                                    if (selectedSide == "right") Color(0xFFAF52DE).copy(alpha = 0.40f)
                                    else Color(0xFFAF52DE).copy(alpha = 0.20f)
                                )
                                .border(
                                    width = if (selectedSide == "right") 2.dp else 1.dp,
                                    color = if (selectedSide == "right") Color(0xFFFF3B30) else Color(0xFFFF3B30).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(topEnd = 26.dp)
                                )
                                .clickable { selectedSide = "right" }
                                .padding(6.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "2:32",
                                    color = Color(0xFFFF7A8A),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                )
                            }
                        }
                    }

                    // Bottom Half: Dock / App Matrix Mockup
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                            .background(Color(0xFF007AFF).copy(alpha = 0.28f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(4) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color.White.copy(alpha = 0.25f))
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(Color.White))
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.4f)))
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(4) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color.White.copy(alpha = 0.25f))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- BOTTOM CONFIGURATION MODAL CARD ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFF1C1C1E))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (selectedSide == "left") "Left Swipe Action" else "Right Swipe Action",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select what happens when pulling down from this area:",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                actionOptions.forEach { (key, label) ->
                    val isSelected = currentAction == key
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) Color(0xFF0A84FF).copy(alpha = 0.18f)
                                else Color.Transparent
                            )
                            .clickable {
                                if (selectedSide == "left") {
                                    leftAction = key
                                    settingsManager.leftPullDownAction = key
                                } else {
                                    rightAction = key
                                    settingsManager.rightPullDownAction = key
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                if (selectedSide == "left") {
                                    leftAction = key
                                    settingsManager.leftPullDownAction = key
                                } else {
                                    rightAction = key
                                    settingsManager.rightPullDownAction = key
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF0A84FF),
                                unselectedColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF0A84FF) else Color.White,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
