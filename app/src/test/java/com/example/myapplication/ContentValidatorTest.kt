package com.example.myapplication

import com.example.myapplication.data.ContentValidator
import com.example.myapplication.data.Flashcard
import com.example.myapplication.data.QuizQuestion
import com.example.myapplication.data.getFlashcards
import com.example.myapplication.data.getQuizQuestions
import com.example.myapplication.data.getTopics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Tests for the checks that decide whether quiz, flashcard and topic content is safe to show.
class ContentValidatorTest {

    // A small course resource, like the notes the content is generated from
    private val resource = """
        Database Management
        Topic: Normalization
        Normalization is the process of organising data to reduce duplication.
        A primary key uniquely identifies each row in a table.
    """.trimIndent()

    private fun goodQuestion(n: Int) = QuizQuestion(
        question = "Question number $n about keys?",
        options = listOf("Option A$n", "Option B$n", "Option C$n", "Option D$n"),
        correctAnswer = "Option A$n",
        evidence = "A primary key uniquely identifies each row in a table."
    )

    private fun goodQuiz() = (1..4).map { goodQuestion(it) }

    // ---- Built-in sample data must follow the same rules ----

    @Test
    fun sampleQuizzesAreAllValid() {
        for (course in listOf("Database Management", "Computer Networks", "Operating Systems", "Mobile Application Development")) {
            for (topic in getTopics(course)) {
                assertEquals("Quiz for $topic", emptyList<String>(), ContentValidator.validateQuiz(getQuizQuestions(topic)))
            }
        }
    }

    @Test
    fun sampleFlashcardsAreAllValid() {
        for (course in listOf("Database Management", "Computer Networks", "Operating Systems", "Mobile Application Development")) {
            for (topic in getTopics(course)) {
                assertEquals("Flashcards for $topic", emptyList<String>(), ContentValidator.validateFlashcards(getFlashcards(topic)))
            }
        }
    }

    // ---- Quiz questions ----

    @Test
    fun wellFormedQuizPasses() {
        assertTrue(ContentValidator.validateQuiz(goodQuiz(), resource).isEmpty())
    }

    @Test
    fun correctAnswerMissingFromOptionsIsRejected() {
        val bad = goodQuestion(1).copy(correctAnswer = "Something else")
        assertTrue(ContentValidator.validateQuestion(bad).contains("Correct answer is not one of the options"))
    }

    @Test
    fun threeOptionsAreRejected() {
        val bad = goodQuestion(1).copy(options = listOf("A", "B", "C"), correctAnswer = "A")
        assertFalse(ContentValidator.validateQuestion(bad).isEmpty())
    }

    @Test
    fun repeatedOptionsAreRejected() {
        val bad = goodQuestion(1).copy(options = listOf("Same", "same", "B", "C"), correctAnswer = "B")
        assertTrue(ContentValidator.validateQuestion(bad).contains("Two options are the same"))
    }

    @Test
    fun emptyQuestionTextIsRejected() {
        val bad = goodQuestion(1).copy(question = "  ")
        assertTrue(ContentValidator.validateQuestion(bad).contains("Question text is empty"))
    }

    @Test
    fun quizWithWrongNumberOfQuestionsIsRejected() {
        assertFalse(ContentValidator.validateQuiz(goodQuiz().take(3)).isEmpty())
    }

    @Test
    fun duplicateQuestionsAreRejected() {
        val quiz = goodQuiz().toMutableList()
        quiz[1] = quiz[1].copy(question = quiz[0].question)
        assertTrue(ContentValidator.validateQuiz(quiz).contains("The same question appears twice"))
    }

    // ---- Grounding: content must come from the resource ----

    @Test
    fun questionWithInventedEvidenceIsRejected() {
        val invented = goodQuestion(1).copy(evidence = "Primary keys were invented on Mars in 1850.")
        val problems = ContentValidator.validateQuestion(invented, resource)
        assertTrue(problems.contains("Evidence sentence was not found in the course resource"))
    }

    @Test
    fun evidenceMatchingIgnoresCaseAndSpacing() {
        assertTrue(ContentValidator.isInResource("a  PRIMARY key uniquely identifies   each row", resource))
    }

    @Test
    fun blankEvidenceIsNotAccepted() {
        assertFalse(ContentValidator.isInResource("   ", resource))
    }

    // ---- Flashcards ----

    @Test
    fun wellFormedFlashcardsPass() {
        val cards = listOf(
            Flashcard("What is normalization?", "Organising data to reduce duplication.", "Normalization is the process of organising data to reduce duplication."),
            Flashcard("What does a primary key do?", "It uniquely identifies each row.", "A primary key uniquely identifies each row in a table.")
        )
        assertTrue(ContentValidator.validateFlashcards(cards, resource).isEmpty())
    }

    @Test
    fun flashcardWithEmptyAnswerIsRejected() {
        val problems = ContentValidator.validateFlashcards(listOf(Flashcard("What is SQL?", "")))
        assertTrue(problems.contains("Card 1: answer is empty"))
    }

    @Test
    fun flashcardThatRepeatsTheQuestionIsRejected() {
        val problems = ContentValidator.validateFlashcards(listOf(Flashcard("What is SQL?", "what is sql?")))
        assertTrue(problems.contains("Card 1: answer repeats the question"))
    }

    @Test
    fun emptyFlashcardListIsRejected() {
        assertFalse(ContentValidator.validateFlashcards(emptyList()).isEmpty())
    }

    @Test
    fun flashcardWithInventedEvidenceIsRejected() {
        val cards = listOf(Flashcard("What is X?", "Y.", "This sentence is not in the notes."))
        assertFalse(ContentValidator.validateFlashcards(cards, resource).isEmpty())
    }

    // ---- Topics ----

    @Test
    fun topicsThatAppearInTheResourcePass() {
        assertTrue(ContentValidator.validateTopics("Database Management", listOf("Normalization"), resource).isEmpty())
    }

    @Test
    fun inventedTopicIsRejected() {
        val problems = ContentValidator.validateTopics("Database Management", listOf("Normalization", "Quantum Databases"), resource)
        assertEquals(listOf("Topic \"Quantum Databases\" is not in the resource"), problems)
    }

    @Test
    fun inventedCourseIsRejected() {
        val problems = ContentValidator.validateTopics("Astrology", listOf("Normalization"), resource)
        assertTrue(problems.contains("Course \"Astrology\" is not in the resource"))
    }

    @Test
    fun emptyTopicListIsRejected() {
        assertFalse(ContentValidator.validateTopics("Database Management", emptyList(), resource).isEmpty())
    }
}
