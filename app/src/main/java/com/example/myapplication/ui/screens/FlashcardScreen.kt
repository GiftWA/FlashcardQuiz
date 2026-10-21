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
fun FlashcardScreen(
    course: String,
    topic: String,
    onBackClick: () -> Unit,
    onAddNote: (Int, String) -> Unit,
    onMarkFlashcardsSaved: () -> Unit
) {
    val cards = getFlashcards(topic)

    val notes = remember { mutableStateListOf<Pair<Int, String>>() }

    var showNoteSheet by remember { mutableStateOf(false) }
    var noteTargetIndex by remember { mutableStateOf(0) }
    var noteDraft by remember { mutableStateOf("") }

    val total = cards.size
    val completed = total
    val progress = if (total == 0) 0f else 1f

    // Register this topic as having saved flashcards (once per visit).
    LaunchedEffect(topic) {
        onMarkFlashcardsSaved()
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
                        text = course.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "$total Cards",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandBlue,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE3F2FD))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = topic,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Master the key concepts, definitions and examples for this topic.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF52606D)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Card $completed of $total",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF1F2933)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandBlue
                    )
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
                            .fillMaxWidth(progress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BrandBlue)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        cards.forEachIndexed { index, card ->
            FlashcardItem(
                index = index,
                question = card.question,
                answer = card.answer,
                onEditNote = {
                    noteTargetIndex = index
                    noteDraft = ""
                    showNoteSheet = true
                }
            )
            if (index < cards.size - 1) Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                noteTargetIndex = 0
                noteDraft = ""
                showNoteSheet = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue,
                contentColor = Color.White
            )
        ) {
            Text(text = "Add Note", style = MaterialTheme.typography.labelLarge)

        }
    }

    if (showNoteSheet) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showNoteSheet = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "My Notes",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1F2933),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "For Card ${"%02d".format(noteTargetIndex + 1)} of $topic",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D)
                    )

                    Spacer(Modifier.height(12.dp))

                    androidx.compose.foundation.text.BasicTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF1F2933),
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F7FA))
                            .padding(12.dp)
                            .height(80.dp),
                        decorationBox = { inner ->
                            if (noteDraft.isEmpty()) {
                                Text(
                                    text = "Type your revision notes here...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF52606D)
                                )
                            }
                            inner()
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            modifier = Modifier
                                .clickable { showNoteSheet = false }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Button(
                            onClick = {
                                if (noteDraft.isNotBlank()) {
                                    notes.add(noteTargetIndex to noteDraft)
                                    onAddNote(noteTargetIndex, noteDraft)
                                }
                                showNoteSheet = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Save", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    if (notes.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Saved notes (${notes.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(6.dp))
                        notes.takeLast(3).forEach { (idx, text) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Card ${"%02d".format(idx + 1)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1F2933)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
