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
fun TopicSelectionScreen(
    course: String,
    onTopicStudy: (String) -> Unit,
    onTopicQuiz: (String) -> Unit,
    onBackClick: () -> Unit
) {
    data class TopicMeta(
        val topic: String,
        val description: String,
        val difficulty: String,
        val cardCount: Int,
        val questionCount: Int
    )

    fun metaFor(courseName: String, topic: String): TopicMeta {
        val descriptions = mapOf(
            "Database Fundamentals" to "Basic definitions, DBMS architecture, data abstraction layers, and instance vs schema.",
            "ER Diagrams" to "Entity-Relationship modelling, entity sets, attributes, relationships, cardinality ratios.",
            "Relational Model" to "Relational algebra, tuple relational calculus, keys (primary, foreign, candidate), constraints.",
            "Normalization" to "Functional dependencies, anomalies, 1NF, 2NF, 3NF, BCNF rules.",
            "Network Fundamentals" to "Network types, topologies, hardware, and basic addressing concepts.",
            "OSI Model" to "Seven layers of the OSI model and how each layer interacts with the others.",
            "TCP/IP" to "TCP/IP stack, packet routing, ports, sockets, and the protocols that power the internet.",
            "Network Security" to "Threats, encryption, firewalls, and secure communication protocols.",
            "Process Management" to "Process lifecycle, scheduling, context switching, and inter-process communication.",
            "Memory Management" to "Paging, segmentation, virtual memory, and allocation strategies.",
            "File Systems" to "Files, directories, allocation methods, and disk scheduling.",
            "CPU Scheduling" to "FCFS, SJF, Round Robin, priority scheduling, and performance metrics.",
            "Android Fundamentals" to "Android SDK, activity basics, application structure, and core components.",
            "Activities and Lifecycle" to "The activity lifecycle, state management, and configuration changes.",
            "Jetpack Compose" to "Declarative UI, composables, state, and recomposition.",
            "Android UI Design" to "Material Design principles, layouts, and consistent spacing."
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

        val description = descriptions[topic] ?: "Study the key concepts of this topic."
        val difficulty = difficulties[topic] ?: "Intermediate"
        val cardCount = getFlashcards(topic).size
        val questionCount = getQuizQuestions(topic).size
        return TopicMeta(topic, description, difficulty, cardCount, questionCount)
    }

    val topics = getTopics(course)
    val totalCards = topics.sumOf { getFlashcards(it).size }
    val totalQuestions = topics.sumOf { getQuizQuestions(it).size }

    val category = when (course) {
        "Database Management", "Mobile Application Development" -> "Practical"
        "Computer Networks", "Operating Systems" -> "Theoretical"
        else -> "Course"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
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
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1565C0),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE3F2FD))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = course,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Core concepts, definitions and exam-focused revision for $course.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF52606D)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${topics.size} Topics · $totalCards Flashcards · $totalQuestions Quizzes · ~2.5 hrs est.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Module Progress", style = MaterialTheme.typography.labelSmall, color = Color(0xFF52606D))
                    Text("35%", style = MaterialTheme.typography.labelSmall, color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFD9E3F1))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1565C0))
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Topics",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "${topics.size} Modules",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDFE9F7))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        topics.forEachIndexed { index, topic ->
            val meta = metaFor(course, topic)
            TopicCard(
                topic = meta.topic,
                description = meta.description,
                difficulty = meta.difficulty,
                cardCount = meta.cardCount,
                questionCount = meta.questionCount,
                onStudy = { onTopicStudy(topic) },
                onQuiz = { onTopicQuiz(topic) }
            )
            if (index < topics.size - 1) Spacer(Modifier.height(12.dp))
        }
    }
}
