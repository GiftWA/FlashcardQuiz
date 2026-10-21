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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.ui.components.CourseGridCard
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
@Composable
fun ProfileTabScreen(
    onAboutClick: () -> Unit
) {
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
                                text = "GS",
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
                            text = "Guest Student",
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
                            text = "Computer Science Track",
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
                            .clickable { }
                            .padding(6.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Academic term bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F7FA))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Systems & Database Track",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF1F2933),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Semester IV",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D)
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
                text = "All Time Stat",
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
                value = "24",
                label = "Quizzes Taken",
                modifier = Modifier.weight(1f)
            )
            ProfileStatTile(
                value = "82%",
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
                value = "186",
                label = "Cards Studied",
                modifier = Modifier.weight(1f)
            )
            ProfileStatTile(
                value = "7 Days",
                label = "Current Streak",
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
            SmallTextLink(text = "View All", onClick = { })
        }

        Spacer(Modifier.height(10.dp))

        // Activity 1
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
                        text = "Completed Quiz: ER Diagrams",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF1F2933)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Score: 90% • CS-204 Database Systems",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D)
                    )
                }
                Text(
                    text = "Yesterday",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Activity 2
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
                    Text(text = "📖", fontSize = 16.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Studied 12 Flashcards: Relational Model",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF1F2933)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Mastery: +15% • Normal Forms & Schemas",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D)
                    )
                }
                Text(
                    text = "2 days ago",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF52606D)
                )
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
                    onClick = { }
                )
                SettingsRow(
                    icon = "ℹ",
                    title = "About FlashcardQuiz",
                    subtitle = "Version 2.4.0 • Academic Build",
                    onClick = onAboutClick
                )
                SettingsRow(
                    icon = "?",
                    title = "Help & Guidelines",
                    subtitle = "Spaced repetition FAQ & support",
                    onClick = { }
                )
                SettingsRow(
                    icon = "⏻",
                    title = "Log Out",
                    subtitle = "End session on this workstation",
                    isDestructive = true,
                    onClick = { }
                )
            }
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
@Composable
private fun PlaceholderTab(name: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2933)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Coming soon",
            fontSize = 16.sp,
            color = Color(0xFF52606D)
        )
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var started by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "splashFade"
    )

    LaunchedEffect(Unit) {
        started = true
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
    ) {
        // Top-right "CS Recall" pill
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE3F2FD))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1565C0))
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "CS Recall",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1565C0)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo block — using text as a stand-in for the Stitch asset
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1565C0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FQ",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "FlashcardQuiz",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Computer Science Revision",
                fontSize = 15.sp,
                color = Color(0xFF52606D),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // Preparing modules progress card
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Preparing modules",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF52606D)
                        )
                        Text(
                            text = "100%",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFD9E3F1))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1565C0))
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Ready.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D)
                    )
                }
            }
        }

        // Bottom version pill + tagline
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "v1.0 • Academic Edition",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF52606D),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Algorithms • Systems • Architecture",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF52606D)
            )
        }
    }
}

// One course shown on the Home screen
private data class HomeCourse(
    val name: String,
    val category: String,
    val difficulty: String,
    val icon: ImageVector
)

private val courseItems = listOf(
    HomeCourse("Database Management", "Practical", "Intermediate", Icons.Outlined.Storage),
    HomeCourse("Computer Networks", "Theoretical", "Advanced", Icons.Outlined.Hub),
    HomeCourse("Operating Systems", "Theoretical", "Intermediate", Icons.Outlined.Memory),
    HomeCourse("Mobile Application Development", "Practical", "Beginner", Icons.Outlined.PhoneAndroid)
)

