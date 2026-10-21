package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BlueTint
import com.example.myapplication.ui.theme.BrandBlue
import com.example.myapplication.ui.theme.TextPrimary

@Composable
fun QuizOptionTile(
    letter: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) BrandBlue else Color(0xFFCBD2D9)
    val backgroundColor = if (selected) BlueTint else Color.White
    val letterBg = if (selected) BrandBlue else Color(0xFFEFF3F7)
    val letterFg = if (selected) Color.White else Color(0xFF52606D)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(width = if (selected) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Letter chip (A/B/C/D)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(letterBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = letterFg
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        // Radio indicator
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (selected) BrandBlue else Color(0xFFEFF3F7))
                .border(
                    width = 2.dp,
                    color = if (selected) BrandBlue else Color(0xFFCBD2D9),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}