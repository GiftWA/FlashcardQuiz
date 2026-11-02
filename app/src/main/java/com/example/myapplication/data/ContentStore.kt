package com.example.myapplication.data

// Holds the courses made from imported study material for as long as the app is open.
// getTopics, getFlashcards and getQuizQuestions look here before the built-in sample data.
object ContentStore {
    private val courses = mutableListOf<GeneratedCourse>()

    fun add(course: GeneratedCourse) {
        courses.removeAll { it.name.equals(course.name, ignoreCase = true) }
        courses.add(course)
    }

    fun topicsFor(course: String): List<String>? =
        courses.find { it.name.equals(course, ignoreCase = true) }?.topics?.map { it.name }

    fun flashcardsFor(topic: String): List<Flashcard>? =
        courses.flatMap { it.topics }.find { it.name.equals(topic, ignoreCase = true) }?.flashcards

    fun quizFor(topic: String): List<QuizQuestion>? =
        courses.flatMap { it.topics }.find { it.name.equals(topic, ignoreCase = true) }?.quiz

    fun clear() = courses.clear()
}
