package com.example.myapplication.data

// A course made from study material the student imported.
data class GeneratedTopic(
    val name: String,
    val flashcards: List<Flashcard>,
    val quiz: List<QuizQuestion>
)

data class GeneratedCourse(
    val name: String,
    val category: String,
    val topics: List<GeneratedTopic>
)
