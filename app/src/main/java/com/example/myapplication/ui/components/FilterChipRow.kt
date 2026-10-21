package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.BrandBlue
import com.example.myapplication.ui.theme.BlueTint
import com.example.myapplication.ui.theme.TextSecondary
import androidx.compose.foundation.horizontalScroll

// A single chip. Selected chip = tinted background, blue text.
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) BlueTint else Color.White
    val fg = if (selected) BrandBlue else TextSecondary

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

// Convenience: a row of the four standard chips.
@Composable
fun FilterChipRow(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("All", "Theoretical", "Practical", "Mathematical")
    Row(
        modifier = modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())
    ) {
        options.forEachIndexed { index, label ->
            if (index > 0) Spacer(Modifier.width(8.dp))
            FilterChip(
                label = label,
                selected = label == selected,
                onClick = { onSelected(label) }
            )
        }
    }
}