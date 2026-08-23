package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.R

@Composable
fun SearchPill(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Icon(Icons.Filled.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
        Text(
            text = "  " + stringResourceCompat(),
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

// Small helper so this file doesn't need a @Composable ctx import juggle for stringResource
@Composable
private fun stringResourceCompat(): String =
    androidx.compose.ui.res.stringResource(id = R.string.search_hint)
