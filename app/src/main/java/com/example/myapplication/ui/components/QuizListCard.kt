package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@Composable
fun QuizListCard(
    courseName: String,
    topicName: String,
    category: String,
    questionCount: Int,
    difficulty: String,
    estimatedMinutes: Int,
    onClick: () -> Unit,
    isSaved: Boolean = false,
    onToggleSave: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top row: icon + name block + bookmark
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = courseName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D)
                    )
                    Text(
                        text = topicName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1F2933),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onToggleSave() }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSaved) "🔖" else "🏷",
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Metadata row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetaPill(text = category)
                Text("•", color = Color(0xFFCBD2D9), fontSize = 12.sp)
                Text(
                    text = "$questionCount Questions",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D)
                )
                Text("•", color = Color(0xFFCBD2D9), fontSize = 12.sp)
                Text(
                    text = difficulty,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D)
                )
                Text("•", color = Color(0xFFCBD2D9), fontSize = 12.sp)
                Text(
                    text = "~${estimatedMinutes} mins",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Bottom row: hint + start button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$questionCount questions • 4 choices each",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D)
                )
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Start Quiz →",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaPill(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF1565C0),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE3F2FD))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}