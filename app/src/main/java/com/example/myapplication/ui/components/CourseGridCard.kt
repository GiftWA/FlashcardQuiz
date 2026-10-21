package com.example.myapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.BlueTint
import com.example.myapplication.ui.theme.BrandBlue
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun CourseGridCard(
    title: String,
    category: String,
    topicCount: Int,
    difficulty: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val isPractical = category.equals("Practical", ignoreCase = true)
    val badgeBg = if (isPractical) BlueTint else Color(0xFFDFE9F7)
    val badgeFg = if (isPractical) BrandBlue else TextSecondary

    // Use the icon passed in. If none was passed, pick one from the course title.
    val displayIcon: ImageVector? = icon ?: when (title) {
        "Database Management" -> Icons.Outlined.Storage
        "Computer Networks" -> Icons.Outlined.Hub
        "Operating Systems" -> Icons.Outlined.Memory
        "Mobile Application Development" -> Icons.Outlined.PhoneAndroid
        else -> null
    }

    // The height is fixed at 160dp so every card is exactly the same size.
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: icon on the left, category badge on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BlueTint),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayIcon != null) {
                        Icon(
                            imageVector = displayIcon,
                            contentDescription = null,
                            tint = BrandBlue
                        )
                    }
                }
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeFg,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            // Middle: course title (always reserves two lines)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                minLines = 2
            )

            // Bottom: topics and difficulty
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "$topicCount Topics",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = difficulty,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}