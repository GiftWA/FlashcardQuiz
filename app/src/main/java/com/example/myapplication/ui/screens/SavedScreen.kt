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
fun SavedTabScreen(
    savedQuizTopics: List<String>,
    savedFlashcardTopics: List<String>,
    savedNotes: List<Triple<String, Int, String>>,
    onStartQuiz: (course: String, topic: String) -> Unit,
    onOpenFlashcardTopic: (topic: String) -> Unit
) {
    var selectedChip by remember { mutableStateOf("All Saved") }

    data class QuizEntry(
        val course: String,
        val topic: String,
        val questionCount: Int,
        val estimatedMinutes: Int,
        val difficulty: String
    )

    val difficulties = mapOf(
        "Database Fundamentals" to "Beginner",
        "ER Diagrams" to "Intermediate",
        "Relational Model" to "Intermediate",
        "Normalization" to "Advanced",
        "Network Fundamentals" to "Beginner",
        "OSI Model" to "Intermediate",
        "TCP/IP" to "Intermediate",
        "Network Security" to "Advanced",
        "Process Management" to "Intermediate",
        "Memory Management" to "Advanced",
        "File Systems" to "Intermediate",
        "CPU Scheduling" to "Advanced",
        "Android Fundamentals" to "Beginner",
        "Activities and Lifecycle" to "Intermediate",
        "Jetpack Compose" to "Intermediate",
        "Android UI Design" to "Intermediate"
    )

    val allSavedQuizzes = buildList {
        listOf(
            "Database Management",
            "Computer Networks",
            "Operating Systems",
            "Mobile Application Development"
        ).forEach { course ->
            getTopics(course).forEach { topic ->
                if (savedQuizTopics.contains(topic)) {
                    add(
                        QuizEntry(
                            course = course,
                            topic = topic,
                            questionCount = getQuizQuestions(topic).size,
                            estimatedMinutes = (getQuizQuestions(topic).size * 2).coerceAtLeast(4),
                            difficulty = difficulties[topic] ?: "Intermediate"
                        )
                    )
                }
            }
        }
    }

    val totalSaved = allSavedQuizzes.size + savedFlashcardTopics.size + savedNotes.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Segmented filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "All Saved",
                "Quizzes",
                "Flashcards",
                "Notes"
            ).forEach { label ->
                val isSelected = label == selectedChip
                val bg = if (isSelected) Color(0xFF1565C0) else Color.White
                val fg = if (isSelected) Color.White else Color(0xFF52606D)
                val count = when (label) {
                    "All Saved"  -> totalSaved
                    "Quizzes"    -> allSavedQuizzes.size
                    "Flashcards" -> savedFlashcardTopics.size
                    "Notes"      -> savedNotes.size
                    else         -> 0
                }

                Text(
                    text = "$label ($count)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = fg,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(bg)
                        .clickable { selectedChip = label }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Saved Quizzes section
        if (selectedChip == "All Saved" || selectedChip == "Quizzes") {
            if (allSavedQuizzes.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Quizzes",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1F2933),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${allSavedQuizzes.size} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDFE9F7))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                allSavedQuizzes.forEachIndexed { index, q ->
                    QuizListCard(
                        courseName = q.course,
                        topicName = q.topic,
                        category = "Saved",
                        questionCount = q.questionCount,
                        difficulty = q.difficulty,
                        estimatedMinutes = q.estimatedMinutes,
                        onClick = { onStartQuiz(q.course, q.topic) },
                        isSaved = true,
                        onToggleSave = { }
                    )
                    if (index < allSavedQuizzes.size - 1) Spacer(Modifier.height(12.dp))
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        // Saved Flashcards section
        if (selectedChip == "All Saved" || selectedChip == "Flashcards") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Flashcards",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${savedFlashcardTopics.size} topics",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDFE9F7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            if (savedFlashcardTopics.isEmpty()) {
                EmptySavedCard(
                    title = "No saved flashcards yet",
                    subtitle = "Flashcard decks you open will appear here.",
                    ctaText = "Explore Flashcards"
                )
            } else {
                savedFlashcardTopics.forEachIndexed { index, topic ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenFlashcardTopic(topic) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE3F2FD))
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = topic,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF1F2933),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${getFlashcards(topic).size} cards",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF52606D)
                                    )
                                }
                                Text(text = "🔖", fontSize = 16.sp)
                            }
                        }
                    }
                    if (index < savedFlashcardTopics.size - 1) Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // Notes section
        if (selectedChip == "All Saved" || selectedChip == "Notes") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Notes",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${savedNotes.size} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDFE9F7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            if (savedNotes.isEmpty()) {
                EmptySavedCard(
                    title = "No saved notes yet",
                    subtitle = "Notes you create while revising flashcards will appear here.",
                    ctaText = "Explore Flashcards"
                )
            } else {
                savedNotes.forEachIndexed { index, (topic, cardIdx, text) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "$topic • Card ${"%02d".format(cardIdx + 1)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1565C0),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1F2933)
                            )
                        }
                    }
                    if (index < savedNotes.size - 1) Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun EmptySavedCard(
    title: String,
    subtitle: String,
    ctaText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD))
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1F2933),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF52606D),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE3F2FD),
                    contentColor = Color(0xFF1565C0)
                )
            ) {
                Text(ctaText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
