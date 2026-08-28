package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ourlauncher.app.ui.components.LiquidGlassToggle

@Composable
fun HomeScreenSettingsSheet(
    settingsManager: SettingsManager,
    onOpenTransitionEffects: () -> Unit,
    onSetDefaultScreen: () -> Unit,
    onRegenerateIcons: () -> Unit,
    onOpenMoreSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    var showLabel by remember { mutableStateOf(settingsManager.showLabels) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color(0xFF18181B).copy(alpha = 0.98f))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Drag Indicator
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✕",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onDismiss() }
                )
                Text(
                    text = "Home screen settings",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(18.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Transition Effects
            SheetNavRow(title = "Customize Icons", onClick = onOpenTransitionEffects)

            // 2. Set default screen
            SheetNavRow(title = "Set default screen", onClick = onSetDefaultScreen)

            // 3. Show Label Dropdown/Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show label", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = if (showLabel) "On ⬍" else "Off ⬍",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        showLabel = !showLabel
                        settingsManager.showLabels = showLabel
                    }
                )
            }

            // 4. Liquid Folder Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Liquid folder",
                    color = Color.White,
                    fontSize = 15.sp
                )
                LiquidGlassToggle(
                    checked = settingsManager.liquidFolder,
                    onCheckedChange = { 
                        settingsManager.liquidFolder = it 
                    }
                )    
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Regenerate All Icons (Blue Action)
            Text(
                text = "Regenerate all icons   ›",
                color = Color(0xFF0A84FF),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { onRegenerateIcons() }
                    .padding(vertical = 8.dp)
            )

            // 6. More Settings
            Text(
                text = "More settings   ›",
                color = Color(0xFF0A84FF),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { onOpenMoreSettings() }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun SheetNavRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(text = "›", color = Color.White.copy(alpha = 0.45f), fontSize = 18.sp)
    }
}
