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
fun QuizzesTabScreen(
    onStartQuiz: (course: String, topic: String) -> Unit,
    savedTopics: List<String>,
    onToggleSave: (String) -> Unit
) {
    // Build the list of all 16 quizzes (one per topic across all 4 courses)
    data class QuizEntry(
        val course: String,
        val topic: String,
        val category: String,
        val difficulty: String,
        val questionCount: Int,
        val estimatedMinutes: Int
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

    fun categoryFor(course: String): String = when (course) {
        "Database Management", "Mobile Application Development" -> "Practical"
        "Computer Networks", "Operating Systems" -> "Theoretical"
        else -> "Course"
    }

    val allQuizzes = buildList {
        listOf(
            "Database Management",
            "Computer Networks",
            "Operating Systems",
            "Mobile Application Development"
        ).forEach { course ->
            getTopics(course).forEach { topic ->
                val qCount = getQuizQuestions(topic).size
                add(
                    QuizEntry(
                        course = course,
                        topic = topic,
                        category = categoryFor(course),
                        difficulty = difficulties[topic] ?: "Intermediate",
                        questionCount = qCount,
                        estimatedMinutes = (qCount * 2).coerceAtLeast(4)
                    )
                )
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All Quizzes") }

    val filtered = allQuizzes.filter { q ->
        val matchesQuery = searchQuery.isBlank() ||
                q.topic.contains(searchQuery, ignoreCase = true) ||
                q.course.contains(searchQuery, ignoreCase = true)

        val matchesChip = when (selectedChip) {
            "All Quizzes"   -> true
            "Recommended"   -> q.difficulty == "Intermediate"
            "New"           -> q.questionCount >= 4
            "Popular"       -> true
            "Saved"         -> savedTopics.contains(q.topic)
            else            -> true
        }
        matchesQuery && matchesChip
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Weekly goal banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WEEKLY DRILL GOAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = "3 of 5 Mastered",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1F2933),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 20.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFBBDEFB))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1565C0))
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Search bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1F2933),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search quizzes...",
                                fontSize = 14.sp,
                                color = Color(0xFF52606D)
                            )
                        }
                        inner()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "✕",
                        fontSize = 16.sp,
                        color = Color(0xFF52606D),
                        modifier = Modifier
                            .clickable { searchQuery = "" }
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All Quizzes", "Recommended", "New", "Popular", "Saved").forEach { label ->
                val isSelected = label == selectedChip
                val bg = if (isSelected) Color(0xFFE3F2FD) else Color.White
                val fg = if (isSelected) Color(0xFF1565C0) else Color(0xFF52606D)

                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = fg,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(bg)
                        .clickable { selectedChip = label }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Cards / empty state
        if (filtered.isEmpty()) {
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
                        text = "No quizzes found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2933)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your search criteria.",
                        fontSize = 12.sp,
                        color = Color(0xFF52606D),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            filtered.forEachIndexed { index, q ->
                QuizListCard(
                    courseName = q.course,
                    topicName = q.topic,
                    category = q.category,
                    questionCount = q.questionCount,
                    difficulty = q.difficulty,
                    estimatedMinutes = q.estimatedMinutes,
                    onClick = { onStartQuiz(q.course, q.topic) },
                    isSaved = savedTopics.contains(q.topic),
                    onToggleSave = { onToggleSave(q.topic) }
                )
                if (index < filtered.size - 1) Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
