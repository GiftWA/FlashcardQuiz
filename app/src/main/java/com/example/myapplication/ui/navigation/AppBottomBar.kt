package com.example.myapplication.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.BlueTint
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun AppBottomBar(
    current: AppDestination,
    onSelect: (AppDestination) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        AppDestination.entries.forEach { dest ->
            val selected = dest == current
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(dest) },
                icon = {
                    // Soft blue pill behind the icon when selected (matches Stitch).
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) BlueTint else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = dest.icon,
                            contentDescription = dest.label,
                            tint = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                TextSecondary
                        )
                    }
                },
                label = {
                    Text(
                        text = dest.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            TextSecondary
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}