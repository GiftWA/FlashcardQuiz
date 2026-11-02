package com.example.myapplication.data

// Checks content before the app shows it. The same checks are used on the built-in
// sample data and on any quiz, flashcards or topics that are generated from a course
// resource, so wrong or badly formed content is rejected instead of being shown.
// Every function returns a list of problems. An empty list means the content is valid.
object ContentValidator {

    const val QUESTIONS_PER_QUIZ = 4
    const val OPTIONS_PER_QUESTION = 4

    private fun clean(text: String): String =
        text.lowercase().replace(Regex("\\s+"), " ").trim()

    // True when the text appears in the resource (ignoring case and extra spaces)
    fun isInResource(text: String, resource: String): Boolean =
        text.isNotBlank() && clean(resource).contains(clean(text))

    fun validateQuestion(q: QuizQuestion, resource: String? = null): List<String> {
        val problems = mutableListOf<String>()
        if (q.question.isBlank()) problems.add("Question text is empty")
        if (q.options.size != OPTIONS_PER_QUESTION) {
            problems.add("Expected $OPTIONS_PER_QUESTION options but found ${q.options.size}")
        }
        if (q.options.any { it.isBlank() }) problems.add("An option is empty")
        if (q.options.map { clean(it) }.distinct().size != q.options.size) {
            problems.add("Two options are the same")
        }
        val matches = q.options.count { clean(it) == clean(q.correctAnswer) }
        if (matches == 0) problems.add("Correct answer is not one of the options")
        if (matches > 1) problems.add("Correct answer appears more than once")
        if (resource != null && !isInResource(q.evidence, resource)) {
            problems.add("Evidence sentence was not found in the course resource")
        }
        return problems
    }

    fun validateQuiz(questions: List<QuizQuestion>, resource: String? = null): List<String> {
        val problems = mutableListOf<String>()
        if (questions.size != QUESTIONS_PER_QUIZ) {
            problems.add("Expected $QUESTIONS_PER_QUIZ questions but found ${questions.size}")
        }
        if (questions.map { clean(it.question) }.distinct().size != questions.size) {
            problems.add("The same question appears twice")
        }
        questions.forEachIndexed { i, q ->
            validateQuestion(q, resource).forEach { problems.add("Question ${i + 1}: $it") }
        }
        return problems
    }

    fun validateFlashcards(cards: List<Flashcard>, resource: String? = null): List<String> {
        val problems = mutableListOf<String>()
        if (cards.isEmpty()) problems.add("No flashcards were provided")
        if (cards.map { clean(it.question) }.distinct().size != cards.size) {
            problems.add("The same flashcard question appears twice")
        }
        cards.forEachIndexed { i, c ->
            if (c.question.isBlank()) problems.add("Card ${i + 1}: question is empty")
            if (c.answer.isBlank()) problems.add("Card ${i + 1}: answer is empty")
            if (clean(c.question) == clean(c.answer)) problems.add("Card ${i + 1}: answer repeats the question")
            if (resource != null && !isInResource(c.evidence, resource)) {
                problems.add("Card ${i + 1}: evidence sentence was not found in the course resource")
            }
        }
        return problems
    }

    // Every course and topic name must come from the resource, not be invented
    fun validateTopics(course: String, topics: List<String>, resource: String): List<String> {
        val problems = mutableListOf<String>()
        if (!isInResource(course, resource)) problems.add("Course \"$course\" is not in the resource")
        if (topics.isEmpty()) problems.add("No topics were provided for $course")
        if (topics.map { clean(it) }.distinct().size != topics.size) problems.add("A topic appears twice")
        topics.filterNot { isInResource(it, resource) }.forEach {
            problems.add("Topic \"$it\" is not in the resource")
        }
        return problems
    }
}
