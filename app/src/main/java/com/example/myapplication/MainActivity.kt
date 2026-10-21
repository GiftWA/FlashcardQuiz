package com.example.myapplication

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
import com.example.myapplication.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    var currentScreen by remember { mutableStateOf("splash") }
    var selectedCourse by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("study") }
    var quizScore by remember { mutableStateOf(0) }
    var quizTotal by remember { mutableStateOf(0) }

    var currentTab by remember { mutableStateOf(AppDestination.HOME) }
    // ---- Shared saved state (in-memory for this session) ----
    // Keys are "topic" strings for saved quizzes and flashcards.
    val savedQuizTopics = remember { mutableStateListOf<String>() }
    val savedFlashcardTopics = remember { mutableStateListOf<String>() }
    // Note = Triple(topic, cardIndex, text)
    val savedNotes = remember { mutableStateListOf<Triple<String, Int, String>>() }

    val secondaryScreens = setOf("topics", "flashcards", "quiz", "result", "about")
    val isSecondary = currentScreen in secondaryScreens

    if (currentScreen == "splash") {
        SplashScreen(onFinished = { currentScreen = "home" })
        return
    }

    if (isSecondary) {
        val title = when (currentScreen) {
            "topics"     -> "Deck Overview"
            "flashcards" -> "Flashcard Session"
            "quiz"       -> "Quiz Runner"
            "result"     -> "Quiz Result"
            "about"      -> "About"
            else         -> ""
        }
        val onBack: () -> Unit = {
            currentScreen = when (currentScreen) {
                "topics"     -> "home"
                "flashcards" -> "topics"
                "quiz"       -> "topics"
                "result"     -> "home"
                "about"      -> "home"
                else         -> "home"
            }
        }

        Scaffold(
            topBar = { AppTopBarWithBack(title = title, onBack = onBack) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    "about" -> AboutScreen(onBackClick = onBack)
                    "topics" -> TopicSelectionScreen(
                        course = selectedCourse,
                        onTopicStudy = { topic ->
                            selectedTopic = topic
                            currentScreen = "flashcards"
                        },
                        onTopicQuiz = { topic ->
                            selectedTopic = topic
                            currentScreen = "quiz"
                        },
                        onBackClick = onBack
                    )
                    "flashcards" -> FlashcardScreen(
                        course = selectedCourse,
                        topic = selectedTopic,
                        onBackClick = onBack,
                        onAddNote = { cardIndex, text ->
                            savedNotes.add(Triple(selectedTopic, cardIndex, text))
                        },
                        onMarkFlashcardsSaved = {
                            if (!savedFlashcardTopics.contains(selectedTopic)) {
                                savedFlashcardTopics.add(selectedTopic)
                            }
                        }
                    )
                    "quiz" -> QuizScreen(
                        course = selectedCourse,
                        topic = selectedTopic,
                        onBackClick = onBack,
                        onQuizFinished = { score, total ->
                            quizScore = score
                            quizTotal = total
                            currentScreen = "result"
                        }
                    )
                    "result" -> ResultScreen(
                        score = quizScore,
                        total = quizTotal,
                        onTryAgain = { currentScreen = "quiz" },
                        onBackToHome = { currentScreen = "home" }
                    )
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = when (currentTab) {
                    AppDestination.HOME    -> "Home"
                    AppDestination.STUDY   -> "Study"
                    AppDestination.QUIZZES -> "Quizzes"
                    AppDestination.SAVED   -> "Saved"
                    AppDestination.PROFILE -> "Profile"
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                current = currentTab,
                onSelect = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                AppDestination.HOME -> HomeScreen(
                    onStudyClick = {
                        mode = "study"
                        currentTab = AppDestination.STUDY
                    },
                    onQuizClick = {
                        mode = "quiz"
                        currentTab = AppDestination.STUDY
                    },
                    onAboutClick = { currentScreen = "about" },
                    onCourseSelected = { course ->
                        selectedCourse = course
                        currentScreen = "topics"
                    }
                )
                AppDestination.STUDY -> CourseSelectionScreen(
                    onCourseClick = { course ->
                        selectedCourse = course
                        currentScreen = "topics"
                    },
                    onBackClick = { currentTab = AppDestination.HOME }
                )
                AppDestination.QUIZZES -> QuizzesTabScreen(
                    onStartQuiz = { course, topic ->
                        selectedCourse = course
                        selectedTopic = topic
                        currentScreen = "quiz"
                    },
                    savedTopics = savedQuizTopics,
                    onToggleSave = { topic ->
                        if (savedQuizTopics.contains(topic)) {
                            savedQuizTopics.remove(topic)
                        } else {
                            savedQuizTopics.add(topic)
                        }
                    }
                )
                AppDestination.SAVED -> SavedTabScreen(
                    savedQuizTopics = savedQuizTopics,
                    savedFlashcardTopics = savedFlashcardTopics,
                    savedNotes = savedNotes,
                    onStartQuiz = { course, topic ->
                        selectedCourse = course
                        selectedTopic = topic
                        currentScreen = "quiz"
                    },
                    onOpenFlashcardTopic = { topic ->
                        selectedTopic = topic
                        currentScreen = "flashcards"
                    }
                )
                AppDestination.PROFILE -> ProfileTabScreen(
                    onAboutClick = { currentScreen = "about" }
                )
            }
        }
    }
}
