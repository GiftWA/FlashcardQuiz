package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private val allCourses = listOf(
    "Database Management",
    "Computer Networks",
    "Operating Systems",
    "Mobile Application Development"
)
private val allTopics = allCourses.flatMap { getTopics(it) }

// ---------- Topics ----------
class TopicDataTest {

    @Test
    fun everyCourseHasFourTopics() {
        allCourses.forEach { course ->
            assertEquals("Topics for $course", 4, getTopics(course).size)
        }
    }

    @Test
    fun databaseManagementTopicsAreCorrect() {
        assertEquals(
            listOf("Database Fundamentals", "ER Diagrams", "Relational Model", "Normalization"),
            getTopics("Database Management")
        )
    }

    @Test
    fun unknownCourseHasNoTopics() {
        assertTrue(getTopics("Astrology").isEmpty())
    }

    @Test
    fun topicNamesAreUniqueAcrossAllCourses() {
        assertEquals(allTopics.size, allTopics.toSet().size)
    }
}

// ---------- Flashcards ----------
class FlashcardDataTest {

    @Test
    fun everyTopicHasFourFlashcards() {
        allTopics.forEach { topic ->
            assertEquals("Flashcards for $topic", 4, getFlashcards(topic).size)
        }
    }

    @Test
    fun noFlashcardHasABlankQuestionOrAnswer() {
        allTopics.forEach { topic ->
            getFlashcards(topic).forEach { card ->
                assertTrue("Blank question in $topic", card.question.isNotBlank())
                assertTrue("Blank answer in $topic", card.answer.isNotBlank())
            }
        }
    }

    @Test
    fun firstNormalizationCardIsAboutNormalization() {
        val first = getFlashcards("Normalization").first()
        assertEquals("What is normalization?", first.question)
    }

    @Test
    fun unknownTopicGivesASingleFallbackCard() {
        val cards = getFlashcards("Not a real topic")
        assertEquals(1, cards.size)
        assertEquals("No flashcards available.", cards[0].question)
    }
}

// ---------- Quiz questions ----------
class QuizDataTest {

    @Test
    fun everyTopicHasFourQuestions() {
        allTopics.forEach { topic ->
            assertEquals("Questions for $topic", 4, getQuizQuestions(topic).size)
        }
    }

    @Test
    fun everyQuestionHasFourDistinctOptions() {
        allTopics.forEach { topic ->
            getQuizQuestions(topic).forEach { q ->
                assertEquals("Options for '${q.question}'", 4, q.options.size)
                assertEquals("Duplicate options in '${q.question}'", 4, q.options.toSet().size)
            }
        }
    }

    @Test
    fun correctAnswerIsAlwaysOneOfTheOptions() {
        allTopics.forEach { topic ->
            getQuizQuestions(topic).forEach { q ->
                assertTrue(
                    "Correct answer missing from options in '${q.question}'",
                    q.options.contains(q.correctAnswer)
                )
            }
        }
    }

    @Test
    fun firstDatabaseQuestionHasTheRightAnswer() {
        val q = getQuizQuestions("Database Fundamentals").first()
        assertEquals("What is a primary key?", q.question)
        assertEquals("A key that uniquely identifies each row", q.correctAnswer)
        assertTrue(q.options.all { it.isNotBlank() })
    }

    @Test
    fun unknownTopicGivesASingleFallbackQuestion() {
        val questions = getQuizQuestions("Not a real topic")
        assertEquals(1, questions.size)
        assertEquals("No quiz questions available.", questions[0].question)
    }
}
