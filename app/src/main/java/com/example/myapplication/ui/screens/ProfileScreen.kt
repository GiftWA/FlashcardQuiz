package com.example.myapplication.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import com.example.myapplication.ui.components.QuizOptionTile
import com.example.myapplication.ui.theme.BlueTint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.AppTopBar
import com.example.myapplication.ui.components.AppTopBarWithBack
import com.example.myapplication.ui.components.CourseCard
import com.example.myapplication.ui.components.CourseGridCard
import com.example.myapplication.ui.components.FilterChipRow
import com.example.myapplication.ui.components.FlashcardItem
import com.example.myapplication.ui.components.SmallTextLink
import com.example.myapplication.ui.components.StatTile
import com.example.myapplication.ui.components.TonalButton
import com.example.myapplication.ui.components.TopicCard
import com.example.myapplication.ui.navigation.AppBottomBar
import com.example.myapplication.ui.navigation.AppDestination
import com.example.myapplication.ui.theme.BrandBlue
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import com.example.myapplication.ui.components.QuizListCard
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.data.*
import com.example.myapplication.ui.components.*

@Composable
fun ProfileTabScreen(
    onAboutClick: () -> Unit,
    userName: String = "Guest Student",
    userTrack: String = "Computer Science Track",
    onSaveProfile: (String, String) -> Unit = { _, _ -> },
    quizResults: List<QuizResult> = emptyList(),
    studiedTopics: List<String> = emptyList(),
    notesCount: Int = 0
) {
    var showEdit by remember { mutableStateOf(false) }
    val initials = userName.trim().split(" ").filter { it.isNotEmpty() }.take(2)
        .joinToString("") { it.first().uppercase() }.ifEmpty { "G" }

    // Real statistics from what the user has done since opening the app
    val quizzesTaken = quizResults.size
    val averageScore = if (quizResults.isEmpty()) "-" else {
        val percents = quizResults.map { if (it.total > 0) it.score * 100 / it.total else 0 }
        "${percents.sum() / percents.size}%"
    }
    val cardsStudied = studiedTopics.sumOf { getFlashcards(it).size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // ---- Identity card ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar with online dot
                    Box(modifier = Modifier.size(56.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        }
                        // Online dot
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color(0xFF1565C0))
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1F2933),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Learning self-paced",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF52606D)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = userTrack,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1565C0),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE3F2FD))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "✎",
                        fontSize = 18.sp,
                        color = Color(0xFF52606D),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showEdit = true }
                            .padding(6.dp)
                    )
                }

            }
        }

        Spacer(Modifier.height(20.dp))

        // ---- Review Statistics ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Review Statistics",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1F2933),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "This session",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF52606D)
            )
        }

        Spacer(Modifier.height(10.dp))

        // 2×2 stat grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileStatTile(
                value = quizzesTaken.toString(),
                label = "Quizzes Taken",
                modifier = Modifier.weight(1f)
            )
            ProfileStatTile(
                value = averageScore,
                label = "Average Score",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileStatTile(
                value = cardsStudied.toString(),
                label = "Cards Studied",
                modifier = Modifier.weight(1f)
            )
            ProfileStatTile(
                value = notesCount.toString(),
                label = "Notes Added",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ---- Recent Activity ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1F2933),
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(10.dp))

        if (quizResults.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "No activity yet. Complete a quiz and it will show up here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF52606D),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        } else {
            quizResults.takeLast(3).reversed().forEach { result ->
                val percent = if (result.total > 0) result.score * 100 / result.total else 0
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✓", color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Completed Quiz: ${result.topic}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF1F2933)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Score: $percent% • ${result.course}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF52606D)
                            )
                        }
                        Text(
                            text = "${result.score}/${result.total}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF52606D)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---- Settings list ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SettingsRow(
                    icon = "👤",
                    title = "Edit Profile",
                    subtitle = "Update personal info and department",
                    onClick = { showEdit = true }
                )
                SettingsRow(
                    icon = "ℹ",
                    title = "About FlashcardQuiz",
                    subtitle = "Version 1.0",
                    onClick = onAboutClick
                )
                SettingsRow(
                    icon = "?",
                    title = "Help & Guidelines",
                    subtitle = "Spaced repetition FAQ & support",
                    onClick = { }
                )
            }
        }

        if (showEdit) {
            var nameInput by remember { mutableStateOf(userName) }
            var trackInput by remember { mutableStateOf(userTrack) }
            val fieldColors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0),
                focusedLabelColor = Color(0xFF1565C0),
                cursorColor = Color(0xFF1565C0)
            )

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showEdit = false },
                title = { Text("Edit Profile") },
                text = {
                    Column {
                        androidx.compose.material3.OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full name") },
                            singleLine = true,
                            colors = fieldColors
                        )
                        Spacer(Modifier.height(12.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = trackInput,
                            onValueChange = { trackInput = it },
                            label = { Text("Department or programme") },
                            singleLine = true,
                            colors = fieldColors
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onSaveProfile(
                            nameInput.trim().ifEmpty { "Guest Student" },
                            trackInput.trim().ifEmpty { "Computer Science Track" }
                        )
                        showEdit = false
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showEdit = false }) { Text("Cancel") }
                }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileStatTile(
    value: String,
    label: String,
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
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD))
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1F2933),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF52606D)
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: String,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val bg = if (isDestructive) Color(0xFFFFDAD6) else Color(0xFFE3F2FD)
    val fg = if (isDestructive) Color(0xFFBA1A1A) else Color(0xFF1565C0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 15.sp, color = fg, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (isDestructive) Color(0xFFBA1A1A) else Color(0xFF1F2933),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF52606D)
            )
        }
        Text(
            text = "›",
            fontSize = 22.sp,
            color = Color(0xFFCBD2D9)
        )
    }
}