@Composable
fun HomeScreen(
    onStudyClick: () -> Unit,
    onQuizClick: () -> Unit,
    onAboutClick: () -> Unit,
    onCourseSelected: (String) -> Unit
) {
    var selectedChip by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val allCourses = listOf(
        Triple("Database Management", "Practical", 4),
        Triple("Computer Networks", "Theoretical", 4),
        Triple("Operating Systems", "Theoretical", 4),
        Triple("Mobile Application Development", "Practical", 4)
    )

    val searchResults = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        allCourses.filter { it.first.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Greeting banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                    val greeting = when {
                        hour < 12 -> "Good morning"
                        hour < 17 -> "Good afternoon"
                        else      -> "Good evening"
                    }
                    Text(
                        text = greeting,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ready to revise today?",
                        fontSize = 14.sp,
                        color = Color(0xFFDAE5FF)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 18.sp,
                        color = Color(0xFF1565C0)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                                text = "Search courses and quizzes",
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
                            .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                    )
                }
            }
        }

        // Live results panel
        if (searchQuery.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    if (searchResults.isEmpty()) {
                        Text(
                            text = "No matches for \"$searchQuery\"",
                            fontSize = 13.sp,
                            color = Color(0xFF52606D),
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        searchResults.forEach { (title, category, _) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        searchQuery = ""
                                        onCourseSelected(title)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0xFFE3F2FD))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1F2933)
                                    )
                                    Text(
                                        text = category,
                                        fontSize = 11.sp,
                                        color = Color(0xFF52606D)
                                    )
                                }
                                Text(
                                    text = "→",
                                    fontSize = 16.sp,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Continue Studying
        Text(
            text = "Continue Studying",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2933)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStudyClick() },
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE3F2FD))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Database Management",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2933)
                        )
                        Text(
                            text = "Relational Model • Card 8 of 12",
                            fontSize = 12.sp,
                            color = Color(0xFF52606D)
                        )
                    }
                    Text(
                        text = "Resume",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1565C0)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mastery Pace", fontSize = 11.sp, color = Color(0xFF52606D))
                    Text("65%", fontSize = 11.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFD9E3F1))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1565C0))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatTile(value = "14", label = "Quizzes taken", modifier = Modifier.weight(1f))
            StatTile(value = "82%", label = "Average score", modifier = Modifier.weight(1f))
            StatTile(value = "128", label = "Cards studied", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Filter chips
        FilterChipRow(
            selected = selectedChip,
            onSelected = { selectedChip = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Explore Courses header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore Courses",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2933)
            )
            SmallTextLink(text = "View all", onClick = { onStudyClick() })
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Course cards, two per row. Every card has the same fixed height.
        courseItems.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { course ->
                    CourseGridCard(
                        title = course.name,
                        category = course.category,
                        topicCount = 4,
                        difficulty = course.difficulty,
                        icon = course.icon,
                        onClick = { onCourseSelected(course.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TonalButton(
                text = "Take Quiz",
                onClick = onQuizClick,
                modifier = Modifier.weight(1f)
            )
            TonalButton(
                text = "About",
                onClick = onAboutClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // ---- App identity card ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1565C0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FQ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "FlashcardQuiz",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Computer Science Revision",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF52606D)
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE3F2FD))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1565C0))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Version 1.0.0 (Academic Build)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- About the App ----
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
                Text(
                    text = "About the App",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "FlashcardQuiz is an Android application designed to help Computer Science students revise core academic modules efficiently using structured flashcards and multiple-choice quizzes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF52606D)
                )
                Spacer(Modifier.height(12.dp))
                PillarRow(
                    "Active Recall",
                    "Retrieval-focused drills engineered to strengthen memory."
                )
                Spacer(Modifier.height(8.dp))
                PillarRow(
                    "Spaced Repetition",
                    "Optimised review intervals based on memory decay modelling."
                )
                Spacer(Modifier.height(8.dp))
                PillarRow(
                    "Self-Paced Revision",
                    "Targeted module review tailored to your exam prep schedule."
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Covered Courses ----
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
                        text = "Covered Courses",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1F2933),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "4 Modules",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF52606D),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDFE9F7))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                CourseRow(1, "Database Management", "Practical")
                Spacer(Modifier.height(6.dp))
                CourseRow(2, "Computer Networks", "Theoretical")
                Spacer(Modifier.height(6.dp))
                CourseRow(3, "Operating Systems", "Theoretical")
                Spacer(Modifier.height(6.dp))
                CourseRow(4, "Mobile Application Development", "Practical")
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Technology Stack ----
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
                Text(
                    text = "Technology Stack",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2933),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))

                TechChipRow(listOf("Kotlin", "Jetpack Compose", "Material Design 3"))
                Spacer(Modifier.height(8.dp))
                TechChipRow(listOf("Kotlin Coroutines", "Gemini Flash API"))
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Peer-Reviewed Curricula banner ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛡", fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Peer-Reviewed Curricula",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF1F2933),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "All card algorithms and protocols conform to standard departmental syllabi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF52606D)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

            Text(
                text = "Designed for University CS Students • 2026",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF52606D),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
                    Spacer(Modifier.height(24.dp))
    }
}
@Composable
private fun PillarRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F7FA))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1565C0))
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF1F2933),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF52606D)
            )
        }
    }
}

