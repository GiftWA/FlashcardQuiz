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
fun CourseSelectionScreen(onCourseClick: (String) -> Unit, onBackClick: () -> Unit) {
    data class CourseMeta(
        val title: String,
        val category: String,
        val topicCount: Int,
        val difficulty: String
    )

    val allCourses = listOf(
        CourseMeta("Database Management", "Practical", 4, "Intermediate"),
        CourseMeta("Computer Networks", "Theoretical", 4, "Advanced"),
        CourseMeta("Operating Systems", "Theoretical", 4, "Intermediate"),
        CourseMeta("Mobile Application Development", "Practical", 4, "Beginner")
    )

    var query by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All Courses") }

    val filtered = allCourses.filter { c ->
        val matchesQuery = query.isBlank() ||
                c.title.contains(query, ignoreCase = true)
        val matchesChip = when (selectedChip) {
            "All Courses" -> true
            "Theoretical" -> c.category == "Theoretical"
            "Practical"   -> c.category == "Practical"
            "Mathematical" -> c.category == "Mathematical"
            else -> true
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
        Text(
            text = "Study",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2933)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1F2933),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Search courses...",
                                fontSize = 14.sp,
                                color = Color(0xFF52606D)
                            )
                        }
                        inner()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips (Horizontal Scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All Courses", "Theoretical", "Practical", "Mathematical").forEach { label ->
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

        Spacer(modifier = Modifier.height(20.dp))

        // Empty State or Grid
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
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No courses found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2933)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your filters or search keywords.",
                        fontSize = 12.sp,
                        color = Color(0xFF52606D),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val rows = filtered.chunked(2)
            rows.forEachIndexed { rowIndex, rowItems ->
                if (rowIndex > 0) Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { c ->
                        CourseGridCard(
                            title = c.title,
                            category = c.category,
                            topicCount = c.topicCount,
                            difficulty = c.difficulty,
                            onClick = { onCourseClick(c.title) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add Custom Course Button (Single instance at the bottom)
        var showAddDialog by remember { mutableStateOf(false) }
        OutlinedButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color(0xFF1565C0)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0))
        ) {
            Text("+ Add Custom Course", fontWeight = FontWeight.SemiBold)
        }

        if (showAddDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Custom Course") },
                text = { Text("This feature requires a persistent database and will be available in a future update.") },
                confirmButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("OK") }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
