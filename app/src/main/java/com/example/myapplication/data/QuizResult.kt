package com.example.myapplication.data

// One finished quiz attempt, kept in memory while the app is open.
data class QuizResult(
    val course: String,
    val topic: String,
    val score: Int,
    val total: Int
)