@Composable
private fun CourseRow(index: Int, name: String, category: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F7FA))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1565C0),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF1F2933),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = if (category == "Practical") Color(0xFF1565C0) else Color(0xFF52606D),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (category == "Practical") Color(0xFFE3F2FD) else Color(0xFFDFE9F7)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun TechChipRow(chips: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            Text(
                text = chip,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF1F2933),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F7FA))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

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

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

@Composable
fun QuizScreen(
    course: String,
    topic: String,
    onBackClick: () -> Unit,
    onQuizFinished: (Int, Int) -> Unit
) {
    // Local state
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }

    // Simulated loading state (future Gemini API will actually load here)
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Brief simulated load
    LaunchedEffect(topic) {
        isLoading = true
        hasError = false
        delay(700)
        isLoading = false
    }

    val questions = getQuizQuestions(topic)

    // ---- LOADING STATE ----
    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular spinner substitute
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BlueTint),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⋯",
                    fontSize = 32.sp,
                    color = BrandBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Preparing your quiz…",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Retrieving questions from module library",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Syncing: $topic • Q1–Q${questions.size}",
                style = MaterialTheme.typography.labelSmall,
                color = BrandBlue,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        return
    }

    // ---- ERROR STATE ----
    if (hasError) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFDAD6)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "!", fontSize = 32.sp, color = Color(0xFFBA1A1A), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Couldn't load the quiz",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Check your connection and try again.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    hasError = false
                    isLoading = true
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
                Text("Retry Connection", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, BrandBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
            ) {
                Text("Back", style = MaterialTheme.typography.labelLarge)
            }
        }
        return
    }

    // ---- QUESTION STATE ----
    val currentQuestion = questions[currentIndex]
    val isLastQuestion = currentIndex == questions.size - 1
    val letters = listOf("A", "B", "C", "D")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${currentIndex + 1} of ${questions.size}",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = "Active Session",
                style = MaterialTheme.typography.labelSmall,
                color = BrandBlue,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFD9E3F1))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((currentIndex + 1).toFloat() / questions.size)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandBlue)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "$topic • $course",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandBlue,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE3F2FD))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = currentQuestion.question,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }


        Spacer(Modifier.height(16.dp))

        // Options
        currentQuestion.options.forEachIndexed { i, option ->
            QuizOptionTile(
                letter = letters.getOrElse(i) { "?" },
                text = option,
                selected = option == selectedAnswer,
                onClick = { selectedAnswer = option }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(12.dp))

        // Next / Finish
        Button(
            onClick = {
                var newScore = score
                if (selectedAnswer == currentQuestion.correctAnswer) {
                    newScore = score + 1
                }
                if (isLastQuestion) {
                    onQuizFinished(newScore, questions.size)
                } else {
                    score = newScore
                    currentIndex = currentIndex + 1
                    selectedAnswer = ""
                }
            },
            enabled = selectedAnswer.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (isLastQuestion) "Finish Quiz" else "Next",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ResultScreen(
    score: Int,
    total: Int,
    onTryAgain: () -> Unit,
    onBackToHome: () -> Unit
) {
    val safeTotal = if (total == 0) 1 else total
    val percent = (score * 100) / safeTotal

    val message = when {
        percent >= 90 -> "Excellent work!"
        percent >= 70 -> "Great job!"
        percent >= 50 -> "Good effort!"
        else          -> "Keep practising!"
    }

    val subtitle = when {
        percent >= 90 -> "You've mastered this topic."
        percent >= 70 -> "You've got a strong grasp of the fundamentals."
        percent >= 50 -> "A solid attempt — review and try again."
        else          -> "Review the flashcards and try again."
    }

    // Session timer — starts when this screen appears.
    var secondsElapsed by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            secondsElapsed += 1
        }
    }
    val minutes = secondsElapsed / 60
    val seconds = secondsElapsed % 60
    val studyTimeText = if (minutes == 0) "$seconds secs" else "$minutes mins $seconds secs"

    // Save-quiz toggle (in-memory only for now)
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Trophy icon in a tinted circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🏆", fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Quiz Complete",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Radial score ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(160.dp)) {
                val strokeWidth = 14.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = androidx.compose.ui.geometry.Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

                // Background track
                drawArc(
                    color = Color(0xFFD9E3F1),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )

                // Foreground progress
                val sweep = 360f * (score.toFloat() / safeTotal.toFloat())
                drawArc(
                    color = Color(0xFF1565C0),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score / $total",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Text(
                    text = "Score: $percent%",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Performance summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Card header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F7FA))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FOCUS DOMAIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = message,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                    }
                }

                // Rows
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    SummaryRow(icon = "✓", label = "Correct answers", value = "$score questions", positive = true)
                    Spacer(Modifier.height(10.dp))
                    SummaryRow(icon = "!", label = "Incorrect answers", value = "${safeTotal - score} question" + (if (safeTotal - score == 1) "" else "s"), positive = false)
                    Spacer(Modifier.height(10.dp))
                    SummaryRow(icon = "%", label = "Accuracy", value = "$percent%", positive = true)
                    Spacer(Modifier.height(10.dp))
                    SummaryRow(icon = "⏱", label = "Study Time", value = studyTimeText, positive = false)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Try Again (primary)
        Button(
            onClick = onTryAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "⟳  Try Again",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Save Quiz (tonal)
        Button(
            onClick = { isSaved = !isSaved },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSaved) Color(0xFFD9E3F1) else Color(0xFFE3F2FD),
                contentColor = Color(0xFF1565C0)
            )
        ) {
            Text(
                text = if (isSaved) "✓  Quiz Saved" else "🔖  Save Quiz",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Back to Home (text link)
        Text(
            text = "Back to Home",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            modifier = Modifier
                .clickable { onBackToHome() }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SummaryRow(
    icon: String,
    label: String,
    value: String,
    positive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (positive) Color(0xFFE3F2FD) else Color(0xFFEFF3F7)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 13.sp,
                color = if (positive) Color(0xFF1565C0) else Color(0xFF52606D),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1F2933),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = if (positive) Color(0xFF1565C0) else Color(0xFF1F2933),
            fontWeight = if (positive) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
@Composable
fun HomeButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1565C0),
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

@Composable
fun BackButton(text: String = "Back", onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color(0xFF1565C0)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF1565C0)
        )
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

fun getTopics(course: String): List<String> {
    return when (course) {
        "Database Management" -> listOf(
            "Database Fundamentals", "ER Diagrams", "Relational Model", "Normalization"
        )
        "Computer Networks" -> listOf(
            "Network Fundamentals", "OSI Model", "TCP/IP", "Network Security"
        )
        "Operating Systems" -> listOf(
            "Process Management", "Memory Management", "File Systems", "CPU Scheduling"
        )
        "Mobile Application Development" -> listOf(
            "Android Fundamentals", "Activities and Lifecycle", "Jetpack Compose", "Android UI Design"
        )
        else -> emptyList()
    }
}

data class Flashcard(val question: String, val answer: String)

fun getFlashcards(topic: String): List<Flashcard> {
    return when (topic) {
        "Database Fundamentals" -> listOf(
            Flashcard("What is a database?", "An organised collection of data that can be easily stored, accessed and managed."),
            Flashcard("What is a DBMS?", "A Database Management System, the software used to create, manage and control access to databases."),
            Flashcard("What is a primary key?", "A column (or set of columns) that uniquely identifies each row in a table."),
            Flashcard("What is SQL?", "Structured Query Language, used to create, read, update and delete data in a relational database.")
        )
        "ER Diagrams" -> listOf(
            Flashcard("What is an ER diagram?", "A diagram that shows the entities in a database and the relationships between them."),
            Flashcard("What is an entity?", "A thing about which data is stored, such as Student or Course."),
            Flashcard("What is an attribute?", "A property that describes an entity, such as a student's name."),
            Flashcard("What is cardinality?", "It describes how many instances of one entity can relate to another: one-to-one, one-to-many or many-to-many.")
        )
        "Relational Model" -> listOf(
            Flashcard("What is a relation?", "A table made up of rows and columns."),
            Flashcard("What is a tuple?", "A single row in a relation."),
            Flashcard("What is a foreign key?", "A column that refers to the primary key of another table in order to link them."),
            Flashcard("What is a schema?", "The structure of a database, including its tables, columns and data types.")
        )
        "Normalization" -> listOf(
            Flashcard("What is normalization?", "Organising data into tables to reduce redundancy and improve data integrity."),
            Flashcard("What does First Normal Form (1NF) require?", "Each column holds a single value and there are no repeating groups."),
            Flashcard("What does Second Normal Form (2NF) require?", "The table is in 1NF and every non-key column depends on the whole primary key."),
            Flashcard("What does Third Normal Form (3NF) require?", "The table is in 2NF and no non-key column depends on another non-key column.")
        )
        "Network Fundamentals" -> listOf(
            Flashcard("What is a network?", "Two or more devices connected together to share data and resources."),
            Flashcard("What is a router?", "A device that forwards data between different networks."),
            Flashcard("What is an IP address?", "A unique number that identifies a device on a network."),
            Flashcard("What is the difference between a LAN and a WAN?", "A LAN covers a small area such as a building, while a WAN covers a large area such as a country.")
        )
        "OSI Model" -> listOf(
            Flashcard("How many layers does the OSI model have?", "Seven."),
            Flashcard("Name the seven OSI layers.", "Physical, Data Link, Network, Transport, Session, Presentation and Application."),
            Flashcard("Which layer is responsible for routing?", "The Network layer."),
            Flashcard("Which layer provides end-to-end delivery of data?", "The Transport layer.")
        )
        "TCP/IP" -> listOf(
            Flashcard("What does TCP do?", "It provides reliable, ordered delivery of data between devices."),
            Flashcard("What does IP do?", "It addresses and routes packets so they reach their destination."),
            Flashcard("What is the difference between TCP and UDP?", "TCP is reliable and connection-based, while UDP is faster but does not guarantee delivery."),
            Flashcard("What is DNS?", "The Domain Name System, which translates names like google.com into IP addresses.")
        )
        "Network Security" -> listOf(
            Flashcard("What is a firewall?", "A system that filters network traffic based on security rules."),
            Flashcard("What is encryption?", "Converting data into a coded form so only authorised people can read it."),
            Flashcard("What is phishing?", "Tricking people into giving away sensitive information using fake emails or websites."),
            Flashcard("What does HTTPS do?", "It encrypts the communication between a browser and a website.")
        )
        "Process Management" -> listOf(
            Flashcard("What is a process?", "A program that is currently running."),
            Flashcard("What is a Process Control Block (PCB)?", "A data structure that stores information about a process, such as its ID and state."),
            Flashcard("Name the common process states.", "New, Ready, Running, Waiting and Terminated."),
            Flashcard("What is context switching?", "Saving the state of one process and loading another so the CPU can switch between them.")
        )
        "Memory Management" -> listOf(
            Flashcard("What is paging?", "Dividing memory into fixed-size blocks called pages and frames to use memory efficiently."),
            Flashcard("What is virtual memory?", "A technique that uses disk space to extend RAM so programs can be larger than physical memory."),
            Flashcard("What is a page fault?", "It happens when a program accesses a page that is not currently in RAM."),
            Flashcard("What is fragmentation?", "Wasted memory that is split into small pieces that are too small to use.")
        )
        "File Systems" -> listOf(
            Flashcard("What is a file system?", "The method an operating system uses to store, organise and retrieve files on a storage device."),
            Flashcard("What is a directory?", "A folder that holds files and other directories."),
            Flashcard("Give three examples of file systems.", "NTFS, FAT32 and ext4."),
            Flashcard("What are file permissions?", "Rules that control who can read, write or execute a file.")
        )
        "CPU Scheduling" -> listOf(
            Flashcard("What is CPU scheduling?", "Deciding which process in the ready queue gets the CPU next."),
            Flashcard("How does First Come First Served (FCFS) work?", "Processes run in the order in which they arrive."),
            Flashcard("How does Round Robin scheduling work?", "Each process gets a fixed time slice, one after another."),
            Flashcard("What is turnaround time?", "The total time from when a process is submitted until it completes.")
        )
        "Android Fundamentals" -> listOf(
            Flashcard("Which programming language is recommended for Android development?", "Kotlin."),
            Flashcard("What is an APK?", "An Android Package file used to install an app on a device."),
            Flashcard("What does the AndroidManifest.xml file do?", "It declares the app's components, permissions and basic information."),
            Flashcard("What is an Activity?", "A single screen of an app that has a user interface.")
        )
        "Activities and Lifecycle" -> listOf(
            Flashcard("What is the activity lifecycle?", "The set of states an activity moves through from creation to destruction."),
            Flashcard("Which method is called when an activity is first created?", "onCreate()."),
            Flashcard("Which method is called when an activity becomes visible to the user?", "onStart()."),
            Flashcard("What does onDestroy() do?", "It is called before the activity is destroyed, so resources can be cleaned up.")
        )
        "Jetpack Compose" -> listOf(
            Flashcard("What is Jetpack Compose?", "Android's modern toolkit for building native user interfaces using Kotlin functions."),
            Flashcard("What does @Composable mean?", "It marks a function that describes part of the user interface."),
            Flashcard("What is state in Compose?", "A value that can change and causes the screen to update when it changes."),
            Flashcard("What does remember do?", "It keeps a value so it is not lost when the screen is redrawn.")
        )
        "Android UI Design" -> listOf(
            Flashcard("What is Material Design?", "Google's design system with guidelines and components for building consistent apps."),
            Flashcard("Why do we use dp for sizes?", "Density-independent pixels keep sizes consistent across different screens."),
            Flashcard("What does a Column do in Compose?", "It places its children vertically, one after another."),
            Flashcard("Why is consistent spacing important?", "It makes the interface neat, balanced and easy to read.")
        )
        else -> listOf(
            Flashcard("No flashcards available.", "Please choose another topic.")
        )
    }
}

fun getQuizQuestions(topic: String): List<QuizQuestion> {
    return when (topic) {
        "Database Fundamentals" -> listOf(
            QuizQuestion("What is a primary key?", listOf("A key that uniquely identifies each row", "A key used only for sorting", "A key used to delete a database", "A key used to connect to Wi-Fi"), "A key that uniquely identifies each row"),
            QuizQuestion("What does DBMS stand for?", listOf("Data Backup Management System", "Database Management System", "Digital Browser Management Software", "Database Memory Storage"), "Database Management System"),
            QuizQuestion("What is SQL used for?", listOf("Designing mobile app screens", "Connecting to Wi-Fi networks", "Creating and managing data in a database", "Editing photos"), "Creating and managing data in a database"),
            QuizQuestion("What is a database table made of?", listOf("Folders and files", "Pages and frames", "Nodes and routers", "Rows and columns"), "Rows and columns")
        )
        "ER Diagrams" -> listOf(
            QuizQuestion("What does an ER diagram show?", listOf("Entities and the relationships between them", "The speed of a network", "The steps of a program", "The layers of an operating system"), "Entities and the relationships between them"),
            QuizQuestion("In an ER diagram, what is an entity?", listOf("A type of network cable", "A thing about which data is stored", "A programming language", "A kind of database password"), "A thing about which data is stored"),
            QuizQuestion("What is an attribute?", listOf("A link between two networks", "A type of file system", "A property that describes an entity", "A database backup"), "A property that describes an entity"),
            QuizQuestion("What does cardinality describe?", listOf("How fast a query runs", "How big a hard disk is", "How many users can log in", "How many instances of one entity relate to another"), "How many instances of one entity relate to another")
        )
        "Relational Model" -> listOf(
            QuizQuestion("What is a tuple?", listOf("A single row in a table", "A single column in a table", "A database password", "A type of network"), "A single row in a table"),
            QuizQuestion("What is a relation in the relational model?", listOf("A link between two computers", "A table of rows and columns", "A type of programming loop", "A user account"), "A table of rows and columns"),
            QuizQuestion("What is a foreign key?", listOf("A key that deletes a table", "A key used to encrypt Wi-Fi", "A column that refers to the primary key of another table", "A key that sorts data alphabetically"), "A column that refers to the primary key of another table"),
            QuizQuestion("What is a database schema?", listOf("A backup of a database", "A type of query", "A database user", "The structure of a database"), "The structure of a database")
        )
        "Normalization" -> listOf(
            QuizQuestion("What is the main purpose of normalization?", listOf("To reduce data redundancy", "To make a database larger", "To speed up the internet", "To encrypt data"), "To reduce data redundancy"),
            QuizQuestion("What does First Normal Form (1NF) require?", listOf("Every table has exactly three columns", "Each column holds a single value", "No table has a primary key", "All data is stored in one table"), "Each column holds a single value"),
            QuizQuestion("What does Second Normal Form (2NF) require?", listOf("Every table has two rows", "No table has a foreign key", "Every non-key column depends on the whole primary key", "All columns are numbers"), "Every non-key column depends on the whole primary key"),
            QuizQuestion("What does Third Normal Form (3NF) require?", listOf("Every table has three columns", "All data is stored in one column", "Every table has three keys", "No non-key column depends on another non-key column"), "No non-key column depends on another non-key column")
        )
        "Network Fundamentals" -> listOf(
            QuizQuestion("What does a router do?", listOf("Forwards data between networks", "Stores web pages", "Prints documents", "Cools the computer"), "Forwards data between networks"),
            QuizQuestion("What is an IP address?", listOf("A type of cable", "A unique number that identifies a device on a network", "A web browser", "A password for Wi-Fi"), "A unique number that identifies a device on a network"),
            QuizQuestion("What area does a LAN usually cover?", listOf("An entire country", "Only one computer", "A small area such as a building", "The whole world"), "A small area such as a building"),
            QuizQuestion("What is a network?", listOf("A single computer with a screen", "A type of software license", "A kind of keyboard", "Two or more devices connected to share data"), "Two or more devices connected to share data")
        )
        "OSI Model" -> listOf(
            QuizQuestion("How many layers does the OSI model have?", listOf("Seven", "Four", "Five", "Nine"), "Seven"),
            QuizQuestion("Which layer is responsible for routing?", listOf("Physical layer", "Network layer", "Application layer", "Session layer"), "Network layer"),
            QuizQuestion("Which layer provides end-to-end delivery of data?", listOf("Presentation layer", "Data Link layer", "Transport layer", "Physical layer"), "Transport layer"),
            QuizQuestion("Which layer deals with cables and electrical signals?", listOf("Application layer", "Transport layer", "Session layer", "Physical layer"), "Physical layer")
        )
        "TCP/IP" -> listOf(
            QuizQuestion("What does TCP provide?", listOf("Reliable, ordered delivery of data", "Faster delivery without any checking", "Screen display", "Image editing"), "Reliable, ordered delivery of data"),
            QuizQuestion("What does IP do?", listOf("Stores emails", "Addresses and routes packets to their destination", "Draws web pages", "Compresses images"), "Addresses and routes packets to their destination"),
            QuizQuestion("How does UDP compare with TCP?", listOf("It is slower and more reliable", "It is used only for printers", "It is faster but does not guarantee delivery", "It is used only for databases"), "It is faster but does not guarantee delivery"),
            QuizQuestion("What does DNS do?", listOf("Blocks viruses", "Stores passwords", "Sends emails", "Translates domain names into IP addresses"), "Translates domain names into IP addresses")
        )
        "Network Security" -> listOf(
            QuizQuestion("What does a firewall do?", listOf("Filters network traffic using security rules", "Speeds up downloads", "Stores backups", "Creates new IP addresses"), "Filters network traffic using security rules"),
            QuizQuestion("What is encryption?", listOf("Deleting old files", "Converting data into a coded form", "Making a copy of data", "Increasing network speed"), "Converting data into a coded form"),
            QuizQuestion("What is phishing?", listOf("Fixing a network fault", "A way to speed up Wi-Fi", "Tricking people into revealing sensitive information", "A type of backup"), "Tricking people into revealing sensitive information"),
            QuizQuestion("What does HTTPS do?", listOf("Makes websites load images faster", "Stores cookies permanently", "Blocks all advertisements", "Encrypts communication between a browser and a website"), "Encrypts communication between a browser and a website")
        )
        "Process Management" -> listOf(
            QuizQuestion("What is a process?", listOf("A program that is currently running", "A file stored on disk", "A type of hardware", "A network cable"), "A program that is currently running"),
            QuizQuestion("What does a Process Control Block (PCB) store?", listOf("The user's passwords", "Information about a process, such as its ID and state", "The list of all files on the disk", "The computer's IP address"), "Information about a process, such as its ID and state"),
            QuizQuestion("Which of these is a common process state?", listOf("Typing", "Printing", "Running", "Browsing"), "Running"),
            QuizQuestion("What is context switching?", listOf("Changing the screen brightness", "Switching a computer off", "Copying a file to a USB drive", "Saving one process's state and loading another's"), "Saving one process's state and loading another's")
        )
        "Memory Management" -> listOf(
            QuizQuestion("What is paging?", listOf("Dividing memory into fixed-size blocks", "Deleting unused programs", "Increasing screen size", "Connecting to the internet"), "Dividing memory into fixed-size blocks"),
            QuizQuestion("What is virtual memory?", listOf("A faster type of CPU", "Disk space used to extend RAM", "A kind of monitor", "Memory inside the keyboard"), "Disk space used to extend RAM"),
            QuizQuestion("What is a page fault?", listOf("A broken page on a website", "An error in the keyboard", "Accessing a page that is not currently in RAM", "A full hard disk"), "Accessing a page that is not currently in RAM"),
            QuizQuestion("What is fragmentation?", listOf("Extra memory added to RAM", "A way to compress files", "A type of virus", "Wasted memory split into small unusable pieces"), "Wasted memory split into small unusable pieces")
        )
        "File Systems" -> listOf(
            QuizQuestion("What is the purpose of a file system?", listOf("To store, organise and retrieve files", "To draw graphics on the screen", "To connect to the internet", "To cool the CPU"), "To store, organise and retrieve files"),
            QuizQuestion("What is a directory?", listOf("A type of virus", "A folder that holds files and other folders", "A kind of processor", "A network protocol"), "A folder that holds files and other folders"),
            QuizQuestion("Which of these is an example of a file system?", listOf("HTTP", "HTML", "NTFS", "Bluetooth"), "NTFS"),
            QuizQuestion("What do file permissions do?", listOf("Decide the colour of a file", "Increase the size of a file", "Speed up the CPU", "Control who can read, write or execute a file"), "Control who can read, write or execute a file")
        )
        "CPU Scheduling" -> listOf(
            QuizQuestion("What does CPU scheduling decide?", listOf("Which process gets the CPU next", "Which website to open", "How much a computer costs", "Which file to delete"), "Which process gets the CPU next"),
            QuizQuestion("How does First Come First Served (FCFS) work?", listOf("The shortest process always runs first", "Processes run in the order they arrive", "Processes run in random order", "The largest process always runs first"), "Processes run in the order they arrive"),
            QuizQuestion("How does Round Robin scheduling work?", listOf("Only the longest process runs", "Processes are never switched", "Each process gets a fixed time slice in turn", "Only the newest process runs"), "Each process gets a fixed time slice in turn"),
            QuizQuestion("What is turnaround time?", listOf("The time the CPU is idle", "The time a computer takes to start", "The time taken to type a command", "The total time from submission to completion"), "The total time from submission to completion")
        )
        "Android Fundamentals" -> listOf(
            QuizQuestion("Which programming language is recommended for Android development?", listOf("Kotlin", "COBOL", "Fortran", "Pascal"), "Kotlin"),
            QuizQuestion("What is an APK?", listOf("A type of phone battery", "A file used to install an Android app", "A programming language", "A kind of emulator"), "A file used to install an Android app"),
            QuizQuestion("What does the AndroidManifest.xml file do?", listOf("Stores the user's photos", "Compiles Kotlin code", "Declares the app's components and permissions", "Charges the phone battery"), "Declares the app's components and permissions"),
            QuizQuestion("What is an Activity?", listOf("A phone battery setting", "A type of file system", "A network cable", "A single screen with a user interface"), "A single screen with a user interface")
        )
        "Activities and Lifecycle" -> listOf(
            QuizQuestion("What is the activity lifecycle?", listOf("The states an activity moves through from creation to destruction", "How long a phone battery lasts", "How many apps are installed", "The warranty period of a phone"), "The states an activity moves through from creation to destruction"),
            QuizQuestion("Which method is called when an activity is first created?", listOf("onDestroy()", "onCreate()", "onStop()", "onPause()"), "onCreate()"),
            QuizQuestion("Which method is called when an activity becomes visible to the user?", listOf("onDestroy()", "onStop()", "onStart()", "onPause()"), "onStart()"),
            QuizQuestion("What does onDestroy() do?", listOf("It is called when the activity first appears", "It is called when the user taps a button", "It is called when the app is installed", "It is called before the activity is destroyed, to clean up resources"), "It is called before the activity is destroyed, to clean up resources")
        )
        "Jetpack Compose" -> listOf(
            QuizQuestion("What is Jetpack Compose?", listOf("Android's modern toolkit for building user interfaces with Kotlin", "A database system", "A web browser", "An image editor"), "Android's modern toolkit for building user interfaces with Kotlin"),
            QuizQuestion("What does @Composable mean?", listOf("It deletes a function", "It marks a function that describes part of the user interface", "It connects the app to the internet", "It creates a database table"), "It marks a function that describes part of the user interface"),
            QuizQuestion("What is state in Compose?", listOf("A country in an address", "A permanent file", "A value that can change and updates the screen", "A hardware component"), "A value that can change and updates the screen"),
            QuizQuestion("What does remember do?", listOf("It saves a file to disk permanently", "It sends a message to the user", "It closes the app", "It keeps a value when the screen is redrawn"), "It keeps a value when the screen is redrawn")
        )
        "Android UI Design" -> listOf(
            QuizQuestion("What is Material Design?", listOf("Google's design system with guidelines and components", "A type of phone case", "A database tool", "A programming language"), "Google's design system with guidelines and components"),
            QuizQuestion("Why do we use dp for sizes?", listOf("It measures battery life", "It keeps sizes consistent across different screens", "It is a colour code", "It measures internet speed"), "It keeps sizes consistent across different screens"),
            QuizQuestion("What does a Column do in Compose?", listOf("It places items side by side", "It deletes items", "It places items vertically, one after another", "It draws a circle"), "It places items vertically, one after another"),
            QuizQuestion("Why is consistent spacing important?", listOf("It makes the app larger in storage", "It connects to the internet faster", "It hides the buttons", "It makes the interface neat and easy to read"), "It makes the interface neat and easy to read")
        )
        else -> listOf(
            QuizQuestion("No quiz questions available.", listOf("Please choose another topic."), "Please choose another topic.")
        )
    }
}